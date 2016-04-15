package com.reizes.shiva2.kafka.extractor;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.consumer.OffsetCommitCallback;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.core.extractor.AbstractExtractor;

public class KafkaConsumeExtractor extends AbstractExtractor implements ConsumerRebalanceListener, AfterProcessAware, ProcessContextAware {

	private KafkaConsumer<String, String> consumer;
	private String topic;
	private String messageKey;
	private long pollingTimeout = 1000*5; // 5 seconds
	private int threadJobQueueCapacity = 100;
	private ProcessContext context;
	private KafkaConsumerController controller = new KafkaConsumerController();
	private KafkaOffsetListener kafkaOffsetListener;
	private PollingTimeoutListener pollingTimeoutListener;
	private OffsetStorage offsetStorage;
	//private MessageProcessThread thread;
	private AtomicBoolean isContinueConsume = new AtomicBoolean(true);
	private ConcurrentHashMap<TopicPartition, OffsetAndMetadata> currentOffset = new ConcurrentHashMap<>();

	public KafkaConsumeExtractor(Map<java.lang.String, java.lang.Object> configs) {
		consumer = new KafkaConsumer<>(configs);
		topic = (String) configs.get("topic");
		messageKey = (String) configs.get("messageKey");
		addShutdownHook();
	}

	public KafkaConsumeExtractor(Properties properties) {
		consumer = new KafkaConsumer<>(properties);
		topic = properties.getProperty("topic");
		messageKey = properties.getProperty("messageKey");
		addShutdownHook();
	}
	
	/*private class MessageProcessThread extends Thread {
		private LinkedBlockingQueue<ConsumerRecord<String, String>> queue;
		private AtomicBoolean isContinue = new AtomicBoolean(true);
		
		public MessageProcessThread(int queueCapacity) {
			queue = new LinkedBlockingQueue<ConsumerRecord<String, String>>(queueCapacity);
		}
		
		@Override
		public void run() {
			long count = 0;
			do {
				try {
					ConsumerRecord<String, String> record = queue.take();
					if (record==null) {
						continue;
					}
					String key = record.key();
					String value = record.value();
					if (messageKey == null || messageKey.equals(key)) {
						startProcessItem(value);
					}
					count++;
					if (count%100 == 0) {
						commit();
						count = 0;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (context.getExecutionStatus() == ExecutionStatus.STOP) break;
			} while(isContinue.get());
			try {
				queue.clear();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}*/
	
	private void addShutdownHook() {
		final Thread mainThread = Thread.currentThread();

        // Registering a shutdown hook so we can exit cleanly
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                System.out.println("Starting exit...");
                //thread.isContinue.set(false);
                isContinueConsume.set(false);
                context.setExecutionStatus(ExecutionStatus.STOP);
                /*try {
					thread.queue.put(new ConsumerRecord<String, String>("topic",0 ,0l, "",""));
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}*/
                // Note that shutdownhook runs in a separate thread, so the only thing we can safely do to a consumer is wake it up
                consumer.wakeup();
                try {
                	System.out.println("wait join...");
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	private void commit() {
		consumer.commitSync(currentOffset);
		saveOffset(currentOffset.keySet(), currentOffset);
	}
	
	private void saveOffset(Collection<TopicPartition> partitions, Map<TopicPartition, OffsetAndMetadata> offsets) {
		if (offsetStorage != null) {
			for(TopicPartition partition : partitions) {
				OffsetAndMetadata offset = offsets.get(partition);
				offsetStorage.saveOffset(partition.topic(), partition.partition(), offset!=null?offset.offset():0);
			}
		}
	}
	
	private void seek(Collection<TopicPartition> partitions) {
		if (offsetStorage != null) {
			for (TopicPartition partition : partitions) {
				consumer.seek(partition, offsetStorage.loadOffset(partition.topic(), partition.partition()));
			}
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		/*if (thread == null) {
			thread = new MessageProcessThread(threadJobQueueCapacity);
			thread.start();
		}*/
		
		try {
			consumer.subscribe(Arrays.asList(topic), this);
			consumer.poll(0);
			seek(consumer.assignment());
			long count = 0;
polling: 
			while (isContinueConsume.get()) {
				ConsumerRecords<String, String> records = consumer.poll(pollingTimeout);
				
				if (pollingTimeoutListener!=null && (records==null || records.count()==0)) {
					pollingTimeoutListener.onPollingTimeout();
					continue;
				}
				
				for (ConsumerRecord<String, String> record : records) {
					currentOffset.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
					
					if (kafkaOffsetListener!=null) {
						kafkaOffsetListener.setOffsetAndRecord(record.topic(), record.partition(), record.offset(), record.value());
					}
					KafkaControlCommand cmd = controller.parseCommand(record);
					if (cmd != null) {
						switch (cmd) {
						case WAKEUP:
							break;
						case STOP:
							commit();
							break polling;
						}
					} else {
						//this.thread.queue.put(record);
						String key = record.key();
						String value = record.value();
						if (messageKey == null || messageKey.equals(key)) {
							startProcessItem(value);
						}
						count++;
						if (count%100 == 0) {
							commit();
							count = 0;
						}
					}
				}
				commit();
				count = 0;
				if (context.getExecutionStatus() == ExecutionStatus.STOP) break;
			}
		} catch (WakeupException e) {
            // ignore for shutdown
        } finally {
			try {
		        consumer.commitSync();
		    } finally {
		        consumer.close();
		    }
		}
		return input;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
	}

	public String getTopic() {
		return topic;
	}

	public KafkaConsumeExtractor setTopic(String topic) {
		this.topic = topic;
		return this;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public KafkaConsumeExtractor setMessageKey(String messageKey) {
		this.messageKey = messageKey;
		return this;
	}

	public long getPollingTimeout() {
		return pollingTimeout;
	}

	public KafkaConsumeExtractor setPollingTimeout(long pollingTimeout) {
		this.pollingTimeout = pollingTimeout;
		return this;
	}

	public KafkaConsumer<String, String> getConsumer() {
		return consumer;
	}

	public KafkaOffsetListener getKafkaOffsetListener() {
		return kafkaOffsetListener;
	}

	public KafkaConsumeExtractor setKafkaOffsetListener(KafkaOffsetListener kafkaOffsetListener) {
		this.kafkaOffsetListener = kafkaOffsetListener;
		return this;
	}

	public PollingTimeoutListener getPollingTimeoutListener() {
		return pollingTimeoutListener;
	}

	public KafkaConsumeExtractor setPollingTimeoutListener(PollingTimeoutListener pollingTimeoutListener) {
		this.pollingTimeoutListener = pollingTimeoutListener;
		return this;
	}

	@Override
	public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
		saveOffset(partitions, currentOffset);
		//commitDBTransaction();
	}

	@Override
	public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
		seek(partitions);
	}

	public OffsetStorage getOffsetStorage() {
		return offsetStorage;
	}

	public KafkaConsumeExtractor setOffsetStorage(OffsetStorage offsetStorage) {
		this.offsetStorage = offsetStorage;
		return this;
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		this.context = context;
	}

	public int getThreadJobQueueCapacity() {
		return threadJobQueueCapacity;
	}

	public KafkaConsumeExtractor setThreadJobQueueCapacity(int threadJobQueueCapacity) {
		this.threadJobQueueCapacity = threadJobQueueCapacity;
		return this;
	}

}

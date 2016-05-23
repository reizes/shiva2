package com.reizes.shiva2.kafka.extractor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.CommitFailedException;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.management.AbstractNotificatableExtractor;
import com.reizes.shiva2.management.Managable;

public class KafkaConsumeExtractor extends AbstractNotificatableExtractor implements KafkaConsumeExtractorMBean, Managable, ConsumerRebalanceListener, AfterProcessAware, ProcessContextAware {

	private KafkaConsumeExtractorStatus status = new KafkaConsumeExtractorStatus();
	private KafkaConsumer<String, String> consumer;
	private long pollingTimeout = 1000*5; // 5 seconds
	private int threadJobQueueCapacity = 100;
	private ProcessContext context;
	private KafkaConsumerController controller = new KafkaConsumerController();
	private KafkaOffsetListener kafkaOffsetListener;
	private PollingTimeoutListener pollingTimeoutListener;
	private OffsetStorage offsetStorage;
	private AtomicBoolean isContinueConsume = new AtomicBoolean(true);
	private ConcurrentHashMap<TopicPartition, OffsetAndMetadata> currentOffset = new ConcurrentHashMap<>();
	private List<Integer> assignPartitions;

	public KafkaConsumeExtractor(Map<java.lang.String, java.lang.Object> configs) {
		consumer = new KafkaConsumer<>(configs);
		status.setConsumeTopic((String) configs.get("topic"));
		status.setConsumeMessageKey((String) configs.get("messageKey"));
		addShutdownHook();
	}

	public KafkaConsumeExtractor(Properties properties) {
		consumer = new KafkaConsumer<>(properties);
		status.setConsumeTopic(properties.getProperty("topic"));
		status.setConsumeMessageKey(properties.getProperty("messageKey"));
		addShutdownHook();
	}
	
	private void addShutdownHook() {
		final Thread mainThread = Thread.currentThread();

        // Registering a shutdown hook so we can exit cleanly
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
            	status.setStatus("Starting exit");
                System.out.println("Starting exit...");
                isContinueConsume.set(false);
                context.setExecutionStatus(ExecutionStatus.STOP);
                consumer.wakeup();
                try {
                	status.setStatus("Wait join");
                	System.out.println("wait join...");
                    mainThread.join();
                	status.setStatus("Exited");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
	}
	
	private void commit() {
		try {
			consumer.commitSync(currentOffset);
			saveOffset(currentOffset.keySet(), currentOffset);
		} catch(CommitFailedException e) {
			sendNotification(e);
            System.out.println(e.getMessage());
            //e.printStackTrace();
		}
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
				long offset = offsetStorage.loadOffset(partition.topic(), partition.partition());
				System.out.println(partition.toString()+" : "+offset);
				consumer.seek(partition, offset);
			}
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		try {
			if (assignPartitions != null) {
				List<TopicPartition> partitions = new ArrayList<>();
				for(Integer partition : assignPartitions) {
					partitions.add(new TopicPartition(status.getConsumeTopic(), partition));
				}
				consumer.assign(partitions);
				seek(partitions);
			} else {
				consumer.subscribe(Arrays.asList(status.getConsumeTopic()), this);
				consumer.poll(0);
				seek(consumer.assignment());
			}
			status.setStatus("Start consumer");
			String consumeMessageKey = status.getConsumeMessageKey();
			long count = 0;
polling: 
			while (isContinueConsume.get()) {
				ConsumerRecords<String, String> records = consumer.poll(pollingTimeout);
				
				if (pollingTimeoutListener!=null && (records==null || records.count()==0)) {
					status.setStatus("POLLING TIMEOUT");
					pollingTimeoutListener.onPollingTimeout();
					continue;
				}
				
				status.setStatus("MESSAGE RECEIVED");
				for (ConsumerRecord<String, String> record : records) {
					currentOffset.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset()));
					status.setTopic(record.topic());
					status.getPartitions().add(record.partition());
					status.setOffset(record.offset());
					status.setMessageKey(record.key());
					status.setMessage(record.value());
					//System.out.println(record.topic()+"-"+record.partition()+":"+record.offset()+" "+record.key());
					
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
						if (consumeMessageKey == null || consumeMessageKey.equals(key)) {
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
			status.setStatus("Exit Consumer Loop");
		} catch (WakeupException e) {
            // ignore for shutdown
			sendNotification(e);
        } finally {
			try {
		        consumer.commitSync();
			}
	        catch (WakeupException e) {
	        	
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
		return status.getTopic();
	}

	public KafkaConsumeExtractor setTopic(String topic) {
		this.status.setTopic(topic);
		return this;
	}

	public String getMessageKey() {
		return status.getMessageKey();
	}

	public KafkaConsumeExtractor setMessageKey(String messageKey) {
		status.setMessageKey(messageKey);
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
		commit();
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

	public List<Integer> getAssignPartitions() {
		return assignPartitions;
	}

	public KafkaConsumeExtractor setAssignPartitions(List<Integer> assignPartitions) {
		this.assignPartitions = assignPartitions;
		return this;
	}

	@Override
	public String getStatus() {
		return status.getStatus();
	}

	@Override
	public long getOffset() {
		return status.getOffset();
	}

	@Override
	public String getPartitions() {
		return StringUtils.join(status.getPartitions().toArray(), ',');
	}

	@Override
	public String getMessage() {
		return status.getMessage();
	}

	@Override
	public String getConsumeTopic() {
		return status.getConsumeTopic();
	}

	@Override
	public String getConsumeMessageKey() {
		return status.getConsumeMessageKey();
	}

	@Override
	public void stopConsumer() {
    	status.setStatus("Starting exit");
        System.out.println("Starting exit...");
        isContinueConsume.set(false);
        context.setExecutionStatus(ExecutionStatus.STOP);
        consumer.wakeup();
	}

	@Override
	public void registerMBean(MBeanServer mbeanServer) throws Exception{
		ObjectName mbeanName = new ObjectName("shiva2.kafka:type=KafkaConsumeExtractor");
		mbeanServer.registerMBean(this, mbeanName);
	}

}

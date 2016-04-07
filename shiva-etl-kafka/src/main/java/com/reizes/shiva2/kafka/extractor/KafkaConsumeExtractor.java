package com.reizes.shiva2.kafka.extractor;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.reader.AbstractExtractor;

public class KafkaConsumeExtractor extends AbstractExtractor implements AfterProcessAware {

	private KafkaConsumer<String, String> consumer;
	private String topic;
	private String messageKey;
	private long pollingTimeout = 1000 * 60 * 60; // 1 hour
	private KafkaConsumerController controller = new KafkaConsumerController();

	public KafkaConsumeExtractor(Map<java.lang.String, java.lang.Object> configs) {
		consumer = new KafkaConsumer<>(configs);
		topic = (String) configs.get("topic");
		messageKey = (String) configs.get("messageKey");
	}

	public KafkaConsumeExtractor(Properties properties) {
		consumer = new KafkaConsumer<>(properties);
		topic = properties.getProperty("topic");
		messageKey = properties.getProperty("messageKey");
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		try {
			consumer.subscribe(Arrays.asList(topic));
longpolling: 
			while (true) {
				ConsumerRecords<String, String> records = consumer.poll(pollingTimeout);
				for (ConsumerRecord<String, String> record : records) {
					KafkaControlCommand cmd = controller.parseCommand(record);
					if (cmd != null) {
						switch (cmd) {
						case WAKEUP:
							break;
						case STOP:
							consumer.commitSync();
							break longpolling;
						}
					} else {
						String key = record.key();
						String value = record.value();
						if (messageKey == null || messageKey.equals(key)) {
							startProcessItem(value);
						}
					}
				}
				consumer.commitSync();
			}
		} finally {
			consumer.close();
		}
		return input;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public String getMessageKey() {
		return messageKey;
	}

	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	public long getPollingTimeout() {
		return pollingTimeout;
	}

	public void setPollingTimeout(long pollingTimeout) {
		this.pollingTimeout = pollingTimeout;
	}

	public KafkaConsumer<String, String> getConsumer() {
		return consumer;
	}

}

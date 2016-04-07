package com.reizes.shiva2.kafka.loader;

import java.util.Map;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.loader.AbstractLoader;

public class KafkaProduceLoader extends AbstractLoader implements AfterProcessAware {
	private KafkaProducer<String, Object> producer;
	private String topic;
	private String messageKey;

	public KafkaProduceLoader(Map<java.lang.String, java.lang.Object> configs) {
		producer = new KafkaProducer<>(configs);
		topic = (String) configs.get("topic");
		messageKey = (String) configs.get("messageKey");
	}

	public KafkaProduceLoader(Properties properties) {
		producer = new KafkaProducer<>(properties);
		topic = properties.getProperty("topic");
		messageKey = properties.getProperty("messageKey");
	}
	
	@Override
	public Object doProcess(Object input) throws Exception {
		producer.send(new ProducerRecord<String, Object>(topic, messageKey, input));
		return input;
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		producer.close();
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

	public KafkaProducer<String, Object> getProducer() {
		return producer;
	}

}

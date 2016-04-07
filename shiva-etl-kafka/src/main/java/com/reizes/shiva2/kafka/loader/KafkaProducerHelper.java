package com.reizes.shiva2.kafka.loader;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducerHelper implements Closeable {
	private Producer<String, String> kafkaProducer;
	
	public KafkaProducerHelper(Map<String, Object> kafkaConfigMap) {
		initKafkaProducer(kafkaConfigMap);
	}

	private void initKafkaProducer(Map<String, Object> kafkaConfigMap) {
		 this.kafkaProducer = new KafkaProducer<>(kafkaConfigMap);
	}
	
	public void send(String topic, String key, String message) {
		kafkaProducer.send(new ProducerRecord<String, String>(topic, key, message));
	}

	@Override
	public void close() throws IOException {
		kafkaProducer.close();
	}
}

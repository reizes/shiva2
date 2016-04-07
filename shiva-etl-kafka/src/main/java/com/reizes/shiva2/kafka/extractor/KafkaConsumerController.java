package com.reizes.shiva2.kafka.extractor;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public class KafkaConsumerController {
	private static final String CONTROL_KEY = "shiva-kafka-control";
	
	public KafkaControlCommand parseCommand(ConsumerRecord<String, String> record) {
		if (record!=null) {
			String key = record.key();
			if (key!=null && key.equals(CONTROL_KEY)) {
				return KafkaControlCommand.fromMessage(record.value());
			}
		}
		
		return null;
	}
}

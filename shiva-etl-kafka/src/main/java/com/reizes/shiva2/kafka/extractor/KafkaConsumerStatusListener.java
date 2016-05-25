package com.reizes.shiva2.kafka.extractor;

public interface KafkaConsumerStatusListener {
	public void onKafkaConsumerStatusChanged(KafkaConsumerStatus oldStatus, KafkaConsumerStatus newStatus);
}

package com.reizes.shiva2.kafka.extractor;

public interface KafkaOffsetListener {
	public void setOffsetAndRecord(String topic, int partition, long offset, String value);
}

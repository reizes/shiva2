package com.reizes.shiva2.kafka.extractor;

public interface OffsetStorage {
	public void saveOffset(String topic, int partition, long offset);
	public long loadOffset(String topic, int partition);
}

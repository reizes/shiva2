package com.reizes.shiva2.kafka.extractor;

public interface KafkaConsumeExtractorMBean {
	public String getStatusString();
	public KafkaConsumerStatus getStatus();
	public long getOffset();
	public String getPartitions();
	public String getTopic();
	public String getMessageKey();
	public String getConsumeTopic();
	public String getConsumeMessageKey();
	public String getMessage();
	public void stopConsumer();
}

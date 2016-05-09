package com.reizes.shiva2.kafka.extractor;

import java.util.HashSet;
import java.util.Set;

import lombok.Data;

@Data
public class KafkaConsumeExtractorStatus {
	private String status;
	private long offset;
	private String topic;
	private String messageKey;
	private String message;
	private String consumeMessageKey;
	private String consumeTopic;
	private Set<Integer> partitions = new HashSet<>();
}

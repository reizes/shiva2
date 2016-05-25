package com.reizes.shiva2.kafka.extractor;

import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

public class KafkaConsumeExtractorStatus {
	@Getter
	@Setter
	private KafkaConsumerStatusListener kafkaConsumerStatusListener;
	@Getter
	@Setter
	private String statusString;
	@Getter
	private KafkaConsumerStatus status = KafkaConsumerStatus.PREPARE;
	@Getter
	@Setter
	private long offset;
	@Getter
	@Setter
	private String topic;
	@Getter
	@Setter
	private String messageKey;
	@Getter
	@Setter
	private String message;
	@Getter
	@Setter
	private String consumeMessageKey;
	@Getter
	@Setter
	private String consumeTopic;
	@Getter
	@Setter
	private Set<Integer> partitions = new HashSet<>();
	
	public void setStatus(KafkaConsumerStatus status) {
		if (!this.status.equals(status)) {
			KafkaConsumerStatus oldStatus = this.status;
			this.status = status;
			if (kafkaConsumerStatusListener != null) {
				kafkaConsumerStatusListener.onKafkaConsumerStatusChanged(oldStatus, status);
			}
		}
	}
}

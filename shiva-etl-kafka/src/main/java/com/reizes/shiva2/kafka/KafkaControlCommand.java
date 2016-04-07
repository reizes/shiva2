package com.reizes.shiva2.kafka;

public enum KafkaControlCommand {
	WAKEUP("CMD:__WAKEUP__"),
	STOP("CMD:__STOP__");
	
	private String cmd;
	
	KafkaControlCommand(String cmd) {
		this.cmd = cmd;
	}
	
	public static KafkaControlCommand fromMessage(String msg) {
		for(KafkaControlCommand cmdObject : KafkaControlCommand.values()) {
			if (cmdObject.cmd.equals(msg)) {
				return cmdObject;
			}
		}
		
		return null;
	}
}

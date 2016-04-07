package com.reizes.shiva2.core;

public enum ExecutionStatus {
	SKIP(-1),	// Skip Current Item Process (go next item)
	STOP(-2),	// Stop Etl Process
	CONTINUE(-3); // Normal. continue process
	
	int status;
	ExecutionStatus(int status) {
		this.status=status;
	}
}

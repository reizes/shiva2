package com.reizes.shiva2.etl.core;

public enum ProcessStatus {
	RUNNING(0),	// process is running
	FINISHED(1),	// process is finished 
	INTERRUPTED(2), // interrupted
	FAILED(3),	 // process is failed
	UNKNOWN(4); // unknown status
	
	int status;
	ProcessStatus(int status) {
		this.status=status;
	}
}

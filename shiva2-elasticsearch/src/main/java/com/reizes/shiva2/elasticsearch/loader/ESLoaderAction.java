package com.reizes.shiva2.elasticsearch.loader;

public enum ESLoaderAction {
	DELETE("delete"),
	INDEX("index"),
	UPDATE("update"),
	UPSERT("upsert");
	
	private final String action;
	
	private ESLoaderAction(String action) {
		this.action = action;
	}
	
	public static ESLoaderAction from(String value) {
		for(ESLoaderAction action : ESLoaderAction.values()) {
			if (action.toString().equals(value.toLowerCase())) {
				return action;
			}
		}
		
		return null;
	}
	
	@Override
	public String toString() {
		return action;
	}
}

package com.reizes.shiva2.management;

public interface Notificatable {
	public void sendNotification(String msg, String attributeName, String attributeType, Object oldValue, Object newValue);
	public void sendNotification(Throwable e);
}

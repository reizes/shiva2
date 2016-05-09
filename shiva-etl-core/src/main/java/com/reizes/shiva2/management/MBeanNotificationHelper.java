package com.reizes.shiva2.management;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.NotificationBroadcasterSupport;

public class MBeanNotificationHelper extends NotificationBroadcasterSupport {
	private String name;
	private String description;
	
	public MBeanNotificationHelper(String name, String description) {
		this.name = name;
		this.description = description;
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE };
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, name, description);
		return new MBeanNotificationInfo[] { info };
	}

}

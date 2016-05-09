package com.reizes.shiva2.management;

import javax.management.AttributeChangeNotification;
import javax.management.MBeanNotificationInfo;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.monitor.MonitorNotification;

/**
 * 
 * @author kane
 *
 * @since 2.0.0
 */
public abstract class AbstractNotificatableTask extends NotificationBroadcasterSupport implements Notificatable {
	private long notificationSequenceNumber = 1;

	@Override
	public void sendNotification(String msg, String attributeName, String attributeType, Object oldValue, Object newValue) {
		Notification n = new AttributeChangeNotification(this, notificationSequenceNumber++, System.currentTimeMillis(),
				msg, attributeName, attributeType, oldValue, newValue);
		sendNotification(n);
	}

	@Override
	public void sendNotification(Throwable e) {
		Notification n = new Notification(this.getClass().getName(), this, notificationSequenceNumber++, System.currentTimeMillis(), e.getMessage());
		sendNotification(n);
	}

	@Override
	public MBeanNotificationInfo[] getNotificationInfo() {
		String[] types = new String[] { AttributeChangeNotification.ATTRIBUTE_CHANGE, MonitorNotification.RUNTIME_ERROR };
		MBeanNotificationInfo info = new MBeanNotificationInfo(types, this.getClass().getName(), "Attribute has changed");
		MBeanNotificationInfo error = new MBeanNotificationInfo(types, this.getClass().getName(), "Exception Occured");
		return new MBeanNotificationInfo[] { info, error };
	}

}

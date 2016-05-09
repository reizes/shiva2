package com.reizes.shiva2.management;

import javax.management.MBeanServer;

public interface Managable {
	public void registerMBean(MBeanServer mbeanServer) throws Exception;
}

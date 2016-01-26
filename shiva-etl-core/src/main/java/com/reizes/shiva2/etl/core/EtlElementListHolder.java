package com.reizes.shiva2.etl.core;

import java.util.List;

public interface EtlElementListHolder {
	public List<EtlElement> getElementList();
	public EtlElementListHolder setElementList(List<EtlElement> elementList);
	public EtlElementListHolder setElement(EtlElement element);
	public EtlElementListHolder addElement(EtlElement element);
}

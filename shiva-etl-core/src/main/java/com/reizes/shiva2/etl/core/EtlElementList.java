package com.reizes.shiva2.etl.core;

import java.util.Iterator;

import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.context.ProcessContextAware;

public class EtlElementList extends EtlElementListBase implements EtlProcessorAware, BeforeProcessAware,
	AfterProcessAware {

	@Override
	public Object doProcess(Object input) throws Exception {
		return processElementList(input, null);
	}

	@Override
	public void setEtlProcessor(EtlProcessor processor) {
		if (this.elementList != null) {
			for (Iterator<EtlElement> iter = this.elementList.iterator(); iter.hasNext();) {
				EtlElement next = iter.next();
				if (next instanceof EtlProcessorAware)
					((EtlProcessorAware)next).setEtlProcessor(processor);
			}
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		super.setProcessContext(context);
		if (this.elementList != null) {
			for (Iterator<EtlElement> iter = this.elementList.iterator(); iter.hasNext();) {
				EtlElement next = iter.next();
				if (next instanceof ProcessContextAware)
					((ProcessContextAware)next).setProcessContext(context);
			}
		}
	}

	@Override
	protected void setListenerFrom(EtlElementListBase element) {
		super.setListenerFrom(element);
		if (this.elementList != null) {
			for (Iterator<EtlElement> iter = this.elementList.iterator(); iter.hasNext();) {
				EtlElement next = iter.next();
				if (next instanceof EtlElementListBase) {
					((EtlElementListBase)next).setListenerFrom(element);
				}
			}
		}
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		callBeforeProcessAware(context, data);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		callAfterProcessAware(context, data);
	}

}

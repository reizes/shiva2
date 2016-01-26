package com.reizes.shiva2.etl.core;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.reizes.shiva2.etl.core.context.NullContextException;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.context.ProcessContextAware;

public abstract class EtlElementListBase implements EtlElement, EtlElementListHolder, ProcessContextAware {
	private BeforeItemProcessListener beforeItemProcessListener;
	private AfterItemProcessListener afterItemProcessListener;
	private ExceptionListener exceptionListener;
	protected ProcessContext context;
	protected List<EtlElement> elementList;
	private boolean shareProcessContext = true;

	public BeforeItemProcessListener getBeforeItemProcessListener() {
		return beforeItemProcessListener;
	}

	public void setBeforeItemProcessListener(BeforeItemProcessListener beforeItemProcessListener) {
		this.beforeItemProcessListener = beforeItemProcessListener;
	}

	public AfterItemProcessListener getAfterItemProcessListener() {
		return afterItemProcessListener;
	}

	public void setAfterItemProcessListener(AfterItemProcessListener afterItemProcessListener) {
		this.afterItemProcessListener = afterItemProcessListener;
	}

	protected void setListenerFrom(EtlElementListBase element) {
		this.setAfterItemProcessListener(element.getAfterItemProcessListener());
		this.setBeforeItemProcessListener(element.getBeforeItemProcessListener());
		this.setExceptionListener(element.getExceptionListener());
	}

	protected void callBeforeItemProcessListener(ProcessContext context, Object item) {
		if (this.beforeItemProcessListener != null) {
			this.beforeItemProcessListener.onBeforeItemProcess(context, item);
		}
	}

	protected void callAfterItemProcessListener(ProcessContext context, Object item) {
		if (this.afterItemProcessListener != null) {
			this.afterItemProcessListener.onAfterItemProcess(context, item);
		}
	}

	protected void callExceptionListener(ProcessContext context, Object item, Exception e) {
		if (this.exceptionListener != null) {
			this.exceptionListener.onException(context, item, e);
		}
	}

	public ExceptionListener getExceptionListener() {
		return exceptionListener;
	}

	public void setExceptionListener(ExceptionListener exceptionListener) {
		this.exceptionListener = exceptionListener;
	}

	protected Object processElementList(Object input, EtlElement startElement) throws Exception {
		Object output = input;
		boolean started = false;

		if (this.context == null) {
			throw new NullContextException();
		}
		for (EtlElement element : this.elementList) {
			if (started == false && startElement != null && startElement != element) {
				continue;
			}

			started = true;

			if (!(element instanceof EtlElementListBase)) {
				callBeforeItemProcessListener(this.context, output);
			}

			Exception rethrow = null;

			if (this.context.getExecutionStatus() == ExecutionStatus.CONTINUE) {
				if (element instanceof BeforeItemProcessAware) {
					((BeforeItemProcessAware)element).onBeforeItemProcess(this.context, output);
				}
				if (this.context.getExecutionStatus() == ExecutionStatus.CONTINUE) {
					try {
						output = element.doProcess(output);
					} catch (EtlInterruptException e) {
						rethrow = e;
						this.context.setExecutionStatus(ExecutionStatus.STOP);
					} catch (Exception e) {
						rethrow = e;
						this.context.setExecutionStatus(ExecutionStatus.STOP);
						callExceptionListener(this.context, output, e);
					}
				}
				if (element instanceof AfterItemProcessAware) {
					((AfterItemProcessAware)element).onAfterItemProcess(this.context, output);
				}
			}
			if (!(element instanceof EtlElementListBase)) {
				callAfterItemProcessListener(this.context, output);
			}
			// check ExecutionStatus
			if (rethrow != null && this.context.isThrowException()) {
				// exception
				if (this.context.getExecutionStatus() == ExecutionStatus.STOP) {
					throw rethrow;
				}

				rethrow = null;
			}

			if (this.context.getExecutionStatus() == ExecutionStatus.SKIP) {
				break;
			}
			if (this.context.getExecutionStatus() == ExecutionStatus.STOP) {
				throw new EtlInterruptException();
			}
		}
		if (this.context.getExecutionStatus() != ExecutionStatus.SKIP) {
			this.context.setItemCount(this.context.getItemCount() + 1);
		} else {
			this.context.setSkipCount(this.context.getSkipCount() + 1); // 2.1.1 - skip 카운트 도입 2012.7.3
		}

		return output;
	}

	protected void callBeforeProcessAware(ProcessContext context, Object data) throws Exception {
		if (this.elementList != null) {
			for (Iterator<EtlElement> iter = this.elementList.iterator(); iter.hasNext();) {
				EtlElement next = iter.next();
				if (next instanceof BeforeProcessAware) {
					((BeforeProcessAware)next).onBeforeProcess(context, data);
				}
			}
		}
	}

	protected void callAfterProcessAware(ProcessContext context, Object data) throws Exception {
		if (this.elementList != null) {
			for (Iterator<EtlElement> iter = this.elementList.iterator(); iter.hasNext();) {
				EtlElement next = iter.next();
				if (next instanceof AfterProcessAware) {
					((AfterProcessAware)next).onAfterProcess(context, data);
				}
			}
		}
	}

	@Override
	public void setProcessContext(ProcessContext context) {
		if (isShareProcessContext()) {
			this.context = context;
		}
	}

	public ProcessContext getProcessContext() {
		return this.context;
	}

	@Override
	public List<EtlElement> getElementList() {
		return elementList;
	}

	@Override
	public EtlElementListHolder setElementList(List<EtlElement> elementList) {
		for (EtlElement element : elementList) {
			this.addElement(element);
		}
		
		return this;
	}

	@Override
	public EtlElementListHolder setElement(EtlElement element) {
		this.elementList = new LinkedList<EtlElement>();
		this.elementList.add(element);
		return this;
	}

	@Override
	public EtlElementListHolder addElement(EtlElement element) {
		if (this.elementList == null) {
			this.setElement(element);
		} else {
			this.elementList.add(element);
		}
		return this;
	}

	public boolean isShareProcessContext() {
		return shareProcessContext;
	}

	public void setShareProcessContext(boolean shareProcessContext) {
		this.shareProcessContext = shareProcessContext;
	}

}

package com.reizes.shiva2.core;

import java.lang.management.ManagementFactory;
import java.util.List;

import javax.management.MBeanServer;
import javax.management.ObjectName;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.core.extractor.ExtractedItemHandler;
import com.reizes.shiva2.core.extractor.Extractor;
import com.reizes.shiva2.core.filter.AbstractFilter;
import com.reizes.shiva2.core.loader.AbstractLoader;
import com.reizes.shiva2.core.task.AbstractTask;
import com.reizes.shiva2.management.Managable;

public class TasksProcessor extends TasksBase implements TasksProcessorMBean, ExtractedItemHandler, BeforeProcessAware, AfterProcessAware {
	private Extractor extractor;
	private BeforeProcessListener beforeProcessListener;
	private AfterProcessListener afterProcessListener;
	private Task lastProcessor;
	private boolean shareProcessAware = false;

	public TasksProcessor() {
		this.setProcessContext(new ProcessContext(this));
	}

	private void callAfterProcessAware(ProcessContext context, Object input, Object output) throws Exception {
		callAfterProcessAware(this.context, output);

		if (extractor instanceof AfterProcessAware) {
			((AfterProcessAware)extractor).onAfterProcess(this.context, input);
		}

	}

	private void callBeforeProcessAwareSub(ProcessContext context, Object input) throws Exception {
		callBeforeProcessAware(this.context, input);

		if (extractor instanceof BeforeProcessAware) {
			((BeforeProcessAware)extractor).onBeforeProcess(this.context, input);
		}
	}

	@Override
	public Object doProcess(Object input) throws Exception {
		registerMBean();
		propagateListener();

		if (this.extractor != null) {
			this.context.initContext();
			this.context.setProcessParameter(input);
			this.context.setProcessStatus(ProcessStatus.RUNNING);
			Exception rethrow = null;
			Object output = null;

			if (this.beforeProcessListener != null) {
				this.beforeProcessListener.onBeforeProcess(this.context, input);
			}
			if (this.context.getProcessStatus() == ProcessStatus.RUNNING) {
				try {
					//this.onBeforeProcess(this.context, input);
					callBeforeProcessAwareSub(this.context, input);

					if (this.context.getProcessStatus() == ProcessStatus.RUNNING) {
						output = this.extractor.doProcess(input);

						if (this.lastProcessor != null) {
							// 마지막에 한번 더 처리가 지정되었을 경우 2009.11.2 reizes
							this.context.setExecutionStatus(ExecutionStatus.CONTINUE);
							((ProcessLastOneMore)this.lastProcessor).setLastItem(true);
							output = processElementList(input, this.lastProcessor);
						}

						this.context.setProcessStatus(ProcessStatus.FINISHED);
						this.context.setExecutionStatus(ExecutionStatus.STOP);
					}

				} catch (InterruptException e) {
					this.context.setProcessStatus(ProcessStatus.INTERRUPTED);
				} catch (Exception e) {
					this.context.setProcessStatus(ProcessStatus.FAILED);
					callExceptionListener(this.context, output, e);
					rethrow = e;
				}

				if (!isShareProcessAware()) {
					callAfterProcessAware(this.context, input, output);
				}
			}
			if (this.afterProcessListener != null) {
				this.afterProcessListener.onAfterProcess(this.context, output);
			}
			if (rethrow != null) {
				sendNotification(rethrow);
				throw rethrow;
			}

			return output;
		}

		throw new NullExtractorException("Extractor is NULL");
	}

	@Override
	public TasksHolder setTasks(List<Task> elementList) {
		clearTasks();
		for (Task element : elementList) {
			addTask(element);
		}
		
		return this;
	}

	@Override
	public TasksHolder addTasks(List<Task> elementList) {
		for (Task element : elementList) {
			addTask(element);
		}
		
		return this;
	}

	@Override
	public TasksHolder setTask(Task element) {
		clearTasks();
		this.addTask(element);
		return this;
	}

	@Override
	public TasksProcessor addTask(Task element) {
		super.addTask(element);
		if (element instanceof ProcessLastOneMore) {
			lastProcessor = element;
		}
		processAware(element);
		return this;
	}
	
	public TasksHolder addTransformer(AbstractTask transformer) {
		return this.addTask(transformer);
	}
	
	public TasksHolder addFilter(AbstractFilter filter) {
		return this.addTask(filter);
	}
	
	public TasksHolder addFilter(AbstractLoader loader) {
		return this.addTask(loader);
	}

	private void processAware(Task element) {
		if (element instanceof TasksProcessorAware) {
			((TasksProcessorAware)element).setTasksProcessor(this);
		}
		if (element instanceof ProcessContextAware) {
			((ProcessContextAware)element).setProcessContext(this.context);
		}
	}

	private void propagateListener() {
		if (elementList == null) {
			throw new IllegalStateException("There is no added element.");
		}
		for (Task task : this.elementList) {
			if (task instanceof TasksBase) {
				((TasksBase)task).setListenerFrom(this);
			}
		}
	}

	@Override
	public Object processExtractedItem(Object item) throws Exception {
		// reset status
		this.context.setExecutionStatus(ExecutionStatus.CONTINUE);

		return processElementList(item, null);
	}

	/**
	 * return extractor
	 * @since 0.2.0 change EtlElement to Extractor 
	 * @return Extractor object
	 */
	public Extractor getExtractor() {
		return extractor;
	}

	public TasksProcessor setExtractor(Extractor extractor) {
		this.extractor = extractor;
		this.extractor.setExtractedItemHandler(this);
		processAware(this.extractor);
		extractor.onAfterSetExtractorCallback(this.context);
		return this;
	}

	public BeforeProcessListener getBeforeProcessListener() {
		return beforeProcessListener;
	}

	public TasksProcessor setBeforeProcessListener(BeforeProcessListener beforeProcessListener) {
		this.beforeProcessListener = beforeProcessListener;
		return this;
	}

	public AfterProcessListener getAfterProcessListener() {
		return afterProcessListener;
	}

	public TasksProcessor setAfterProcessListener(AfterProcessListener afterProcessListener) {
		this.afterProcessListener = afterProcessListener;
		return this;
	}

	/**
	 * 2013.1.23 Will be called if this processor can receive parent's event.
	 * @param context ProcessContext for current processor
	 * @param data Data object for current process
	 * @throws Exception Exception object
	 * @see com.reizes.shiva2.core.AfterProcessAware#onAfterProcess(com.reizes.shiva2.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		if (isShareProcessAware()) {
			callAfterProcessAware(this.context, this.context.getProcessParameter(), data);
		}
	}

	/**
	 * 2013.1.23 Will be called if this processor can receive parent's event.
	 * @param context ProcessContext for current processor
	 * @param data Data object for current process
	 * @throws Exception Exception Exception object
	 * @see com.reizes.shiva2.core.BeforeProcessAware#onBeforeProcess(com.reizes.shiva2.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (isShareProcessAware()) {
			callBeforeProcessAwareSub(this.context, this.context.getProcessParameter());
		}
		
		registerMBean();
	}

	public boolean isShareProcessAware() {
		return shareProcessAware;
	}

	public TasksProcessor setShareProcessAware(boolean shareProcessAware) {
		this.shareProcessAware = shareProcessAware;
		return this;
	}
	
	public void registerMBean() throws Exception {
		MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
		this.registerMBean(mbs);
		if (this.extractor!=null && this.extractor instanceof Managable) {
			((Managable)this.extractor).registerMBean(mbs);
		}
	}

	@Override
	public void registerMBean(MBeanServer mbeanServer) throws Exception {
		ObjectName mbeanName = new ObjectName("shiva2.core:type=TasksProcessor");
		mbeanServer.registerMBean(this, mbeanName);
		this.context.registerMBean(mbeanServer);
		super.registerMBean(mbeanServer);
	}
}

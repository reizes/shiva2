package com.reizes.shiva2.core;

import java.util.List;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.context.ProcessContextAware;
import com.reizes.shiva2.core.filter.AbstractFilter;
import com.reizes.shiva2.core.loader.AbstractLoader;
import com.reizes.shiva2.core.reader.ExtractedItemHandler;
import com.reizes.shiva2.core.reader.Extractor;
import com.reizes.shiva2.core.task.AbstractTask;

public class TasksProcessor extends TasksBase implements ExtractedItemHandler, BeforeProcessAware, AfterProcessAware {
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
					if (!isShareProcessAware()) {
						callBeforeProcessAwareSub(this.context, input);
					}

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
	 * @since 2.1.0 EtlElement -> Extractor로 변경 
	 * @return
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
	 * 2013.1.23 상위 프로세스 이벤트를 수신할지 여부에 따라 호출됨
	 * @param context
	 * @param data
	 * @throws Exception
	 * @see com.reizes.shiva2.core.AfterProcessAware#onAfterProcess(com.reizes.shiva2.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		if (isShareProcessAware()) {
			callAfterProcessAware(this.context, this.context.getProcessParameter(), data);
		}
	}

	/**
	 * 2013.1.23 상위 프로세스 이벤트를 수신할지 여부에 따라 호출됨
	 * @param context
	 * @param data
	 * @throws Exception
	 * @see com.reizes.shiva2.core.BeforeProcessAware#onBeforeProcess(com.reizes.shiva2.core.context.ProcessContext, java.lang.Object)
	 */
	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (isShareProcessAware()) {
			callBeforeProcessAwareSub(this.context, this.context.getProcessParameter());
		}
	}

	public boolean isShareProcessAware() {
		return shareProcessAware;
	}

	public TasksProcessor setShareProcessAware(boolean shareProcessAware) {
		this.shareProcessAware = shareProcessAware;
		return this;
	}
}

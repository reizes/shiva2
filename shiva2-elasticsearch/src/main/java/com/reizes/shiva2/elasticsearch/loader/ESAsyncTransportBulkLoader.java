package com.reizes.shiva2.elasticsearch.loader;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.AsyncTasks;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.context.ProcessContext;

public class ESAsyncTransportBulkLoader extends AsyncTasks implements Closeable, Flushable, BeforeProcessAware, AfterProcessAware {
	private ESTransportBulkLoader bulkLoader;
	
	public ESAsyncTransportBulkLoader(Map<String, Object> configs) {
		init(new ESTransportBulkLoader(configs));
	}
	
	public ESAsyncTransportBulkLoader(List<String> hosts, String clusterName) {
		init(new ESTransportBulkLoader(hosts, clusterName));
	}
	
	private void init(ESTransportBulkLoader bulkLoader) {
		this.bulkLoader = bulkLoader;
		this.addTask(bulkLoader);
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		this.bulkLoader.onAfterProcess(context, data);
		super.onAfterProcess(context, data);
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		this.bulkLoader.onBeforeProcess(context, data);
		super.onBeforeProcess(context, data);
	}

	@Override
	public void flush() throws IOException {
		bulkLoader.flush();
	}

	public ESTransportBulkLoader getBulkLoader() {
		return bulkLoader;
	}

	@Override
	public void close() throws IOException {
		bulkLoader.close();
	}
}

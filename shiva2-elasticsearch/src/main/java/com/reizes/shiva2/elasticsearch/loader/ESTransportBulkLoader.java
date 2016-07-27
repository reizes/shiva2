package com.reizes.shiva2.elasticsearch.loader;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

import com.reizes.shiva2.core.AfterProcessAware;
import com.reizes.shiva2.core.BeforeProcessAware;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.loader.AbstractLoader;
import com.reizes.shiva2.elasticsearch.BulkLoadException;
import com.reizes.shiva2.elasticsearch.ESTransportClient;

public class ESTransportBulkLoader extends AbstractLoader implements Closeable, Flushable, AfterProcessAware, BeforeProcessAware {
	private ESTransportClient client;
	private int bulkSize = 500;
	private String indexFormat;
	private String typeFormat;
	private String idFormat;
	private String actionFormat;
	private ESLoaderAction action;
	private boolean bulkLoad = true;
	private int currentCount=0;
	private ESIdGenerator idGenerator;
	
	public ESTransportBulkLoader(Map<String, Object> configs) {
		client = new ESTransportClient(configs);
	}
	
	public ESTransportBulkLoader(List<String> hosts, String clusterName) {
		Map<String, Object> configs = new HashMap<>();
		configs.put("hosts", hosts);
		configs.put("cluster.name", clusterName);
		client = new ESTransportClient(configs);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		Map<String, Object> data = (Map<String, Object>)input; 
		if (data!=null) {
			StrSubstitutor sub = new StrSubstitutor(data, "{{", "}}");
			
			String index = sub.replace(indexFormat);
			String type = sub.replace(typeFormat);
			String id = sub.replace(idFormat);
			
			if (idGenerator!=null) {
				id = idGenerator.generateId(id);
			}
			
			ESLoaderAction action = this.action;
			if (actionFormat!=null) {
				String actionStr = sub.replace(actionFormat);
				action = ESLoaderAction.from(actionStr);
			}
			
			if (action!=null) {
				switch(action) {
				case DELETE :
					if (isBulkLoad()) {
						client.addBulkDelete(index, type, id);
					} else {
						client.delete(index, type, id);
					}
					break;
				case UPDATE :
					if (isBulkLoad()) {
						client.addBulkUpdate(index, type, id, data);
					} else {
						client.update(index, type, id, data);
					}
					break;
				case UPSERT :
					if (isBulkLoad()) {
						client.addBulkIndex(index, type, id, data);
					} else {
						client.upsert(index, type, id, data);
					}
					break;
				case INDEX :
				default:
					if (isBulkLoad()) {
						client.addBulkIndex(index, type, id, data);
					} else {
						client.index(index, type, id, data);
					}
					break;
				}
			} else {
				if (isBulkLoad()) {
					client.addBulkIndex(index, type, id, data);
				} else {
					client.index(index, type, id, data);
				}
			}
			
			if (isBulkLoad()) {
				synchronized(this) {
					currentCount++;
					if (currentCount==bulkSize) {
						flush();
					}
				}
			}
		}

		return data;
	}

	@Override
	public void close() throws IOException {
		flush();
		if (client!=null) {
			client.close();
			client=null;
		}
	}

	@Override
	public void onAfterProcess(ProcessContext context, Object data) throws Exception {
		close();
	}

	@Override
	public synchronized void flush() throws IOException {
		if (isBulkLoad() && client != null) {
			BulkResponse response = client.executeBulk();
			currentCount = 0;
			if (response!=null && response.hasFailures()) {
				StringBuilder sb = new StringBuilder();
				for(BulkItemResponse itemResponse : response.getItems()) {
					if (itemResponse.isFailed()) {
						sb.append(itemResponse.getIndex()).append("/")
						.append(itemResponse.getType()).append("/")
						.append(itemResponse.getId()).append(":")
						.append(itemResponse.getOpType()).append(":")
						.append(itemResponse.getFailureMessage())
						.append("\n");
					}
				}
				throw new BulkLoadException(sb.toString());
			}
		}
	}

	public int getBulkSize() {
		return bulkSize;
	}

	public ESTransportBulkLoader setBulkSize(int bulkSize) {
		this.bulkSize = bulkSize;
		return this;
	}

	public String getIndexFormat() {
		return indexFormat;
	}

	public ESTransportBulkLoader setIndexFormat(String indexFormat) {
		this.indexFormat = indexFormat;
		return this;
	}

	public String getTypeFormat() {
		return typeFormat;
	}

	public ESTransportBulkLoader setTypeFormat(String typeFormat) {
		this.typeFormat = typeFormat;
		return this;
	}

	public String getIdFormat() {
		return idFormat;
	}

	public ESTransportBulkLoader setIdFormat(String idFormat) {
		this.idFormat = idFormat;
		return this;
	}

	@Override
	public void onBeforeProcess(ProcessContext context, Object data) throws Exception {
		if (indexFormat==null) {
			throw new InvalidPropertyException("indexFormat can not be null!!");
		}
		if (typeFormat==null) {
			throw new InvalidPropertyException("typeFormat can not be null!!");
		}
		if (idFormat==null) {
			throw new InvalidPropertyException("idFormat can not be null!!");
		}
	}

	public String getActionFormat() {
		return actionFormat;
	}

	public ESTransportBulkLoader setActionFormat(String actionFormat) {
		this.actionFormat = actionFormat;
		return this;
	}

	public ESLoaderAction getAction() {
		return action;
	}

	public ESTransportBulkLoader setAction(ESLoaderAction action) {
		this.action = action;
		return this;
	}

	public boolean isBulkLoad() {
		return bulkLoad;
	}

	public ESTransportBulkLoader setBulkLoad(boolean bulkLoad) {
		this.bulkLoad = bulkLoad;
		return this;
	}

	public ESIdGenerator getIdGenerator() {
		return idGenerator;
	}

	public ESTransportBulkLoader setIdGenerator(ESIdGenerator idGenerator) {
		this.idGenerator = idGenerator;
		return this;
	}

}

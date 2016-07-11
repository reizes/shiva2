package com.reizes.shiva2.elasticsearch;

import java.io.Closeable;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateRequestBuilder;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.settings.Settings.Builder;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.script.Script;

import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.NullArgumentException;

public class ESTransportClient implements Closeable {
	private Client client;
	private static final int DEFAULT_PORT = 9300;
	private BulkRequestBuilder bulkRequest;
	private Map<String, Object> configs;
	
	public ESTransportClient(Map<String, Object> configs) {
		this.configs = configs;
	}
	
	@SuppressWarnings("unchecked")
	private void connect() {
		if (client!=null) {
			return;
		}
		if (configs==null) {
			throw new NullArgumentException("configs can't be null!");
		}
		
		Builder settingBuilder = Settings.settingsBuilder();
		
		for(String key : configs.keySet()) {
			if (!key.equals("hosts")) {
				settingBuilder.put(key, configs.get(key));
			}
		}

		TransportClient client = TransportClient.builder().settings(settingBuilder).build();
		List<String> hosts = (List<String>) configs.get("hosts");
		
		if (hosts==null) {
			throw new InvalidPropertyException("hosts can not be null!!");
		}
		
		for(String host : hosts) {
			String[] strs = StringUtils.split(host, ":");
			try {
				client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(strs[0]), strs.length>1?Integer.parseInt(strs[1]):DEFAULT_PORT));
			} catch (NumberFormatException | UnknownHostException e) {
				e.printStackTrace();
				throw new InvalidPropertyException(e.getMessage());
			}
		}
		
		this.client = client;
		
		bulkRequest = client.prepareBulk(); 
				
	}

	@Override
	public void close() throws IOException {
		if (client != null) {
			client.close();
			client = null;
			bulkRequest = null;
		}
	}
	
	public void addBulkUpdate(UpdateRequest request) {
		connect();
		bulkRequest.add(request);
	}
	
	public void addBulkUpdate(String index, String type, String id, String jsonString, Script script) {
		connect();
		UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setDoc(jsonString);
		if (script!=null) {
			builder.setScript(script);
		}
		bulkRequest.add(builder);
	}
	
	public void addBulkUpdate(String index, String type, String id, String jsonString) {
		addBulkUpdate(index, type, id, jsonString, null);
	}
	
	public void addBulkUpdate(String index, String type, String id, Map<String, Object> doc, Script script) {
		connect();
		UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setDoc(doc);
		if (script!=null) {
			builder.setScript(script);
		}
		bulkRequest.add(builder);
	}
	
	public void addBulkUpdate(String index, String type, String id, Map<String, Object> doc) {
		addBulkUpdate(index, type, id, doc, null);
	}
	
	public void addBulkDelete(DeleteRequest request) {
		connect();
		bulkRequest.add(request);
	}
	
	public void addBulkDelete(String index, String type, String id) {
		connect();
		bulkRequest.add(client.prepareDelete(index, type, id));
	}
	
	public void addBulkIndex(IndexRequest request) {
		connect();
		bulkRequest.add(request);
	}
	
	public void addBulkIndex(String index, String type, String id, Map<String, Object> doc) {
		connect();
		bulkRequest.add(client.prepareIndex(index, type, id).setSource(doc));
	}
	
	public void addBulkIndex(String index, String type, String id, String jsonString) {
		connect();
		bulkRequest.add(client.prepareIndex(index, type, id).setSource(jsonString));
	}
	
	public BulkResponse executeBulk() {
		connect();
		return bulkRequest.get();
	}
	
	public GetResponse get(String index, String type, String id) {
		connect();
		return client.prepareGet(index, type, id).setOperationThreaded(false).get();
	}
	
	public IndexResponse index(String index, String type, String id, Map<String, Object> doc) {
		connect();
		return client.prepareIndex(index, type, id).setSource(doc).get();
	}
	
	public IndexResponse index(String index, String type, String id, String jsonString) {
		connect();
		return client.prepareIndex(index, type, id).setSource(jsonString).get();
	}
	
	public DeleteResponse delete(String index, String type, String id) {
		connect();
		return client.prepareDelete(index, type, id).get();
	}
	
	public UpdateResponse update(String index, String type, String id, String jsonString) {
		connect();
		UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setDoc(jsonString);
		return builder.get();
	}
	
	public UpdateResponse update(String index, String type, String id, Map<String, Object> doc) {
		connect();
		UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setDoc(doc);
		return builder.get();
	}
	
	public UpdateResponse update(String index, String type, String id, Script script) {
		connect();
		UpdateRequestBuilder builder = client.prepareUpdate(index, type, id).setScript(script);
		return builder.get();
	}
	
	public UpdateResponse upsert(String index, String type, String id, String jsonString, Script script) throws InterruptedException, ExecutionException {
		connect();
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(jsonString);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).upsert(indexRequest).script(script);
		return client.update(updateRequest).get();
	}
	
	public UpdateResponse upsert(String index, String type, String id, String jsonString) throws InterruptedException, ExecutionException {
		connect();
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(jsonString);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(jsonString).upsert(indexRequest);
		return client.update(updateRequest).get();
	}
	
	public UpdateResponse upsert(String index, String type, String id, Map<String, Object> doc, Script script) throws InterruptedException, ExecutionException {
		connect();
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(doc);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).upsert(indexRequest).script(script);
		return client.update(updateRequest).get();
	}
	
	public UpdateResponse upsert(String index, String type, String id, Map<String, Object> doc) throws InterruptedException, ExecutionException {
		connect();
		IndexRequest indexRequest = new IndexRequest(index, type, id).source(doc);
		UpdateRequest updateRequest = new UpdateRequest(index, type, id).doc(doc).upsert(indexRequest);
		return client.update(updateRequest).get();
	}
	
}

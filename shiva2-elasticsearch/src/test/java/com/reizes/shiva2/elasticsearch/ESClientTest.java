package com.reizes.shiva2.elasticsearch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ESClientTest {
	ESTransportClient client;
	
	String index="shiva2-es-test";
	String type="test-doc";
	String id="1";
	
	Map<String, Object> testDoc;
	Map<String, Object> testDoc2;

	@Before
	public void setUp() throws Exception {
		Map<String, Object> configs = new HashMap<>();
		List<String> hosts = new ArrayList<>();
		hosts.add("127.0.0.1:9300");
		configs.put("hosts", hosts);
		configs.put("cluster.name", "contents");
		client = new ESTransportClient(configs);
		
		testDoc = new HashMap<>();
		testDoc.put("field1", "data1");
		testDoc.put("field2", "data2");
		testDoc.put("field3", "data3");
		Map<String, Object> nestedDoc = new HashMap<>();
		nestedDoc.put("field4-1", "data4-1");
		nestedDoc.put("field4-2", "data4-2");
		nestedDoc.put("field4-3", "data4-3");
		testDoc.put("field4", nestedDoc);
		
		testDoc2 = new HashMap<>();
		testDoc2.put("field7", "data7");
		testDoc2.put("field5", "data5");
		testDoc2.put("field6", "data6");
		Map<String, Object> nestedDoc2 = new HashMap<>();
		nestedDoc2.put("field4-1", "data4-1");
		nestedDoc2.put("field4-2", "data4-2");
		nestedDoc2.put("field4-3", "data4-3");
		testDoc2.put("field8", nestedDoc2);
	}

	@After
	public void tearDown() throws Exception {
		client.close();
	}
	
	@SuppressWarnings("unchecked")
	private void checkGetResponse(String id) {
		GetResponse response = client.get(index, type, id);
		assertNotNull(response);
		assertEquals(true, response.isExists());
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		Map<String, Object> data = response.getSource();
		
		assertNotNull(data);
		assertEquals("data1", data.get("field1"));
		assertEquals("data2", data.get("field2"));
		assertEquals("data3", data.get("field3"));
		assertNull(data.get("data5"));
		assertNull(data.get("data6"));
		assertNull(data.get("data7"));
		
		Map<String, Object> nestedDoc = (Map<String, Object>) data.get("field4");
		assertNotNull(nestedDoc);
		assertEquals("data4-1", nestedDoc.get("field4-1"));
		assertEquals("data4-2", nestedDoc.get("field4-2"));
		assertEquals("data4-3", nestedDoc.get("field4-3"));
	}
	
	@SuppressWarnings("unchecked")
	private void checkGetResponse2(String id) {
		GetResponse response = client.get(index, type, id);
		assertNotNull(response);
		assertEquals(true, response.isExists());
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		Map<String, Object> data = response.getSource();
		
		assertNotNull(data);
		assertNull(data.get("data1"));
		assertNull(data.get("data2"));
		assertNull(data.get("data3"));
		assertEquals("data7", data.get("field7"));
		assertEquals("data5", data.get("field5"));
		assertEquals("data6", data.get("field6"));
		
		Map<String, Object> nestedDoc = (Map<String, Object>) data.get("field8");
		assertNotNull(nestedDoc);
		assertEquals("data4-1", nestedDoc.get("field4-1"));
		assertEquals("data4-2", nestedDoc.get("field4-2"));
		assertEquals("data4-3", nestedDoc.get("field4-3"));
	}
	
	private void indexDoc1() {
		IndexResponse response = client.index(index, type, id, testDoc);
		
		assertNotNull(response);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
	}
	
	private void indexDoc2() {
		IndexResponse response = client.index(index, type, id, testDoc2);
		
		assertNotNull(response);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
	}

	@Test
	public void testIndexDocument() {
		indexDoc1();
		
		checkGetResponse(id);
		
		indexDoc2();
		checkGetResponse2(id);
	}

	@Test
	public void testDelete() {
		indexDoc1();
		DeleteResponse response = client.delete(index, type, id);
		assertNotNull(response);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		
		assertEquals(false, client.get(index, type, id).isExists());
	}

	@Test
	public void testUpdate() {
		indexDoc1();
		UpdateResponse response = client.update(index, type, id, testDoc2);
		
		assertNotNull(response);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		assertEquals(false, response.isCreated());
		checkGetResponse2(id);
	}

	@Test
	public void testUpsert() throws InterruptedException, ExecutionException {
		client.delete(index, type, id);
		UpdateResponse response = client.upsert(index, type, id, testDoc);
		
		assertNotNull(response);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		assertEquals(true, response.isCreated());
		checkGetResponse(id);
		
		response = client.upsert(index, type, id, testDoc2);
		assertEquals(index, response.getIndex());
		assertEquals(type, response.getType());
		assertEquals(id, response.getId());
		assertEquals(false, response.isCreated());
		checkGetResponse2(id);
		
	}

	@Test
	public void testBulk() {
		client.delete(index, type, id);
		for(int i=0;i<100;i++) {
			client.addBulkIndex(index, type, String.valueOf(i), testDoc);
		}
		BulkResponse response = client.executeBulk();
		assertEquals(false, response.hasFailures());
		checkGetResponse("99");
		for(int i=0;i<100;i++) {
			client.addBulkIndex(index, type, String.valueOf(i), testDoc2);
		}
		response = client.executeBulk();
		assertEquals(false, response.hasFailures());
		checkGetResponse2("99");
		for(int i=0;i<100;i++) {
			client.addBulkDelete(index, type, String.valueOf(i));
		}
		response = client.executeBulk();
		assertEquals(false, response.hasFailures());
		assertEquals(false, client.get(index, type, "99").isExists());
	}
}

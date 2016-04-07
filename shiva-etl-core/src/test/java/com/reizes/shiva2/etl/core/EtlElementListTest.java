package com.reizes.shiva2.etl.core;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.AfterItemProcessListener;
import com.reizes.shiva2.core.BeforeItemProcessListener;
import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.Tasks;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;

public class EtlElementListTest {

	private Tasks etlElementList;
	private LinkedList<Task> elementList;
	private int elementCount=5;
	
	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlElementList=new Tasks();
		this.etlElementList.setProcessContext(new ProcessContext(null));
		
		this.elementList=new LinkedList<Task>(); 
		for(int i=0;i<elementCount;i++) {
			Task element=new MockEtlElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlElementList=null;
		this.elementList.clear();
		this.elementList=null;
	}

	@Test
	public void testGetElementList1() {
		assertNull(this.etlElementList.getTasks());
	}

	@Test
	public void testSetElementList() {
		assertEquals(this.elementList.size(),this.elementCount);
		this.etlElementList.setTasks(this.elementList);
		assertNotNull(this.etlElementList.getTasks());
	}

	@Test
	public void testGetElementList2() {
		this.etlElementList.setTasks(this.elementList);
		assertNotNull(this.etlElementList.getTasks());
		assertEquals(this.etlElementList.getTasks().size(),this.elementCount);
	}

	@Test
	public void testSetElement() {
		Task element=new MockEtlElement();
		this.etlElementList.setTask(element);
		assertEquals(this.etlElementList.getTasks().size(),1);
	}

	@Test
	public void testAddElement() {
		Task element=new MockEtlElement();
		this.etlElementList.setTask(element);
		assertEquals(this.etlElementList.getTasks().size(),1);
		Task element2=new MockEtlElement();
		this.etlElementList.addTask(element2);
		assertEquals(this.etlElementList.getTasks().size(),2);
	}
	
	private class ListenerTest implements BeforeItemProcessListener,AfterItemProcessListener {
		public int beforeItemProcessNum=0;
		public int afterItemProcessNum=0;
		
		@Override
		public void onBeforeItemProcess(ProcessContext context, Object data) {
			beforeItemProcessNum++;
		}

		@Override
		public void onAfterItemProcess(ProcessContext context, Object data) {
			afterItemProcessNum++;
		}
		
	}

	@Test
	public void testDoProcess() {
		assertEquals(this.elementList.size(),this.elementCount);
		this.etlElementList.setTasks(this.elementList);
		assertNotNull(this.etlElementList.getTasks());
		ListenerTest testListener=new ListenerTest();
		this.etlElementList.setBeforeItemProcessListener(testListener);
		this.etlElementList.setAfterItemProcessListener(testListener);
		
		try {
			this.etlElementList.doProcess("MockData");
			assertEquals(MockEtlElement.getProcessCount(),this.elementCount);
			assertEquals(testListener.beforeItemProcessNum,this.elementCount);
			assertEquals(testListener.afterItemProcessNum,this.elementCount);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

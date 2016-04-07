package com.reizes.shiva2.etl.core;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.AfterItemProcessListener;
import com.reizes.shiva2.core.AfterProcessListener;
import com.reizes.shiva2.core.BeforeItemProcessListener;
import com.reizes.shiva2.core.BeforeProcessListener;
import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class EtlProcessorTest {
	
	private TasksProcessor etlProcessor;
	private LinkedList<Task> elementList;
	private int elementCount=5;
	private int itemCount=100;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlProcessor=new TasksProcessor();
		
		this.elementList=new LinkedList<Task>(); 
		for(int i=0;i<elementCount;i++) {
			Task element=new MockEtlElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor=null;
		this.elementList.clear();
		this.elementList=null;
	}


	@Test
	public void testGetElementList1() {
		assertNull(this.etlProcessor.getTasks());
	}

	@Test
	public void testSetElementList() {
		assertEquals(this.elementList.size(),this.elementCount);
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
	}

	@Test
	public void testGetElementList2() {
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount);
	}

	@Test
	public void testSetElement() {
		Task element=new MockEtlElement();
		this.etlProcessor.setTask(element);
		assertEquals(this.etlProcessor.getTasks().size(),1);
	}

	@Test
	public void testAddElement() {
		Task element=new MockEtlElement();
		this.etlProcessor.setTask(element);
		assertEquals(this.etlProcessor.getTasks().size(),1);
		Task element2=new MockEtlElement();
		this.etlProcessor.addTask(element2);
		assertEquals(this.etlProcessor.getTasks().size(),2);
	}
	
	private class ListenerTest implements BeforeProcessListener,AfterProcessListener,BeforeItemProcessListener,AfterItemProcessListener {
		public boolean beforeProcessCheck=false;
		public boolean afterProcessCheck=false;
		public int beforeItemProcessNum=0;
		public int afterItemProcessNum=0;
		
		@Override
		public void onBeforeProcess(ProcessContext context, Object input) {
			beforeProcessCheck=true;
		}

		@Override
		public void onAfterProcess(ProcessContext context, Object output) {
			afterProcessCheck=true;
		}

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
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount);
		
		MockExtractor extractor=new MockExtractor(); 
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(String.format("%d item", i+1));
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);
		
		ListenerTest testListener=new ListenerTest();
		this.etlProcessor.setBeforeProcessListener(testListener);
		this.etlProcessor.setBeforeItemProcessListener(testListener);
		this.etlProcessor.setAfterProcessListener(testListener);
		this.etlProcessor.setAfterItemProcessListener(testListener);
		
		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// element process check
			assertEquals(critNum,MockEtlElement.getProcessCount());
			// listener check
			assertTrue(testListener.beforeProcessCheck);
			assertTrue(testListener.afterProcessCheck);
			assertEquals("before listen : "+testListener.beforeItemProcessNum,critNum,testListener.beforeItemProcessNum);
			assertEquals("after listen : "+testListener.afterItemProcessNum,critNum,testListener.afterItemProcessNum);
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

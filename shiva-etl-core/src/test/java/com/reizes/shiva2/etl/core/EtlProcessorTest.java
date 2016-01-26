package com.reizes.shiva2.etl.core;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class EtlProcessorTest {
	
	private EtlProcessor etlProcessor;
	private LinkedList<EtlElement> elementList;
	private int elementCount=5;
	private int itemCount=100;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlProcessor=new EtlProcessor();
		
		this.elementList=new LinkedList<EtlElement>(); 
		for(int i=0;i<elementCount;i++) {
			EtlElement element=new MockEtlElement();
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
		assertNull(this.etlProcessor.getElementList());
	}

	@Test
	public void testSetElementList() {
		assertEquals(this.elementList.size(),this.elementCount);
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
	}

	@Test
	public void testGetElementList2() {
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(),this.elementCount);
	}

	@Test
	public void testSetElement() {
		EtlElement element=new MockEtlElement();
		this.etlProcessor.setElement(element);
		assertEquals(this.etlProcessor.getElementList().size(),1);
	}

	@Test
	public void testAddElement() {
		EtlElement element=new MockEtlElement();
		this.etlProcessor.setElement(element);
		assertEquals(this.etlProcessor.getElementList().size(),1);
		EtlElement element2=new MockEtlElement();
		this.etlProcessor.addElement(element2);
		assertEquals(this.etlProcessor.getElementList().size(),2);
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
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(),this.elementCount);
		
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

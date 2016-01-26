package com.reizes.shiva2.etl.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockEtlElementRaiseException;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class EtlProcessorExceptionTest {
	private int itemCount=100;
	private int stopCount=50;
	private LinkedList<Object> mockData;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		MockEtlElementRaiseException.resetProcessCount();
		MockEtlElementRaiseException.setStop(stopCount);
		
		mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(new Long(i+1));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testNormalException() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		MockEtlElementRaiseException element=new MockEtlElementRaiseException();
		element.setException(new Exception("Normal Exception Test"));
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(element);
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		try {
			etlProcessor.doProcess(null);
			fail("exception not raised");
		} catch (Exception e) {
			System.out.println("Exception raised : "+e.getMessage());
			// RaiseStop
			assertEquals((this.stopCount-1)*4+3,MockEtlElement.getProcessCount());
			// skip
			assertEquals((this.stopCount-1),MockEtlElementRaiseException.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FAILED,etlProcessor.getProcessContext().getProcessStatus());
		}
	}

	@Test
	public void testInterruptException() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		MockEtlElementRaiseException element=new MockEtlElementRaiseException();
		element.setException(new EtlInterruptException("Interrupt Exception Test"));
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(element);
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		try {
			etlProcessor.doProcess(null);
			// RaiseStop
			assertEquals((this.stopCount-1)*4+3,MockEtlElement.getProcessCount());
			// skip
			assertEquals((this.stopCount-1),MockEtlElementRaiseException.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.INTERRUPTED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	private class TestExceptionListener implements ExceptionListener {
		public ExecutionStatus executionStatus;
		
		@Override
		public void onException(ProcessContext context, Object input,
				Exception e) {
			context.setThrowException(false);
			context.setExecutionStatus(executionStatus);
		}
		
	}
	
	@Test
	public void testExceptionAndStop() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		MockEtlElementRaiseException element=new MockEtlElementRaiseException();
		element.setException(new Exception("Normal Exception Test"));
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(element);
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		TestExceptionListener listener=new TestExceptionListener();
		listener.executionStatus=ExecutionStatus.STOP;
		etlProcessor.setExceptionListener(listener);
		
		try {
			etlProcessor.doProcess(null);
			//  RaiseStop
			assertEquals((this.stopCount-1)*4+3,MockEtlElement.getProcessCount());
			// skip
			assertEquals((this.stopCount-1),MockEtlElementRaiseException.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.INTERRUPTED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExceptionAndContinue() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		MockEtlElementRaiseException element=new MockEtlElementRaiseException();
		element.setException(new Exception("Normal Exception Test"));
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(element);
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		TestExceptionListener listener=new TestExceptionListener();
		listener.executionStatus=ExecutionStatus.CONTINUE;
		etlProcessor.setExceptionListener(listener);
		
		try {
			etlProcessor.doProcess(null);
			// RaiseStop
			assertEquals(4*this.itemCount,MockEtlElement.getProcessCount());
			// skip
			assertEquals(this.itemCount-1,MockEtlElementRaiseException.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testExceptionAndSkip() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		MockEtlElementRaiseException element=new MockEtlElementRaiseException();
		element.setException(new Exception("Normal Exception Test"));
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(element);
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		TestExceptionListener listener=new TestExceptionListener();
		listener.executionStatus=ExecutionStatus.SKIP;
		etlProcessor.setExceptionListener(listener);
		
		try {
			etlProcessor.doProcess(null);
			// 
			assertEquals((this.itemCount)*4-1,MockEtlElement.getProcessCount());
			// skip
			assertEquals((this.itemCount)-1,MockEtlElementRaiseException.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
}

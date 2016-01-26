package com.reizes.shiva2.etl.core;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockEtlElementRaiseStop;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class EtlProcessorStopTest {
	
	private int itemCount=100;
	private int stopCount=50;
	private LinkedList<Object> mockData;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		MockEtlElementRaiseStop.resetProcessCount();
		MockEtlElementRaiseStop.setStop(stopCount);
		
		mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(new Long(i+1));
		}
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStopTest() {
		EtlProcessor etlProcessor=new EtlProcessor();
		
		LinkedList<EtlElement> elementList=new LinkedList<EtlElement>(); 
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElement());
		elementList.add(new MockEtlElementRaiseStop());
		elementList.add(new MockEtlElement());
		etlProcessor.setElementList(elementList);
		assertNotNull(etlProcessor.getElementList());
		assertEquals(etlProcessor.getElementList().size(),5);
		
		MockExtractor extractor=new MockExtractor(); 
		extractor.setMockData(this.mockData);
		etlProcessor.setExtractor(extractor);
		
		try {
			etlProcessor.doProcess(null);
			// 
			assertEquals((this.stopCount-1)*4+3,MockEtlElement.getProcessCount());
			// 
			assertEquals((this.stopCount-1),MockEtlElementRaiseStop.getProcessCount());
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.INTERRUPTED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

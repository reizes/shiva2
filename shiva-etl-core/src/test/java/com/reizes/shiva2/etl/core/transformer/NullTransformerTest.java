package com.reizes.shiva2.etl.core.transformer;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.AfterItemProcessListener;
import com.reizes.shiva2.etl.core.BeforeItemProcessListener;
import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.EtlProcessor;
import com.reizes.shiva2.etl.core.ExecutionStatus;
import com.reizes.shiva2.etl.core.ProcessStatus;
import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.element.NullElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class NullTransformerTest {
	
	private EtlProcessor etlProcessor;
	private LinkedList<EtlElement> elementList;
	private int elementCount=5;
	private int itemCount=100;

	@Before
	public void setUp() throws Exception {
		this.etlProcessor=new EtlProcessor();
		
		this.elementList=new LinkedList<EtlElement>(); 
		for(int i=0;i<elementCount;i++) {
			EtlElement element=new NullElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor=null;
		this.elementList.clear();
		this.elementList=null;
	}

	
	private class ListenerTest implements BeforeItemProcessListener,AfterItemProcessListener {
		public int beforeItemProcessNum=0;
		public int afterItemProcessNum=0;
		public int itemNotEqual=0;
		private Object item;
		
		@Override
		public void onBeforeItemProcess(ProcessContext context, Object data) {
			beforeItemProcessNum++;
			item=data;
		}

		@Override
		public void onAfterItemProcess(ProcessContext context, Object data) {
			afterItemProcessNum++;
			if (!item.equals(data)) itemNotEqual++;
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
		this.etlProcessor.setBeforeItemProcessListener(testListener);
		this.etlProcessor.setAfterItemProcessListener(testListener);

		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// listener check
			assertEquals(critNum,testListener.beforeItemProcessNum);
			assertEquals(critNum,testListener.afterItemProcessNum);
			// null transform check
			assertEquals(0,testListener.itemNotEqual);
			
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

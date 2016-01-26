package com.reizes.shiva2.etl.core;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.context.ProcessContext;
import com.reizes.shiva2.etl.core.context.ProcessContextAware;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class EtlProcessorAwareTest {
	
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
			EtlElement element=new AwareTestElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor=null;
		this.elementList.clear();
		this.elementList=null;
	}
	
	public int itemSum=0;
	public int checkEtlProcessAware=0;
	public int checkBeforeProcessAware=0;
	public int checkAfterProcessAware=0;

	private class AwareTestElement extends MockEtlElement implements
	BeforeItemProcessAware, AfterItemProcessAware, EtlProcessorAware,
	ProcessContextAware,BeforeProcessAware,AfterProcessAware {
		
		private ProcessContext context;

		@Override
		public void onBeforeItemProcess(ProcessContext context,Object data) {
			Long input=(Long)data;
			itemSum+=input.intValue();
			Long item=(Long)this.context.get("cur_item");
			this.context.put("cur_item", item+input);
		}

		@Override
		public void onAfterItemProcess(ProcessContext context,Object data) {
			Long input=(Long)data;
			itemSum-=input.intValue();
			Long item=(Long)this.context.get("cur_item");
			this.context.put("cur_item", item+input);
		}

		@Override
		public void setEtlProcessor(EtlProcessor processor) {
			if (processor!=null) checkEtlProcessAware++;
		}

		@Override
		public void setProcessContext(ProcessContext context) {
			this.context=context;
			Long item=(Long)this.context.get("cur_item");
			if (item==null) this.context.put("cur_item", new Long(0));
		}
		
		@Override
		public void onBeforeProcess(ProcessContext context,Object data) {
			checkBeforeProcessAware++;
		}
		@Override
		public void onAfterProcess(ProcessContext context,Object data) {
			checkAfterProcessAware++;
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
		int checkContextData=0;
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(new Long(i));
			checkContextData+=i;
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);
		
		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// process check
			assertEquals(critNum,MockEtlElement.getProcessCount());
			// aware check
			assertEquals(this.elementCount,checkEtlProcessAware);
			assertEquals(0,itemSum);
			assertEquals(this.elementCount,checkBeforeProcessAware);
			assertEquals(this.elementCount,checkAfterProcessAware);
			assertEquals(new Long(checkContextData*this.elementCount*2),(Long)this.etlProcessor.getProcessContext().get("cur_item"));
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

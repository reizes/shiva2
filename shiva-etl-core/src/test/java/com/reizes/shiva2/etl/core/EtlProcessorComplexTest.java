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

public class EtlProcessorComplexTest {

	private EtlProcessor etlProcessor;
	private LinkedList<EtlElement> elementList;
	private int elementCount = 0;
	private int listCount = 5;
	private boolean[] listFlag = new boolean[] {true, false, true, true, false};
	private int itemCount = 100;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlProcessor = new EtlProcessor();

		this.elementList = new LinkedList<EtlElement>();

		for (int i = 0; i < listFlag.length; i++) {
			if (listFlag[i]) {
				EtlElementList list = new EtlElementList();
				for (int j = 0; j < listCount; j++) {
					EtlElement element = new AwareTestElement();
					list.addElement(element);
					elementCount++;
				}
				elementList.add(list);
			} else {
				EtlElement element = new AwareTestElement();
				elementList.add(element);
				elementCount++;
			}
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor = null;
		this.elementList.clear();
		this.elementList = null;
	}

	private class ListenerTest implements BeforeProcessListener, AfterProcessListener, BeforeItemProcessListener,
		AfterItemProcessListener {
		public boolean beforeProcessCheck = false;
		public boolean afterProcessCheck = false;
		public int beforeItemProcessNum = 0;
		public int afterItemProcessNum = 0;

		@Override
		public void onBeforeProcess(ProcessContext context, Object input) {
			beforeProcessCheck = true;
		}

		@Override
		public void onAfterProcess(ProcessContext context, Object output) {
			afterProcessCheck = true;
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

	public int itemSum = 0;
	public int checkEtlProcessAware = 0;

	private class AwareTestElement extends MockEtlElement implements BeforeItemProcessAware, AfterItemProcessAware,
		EtlProcessorAware, ProcessContextAware {

		private ProcessContext context;

		@Override
		public void onBeforeItemProcess(ProcessContext context, Object data) {
			Long input = (Long)data;
			itemSum += input.intValue();
			Long item = (Long)this.context.get("cur_item");
			this.context.put("cur_item", item + input);
		}

		@Override
		public void onAfterItemProcess(ProcessContext context, Object data) {
			Long input = (Long)data;
			itemSum -= input.intValue();
			Long item = (Long)this.context.get("cur_item");
			this.context.put("cur_item", item + input);
		}

		@Override
		public void setEtlProcessor(EtlProcessor processor) {
			if (processor != null)
				checkEtlProcessAware++;
		}

		@Override
		public void setProcessContext(ProcessContext context) {
			this.context = context;
			Long item = (Long)this.context.get("cur_item");
			if (item == null)
				this.context.put("cur_item", new Long(0));
		}

	}

	@Test
	public void testDoProcess() {
		assertEquals(this.elementList.size(), this.listCount);
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(), this.listCount);

		MockExtractor extractor = new MockExtractor();
		LinkedList<Object> mockData = new LinkedList<Object>();
		int checkContextData = 0;
		for (int i = 0; i < this.itemCount; i++) {
			mockData.add(new Long(i));
			checkContextData += i;
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);
		ListenerTest testListener = new ListenerTest();

		this.etlProcessor.setBeforeProcessListener(testListener);
		this.etlProcessor.setBeforeItemProcessListener(testListener);
		this.etlProcessor.setAfterProcessListener(testListener);
		this.etlProcessor.setAfterItemProcessListener(testListener);

		try {
			this.etlProcessor.doProcess(null);
			int critNum = this.elementCount * this.itemCount;
			// element process check
			assertEquals(critNum, MockEtlElement.getProcessCount());
			// listener check
			assertTrue(testListener.beforeProcessCheck);
			assertTrue(testListener.afterProcessCheck);
			assertEquals(critNum, testListener.beforeItemProcessNum);
			assertEquals(critNum, testListener.afterItemProcessNum);
			// aware check
			assertEquals(this.elementCount, checkEtlProcessAware);
			assertEquals(0, itemSum);
			assertEquals(new Long(checkContextData * this.elementCount * 2),
				(Long)this.etlProcessor.getProcessContext().get("cur_item"));
			assertEquals(ExecutionStatus.STOP, etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED, etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

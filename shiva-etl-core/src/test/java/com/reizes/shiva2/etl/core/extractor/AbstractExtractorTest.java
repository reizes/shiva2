package com.reizes.shiva2.etl.core.extractor;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.EtlProcessor;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;

public class AbstractExtractorTest {
	private MockExtractor extractor;
	private int dataCount=100;

	@Before
	public void setUp() throws Exception {
		this.extractor=new MockExtractor();
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.dataCount;i++) {
			mockData.add(String.format("%d item", i+1));
		}
		this.extractor.setMockData(mockData);
	}

	@After
	public void tearDown() throws Exception {
		this.extractor=null;
	}

	@Test
	public void testDoProcessCauseException() {
		try {
			this.extractor.doProcess(null);
		} catch (Exception e) {
			assertTrue(e instanceof NullItemHandlerException);
			return;
		}
		fail("NullItemHandlerException not raised");
	}
	
	@Test
	public void testDoProcess() {
		EtlProcessor processor=new EtlProcessor();
		EtlElement element=new MockEtlElement();
		processor.setElement(element);
		this.extractor.setExtractedItemHandler(processor);
		try {
			this.extractor.doProcess(null);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		assertEquals(MockEtlElement.getProcessCount(),this.dataCount);
	}
}

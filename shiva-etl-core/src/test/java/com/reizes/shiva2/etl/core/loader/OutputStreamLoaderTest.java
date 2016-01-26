package com.reizes.shiva2.etl.core.loader;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.EtlProcessor;
import com.reizes.shiva2.etl.core.ExecutionStatus;
import com.reizes.shiva2.etl.core.InvalidPropertyException;
import com.reizes.shiva2.etl.core.ProcessStatus;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;
import com.reizes.shiva2.etl.core.mock.MockOutputStream;

public class OutputStreamLoaderTest {
	private EtlProcessor etlProcessor;
	private LinkedList<EtlElement> elementList;
	private int elementCount = 5;
	private int itemCount = 100;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlProcessor = new EtlProcessor();

		this.elementList = new LinkedList<EtlElement>();
		for (int i = 0; i < elementCount; i++) {
			EtlElement element = new MockEtlElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor = null;
		this.elementList.clear();
		this.elementList = null;
	}

	@Test
	public void testDoProcessNullStream() {
		assertEquals(this.elementList.size(), this.elementCount);
		this.elementList.add(new OutputStreamLoader());
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(), this.elementCount + 1);

		MockExtractor extractor = new MockExtractor();
		LinkedList<Object> mockData = new LinkedList<Object>();
		for (int i = 0; i < this.itemCount; i++) {
			mockData.add(i + 1);
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			fail("InvalidPropertyException Not Raised!");
		} catch (InvalidPropertyException e) {
			assertEquals(ExecutionStatus.STOP, etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FAILED, etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoProcessInt() {
		assertEquals(this.elementList.size(), this.elementCount);
		OutputStreamLoader loader = new OutputStreamLoader();
		MockOutputStream outputStream = new MockOutputStream();
		loader.setOutputStream(outputStream);
		this.elementList.add(loader);
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(), this.elementCount + 1);

		MockExtractor extractor = new MockExtractor();
		LinkedList<Object> mockData = new LinkedList<Object>();
		for (int i = 0; i < this.itemCount; i++) {
			mockData.add(i + 1);
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			int critNum = this.elementCount * this.itemCount;
			// element process check
			assertEquals(critNum, MockEtlElement.getProcessCount());

			assertTrue(outputStream.equals(mockData));

			assertEquals(ExecutionStatus.STOP, etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED, etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoProcessByteArray() {
		assertEquals(this.elementList.size(), this.elementCount);
		OutputStreamLoader loader = new OutputStreamLoader();
		MockOutputStream outputStream = new MockOutputStream();
		loader.setOutputStream(outputStream);
		this.elementList.add(loader);
		this.etlProcessor.setElementList(this.elementList);
		assertNotNull(this.etlProcessor.getElementList());
		assertEquals(this.etlProcessor.getElementList().size(), this.elementCount + 1);

		MockExtractor extractor = new MockExtractor();
		LinkedList<Object> mockData = new LinkedList<Object>();
		for (int i = 0; i < this.itemCount; i++) {
			mockData.add(new byte[] {(byte)i, (byte)(i + 1), (byte)(i + 2), (byte)(i + 3), (byte)(i + 4)});
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			int critNum = this.elementCount * this.itemCount;
			// element process check
			assertEquals(critNum, MockEtlElement.getProcessCount());

			StringBuilder sb1 = new StringBuilder();
			StringBuilder sb2 = new StringBuilder();

			for (Object arr : mockData) {
				for (byte b : (byte[])arr) {
					sb1.append(b);
				}
			}

			for (Object arr : outputStream.getOutputList()) {
				for (byte b : (byte[])arr) {
					sb2.append(b);
				}
			}

			assertTrue("Not Equal Stream", sb1.toString().equals(sb2.toString()));

			assertEquals(ExecutionStatus.STOP, etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED, etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

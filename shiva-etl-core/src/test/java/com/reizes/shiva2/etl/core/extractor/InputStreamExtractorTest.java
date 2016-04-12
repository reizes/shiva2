package com.reizes.shiva2.etl.core.extractor;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.extractor.InputStreamExtractor;
import com.reizes.shiva2.core.loader.OutputStreamLoader;
import com.reizes.shiva2.etl.core.mock.MockInputStream;
import com.reizes.shiva2.etl.core.mock.MockOutputStream;

public class InputStreamExtractorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoProcess() {
		byte[] data=new byte[] {0,10,123,127,12,123,112,2,3,1,54,123,43,120,1,92,41,19,100,5,123,13,27,52,36};
		MockInputStream inputStream=new MockInputStream();
		inputStream.setInputData(data);
		MockOutputStream outputStream=new MockOutputStream();
		
		TasksProcessor processor=new TasksProcessor();
		processor.addTask(new OutputStreamLoader(outputStream));
		processor.setExtractor(new InputStreamExtractor(inputStream));
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		for(int b : data) list.add(new Integer(b));
		
		try {
			processor.doProcess(null);
			// element process check
			assertTrue(outputStream.equals(list));
			assertEquals(ExecutionStatus.STOP,processor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,processor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		
	}

}

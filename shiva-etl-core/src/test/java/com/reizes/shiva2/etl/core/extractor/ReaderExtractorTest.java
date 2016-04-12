package com.reizes.shiva2.etl.core.extractor;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.extractor.ReaderExtractor;
import com.reizes.shiva2.core.loader.WriterLoader;
import com.reizes.shiva2.etl.core.mock.MockReader;
import com.reizes.shiva2.etl.core.mock.MockWriter;

public class ReaderExtractorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoProcess() {
		char[] data="reizes ZZANG Reader Extractor Test Mock Data".toCharArray();
		MockReader reader=new MockReader();
		reader.setInputData(data);
		MockWriter writer=new MockWriter();
		
		TasksProcessor processor=new TasksProcessor();
		processor.addTask(new WriterLoader(writer));
		processor.setExtractor(new ReaderExtractor(reader));
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		for(int b : data) list.add(new Integer(b));
		
		try {
			processor.doProcess(null);
			// element process check
			assertTrue(writer.equals(list));
			assertEquals(ExecutionStatus.STOP,processor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,processor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

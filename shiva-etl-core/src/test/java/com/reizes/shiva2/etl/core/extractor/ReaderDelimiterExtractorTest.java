package com.reizes.shiva2.etl.core.extractor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.extractor.ReaderDelimiterExtractor;
import com.reizes.shiva2.core.loader.WriterLoader;
import com.reizes.shiva2.etl.core.mock.MockReader;
import com.reizes.shiva2.etl.core.mock.MockWriter;

public class ReaderDelimiterExtractorTest {

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
		
		ReaderDelimiterExtractor extractor=new ReaderDelimiterExtractor(reader);
		extractor.setDelimiter(' ');
		
		TasksProcessor processor=new TasksProcessor();
		processor.addTask(new WriterLoader(writer));
		processor.setExtractor(extractor);
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		list.add("reizes".toCharArray());
		list.add("ZZANG".toCharArray());
		list.add("Reader".toCharArray());
		list.add("Extractor".toCharArray());
		list.add("Test".toCharArray());
		list.add("Mock".toCharArray());
		list.add("Data".toCharArray());
		
		try {
			processor.doProcess(null);
			// element process check
			List<Object> outlist=writer.getOutputList();
			Iterator<Object> iter1=list.iterator();
			Iterator<Object> iter2=outlist.iterator();
			for(;iter1.hasNext();) {
				char[] arr1=(char[])iter1.next();
				char[] arr2=(char[])iter2.next();
				if (Arrays.equals(arr1,arr2)) continue;
				else fail("Output data not matched");
			}
			
			//assertTrue(outputStream.equals(list));
			assertEquals(ExecutionStatus.STOP,processor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,processor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

}

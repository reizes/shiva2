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
import com.reizes.shiva2.core.extractor.InputStreamLeadByteExtractor;
import com.reizes.shiva2.core.loader.OutputStreamLoader;
import com.reizes.shiva2.etl.core.mock.MockInputStream;
import com.reizes.shiva2.etl.core.mock.MockOutputStream;

public class InputStreamLeadByteExtractorTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testDoProcessNotEscape() {
		byte[] data=new byte[] {0,10,123,127,0,123,112,0,3,1,54,123,43,120,1,92,41,0,100,5,0,13,0,52,36};
		MockInputStream inputStream=new MockInputStream();
		inputStream.setInputData(data);
		MockOutputStream outputStream=new MockOutputStream();
		
		InputStreamLeadByteExtractor extractor=new InputStreamLeadByteExtractor(inputStream);
		extractor.setLeadByte((byte)0);
		
		TasksProcessor processor=new TasksProcessor();
		processor.addTask(new OutputStreamLoader(outputStream));
		processor.setExtractor(extractor);
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		list.add(new byte[] {10,123,127});
		list.add(new byte[] {3,1,54,123,43,120,1,92,41});
		list.add(new byte[] {13});
		
		try {
			processor.doProcess(null);
			// element process check
			List<Object> outlist=outputStream.getOutputList();
			Iterator<Object> iter1=list.iterator();
			Iterator<Object> iter2=outlist.iterator();
			for(;iter1.hasNext();) {
				byte[] arr1=(byte[])iter1.next();
				byte[] arr2=(byte[])iter2.next();
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

	@Test
	public void testDoProcessEscape() {
		byte[] data=new byte[] {0,10,123,100,0,123,112,0,3,1,0,123,43,100,100,92,41,0,100,5,0,13,0,52,36};
		MockInputStream inputStream=new MockInputStream();
		inputStream.setInputData(data);
		MockOutputStream outputStream=new MockOutputStream();
		
		InputStreamLeadByteExtractor extractor=new InputStreamLeadByteExtractor(inputStream);
		extractor.setLeadByte((byte)0);
		extractor.setEscapeByte((byte)100);
		
		TasksProcessor processor=new TasksProcessor();
		processor.addTask(new OutputStreamLoader(outputStream));
		processor.setExtractor(extractor);
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		list.add(new byte[] {10,123,0,123,112});
		list.add(new byte[] {123,43,100,92,41});
		list.add(new byte[] {13});
		
		try {
			processor.doProcess(null);
			// element process check
			List<Object> outlist=outputStream.getOutputList();
			Iterator<Object> iter1=list.iterator();
			Iterator<Object> iter2=outlist.iterator();
			for(;iter1.hasNext();) {
				byte[] arr1=(byte[])iter1.next();
				byte[] arr2=(byte[])iter2.next();
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

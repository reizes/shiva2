package com.reizes.shiva2.etl.core.extractor;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.etl.core.EtlProcessor;
import com.reizes.shiva2.etl.core.ExecutionStatus;
import com.reizes.shiva2.etl.core.ProcessStatus;
import com.reizes.shiva2.etl.core.loader.OutputStreamLoader;
import com.reizes.shiva2.etl.core.mock.MockInputStream;
import com.reizes.shiva2.etl.core.mock.MockOutputStream;

public class InputStreamFixedLengthExtractorTest {

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
		int itemLength=3;
		
		InputStreamFixedLengthExtractor extractor=new InputStreamFixedLengthExtractor(inputStream);
		extractor.setItemLength(itemLength);
		
		EtlProcessor processor=new EtlProcessor();
		processor.addElement(new OutputStreamLoader(outputStream));
		processor.setExtractor(extractor);
		
		// create list for check
		LinkedList<Object> list=new LinkedList<Object>();
		for(int i=0;i<data.length;) {
			byte[] arr=new byte[itemLength];
			for(int cur=0;cur<itemLength && i<data.length;cur++) {
				arr[cur]=data[i];
				i++;
			}
			list.add(arr);
		}
		
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

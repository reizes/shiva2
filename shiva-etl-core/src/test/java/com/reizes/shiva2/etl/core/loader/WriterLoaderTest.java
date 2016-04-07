package com.reizes.shiva2.etl.core.loader;

import static org.junit.Assert.*;

import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.reizes.shiva2.core.ExecutionStatus;
import com.reizes.shiva2.core.InvalidPropertyException;
import com.reizes.shiva2.core.ProcessStatus;
import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.TasksProcessor;
import com.reizes.shiva2.core.loader.WriterLoader;
import com.reizes.shiva2.etl.core.mock.MockEtlElement;
import com.reizes.shiva2.etl.core.mock.MockExtractor;
import com.reizes.shiva2.etl.core.mock.MockWriter;

public class WriterLoaderTest {
	private TasksProcessor etlProcessor;
	private LinkedList<Task> elementList;
	private int elementCount=5;
	private int itemCount=100;

	@Before
	public void setUp() throws Exception {
		MockEtlElement.resetProcessCount();
		this.etlProcessor=new TasksProcessor();
		
		this.elementList=new LinkedList<Task>(); 
		for(int i=0;i<elementCount;i++) {
			Task element=new MockEtlElement();
			elementList.add(element);
		}
	}

	@After
	public void tearDown() throws Exception {
		this.etlProcessor=null;
		this.elementList.clear();
		this.elementList=null;
	}

	@Test
	public void testDoProcessNullWriter() {
		assertEquals(this.elementList.size(),this.elementCount);
		this.elementList.add(new WriterLoader());
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount+1);
		
		MockExtractor extractor=new MockExtractor(); 
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(i+1);
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			fail("InvalidPropertyException Not Raised!");
		} catch (InvalidPropertyException e) {
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FAILED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDoProcessInt() {
		assertEquals(this.elementList.size(),this.elementCount);
		WriterLoader loader=new WriterLoader();
		MockWriter writer=new MockWriter();
		loader.setWriter(writer);
		this.elementList.add(loader);
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount+1);
		
		MockExtractor extractor=new MockExtractor(); 
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(i+1);
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// element process check
			assertEquals(critNum,MockEtlElement.getProcessCount());
			
			assertTrue(writer.equals(mockData));
			
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testDoProcessCharArray() {
		assertEquals(this.elementList.size(),this.elementCount);
		WriterLoader loader=new WriterLoader();
		MockWriter writer=new MockWriter();
		loader.setWriter(writer);
		this.elementList.add(loader);
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount+1);
		
		MockExtractor extractor=new MockExtractor(); 
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add(new char[] {(char)i,(char)(i+1),(char)(i+2),(char)(i+3),(char)(i+4)});
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// element process check
			assertEquals(critNum,MockEtlElement.getProcessCount());
			
			assertTrue("Not Equal Stream",writer.equals(mockData));
			
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testDoProcessString() {
		assertEquals(this.elementList.size(),this.elementCount);
		WriterLoader loader=new WriterLoader();
		MockWriter writer=new MockWriter();
		loader.setWriter(writer);
		this.elementList.add(loader);
		this.etlProcessor.setTasks(this.elementList);
		assertNotNull(this.etlProcessor.getTasks());
		assertEquals(this.etlProcessor.getTasks().size(),this.elementCount+1);
		
		MockExtractor extractor=new MockExtractor(); 
		LinkedList<Object> mockData=new LinkedList<Object>();
		for(int i=0;i<this.itemCount;i++) {
			mockData.add("InputData ["+(i+1)+"]");
		}
		extractor.setMockData(mockData);
		this.etlProcessor.setExtractor(extractor);

		try {
			this.etlProcessor.doProcess(null);
			int critNum=this.elementCount*this.itemCount;
			// element process check
			assertEquals(critNum,MockEtlElement.getProcessCount());
			
			assertTrue(writer.equals(mockData));
			
			assertEquals(ExecutionStatus.STOP,etlProcessor.getProcessContext().getExecutionStatus());
			assertEquals(ProcessStatus.FINISHED,etlProcessor.getProcessContext().getProcessStatus());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}

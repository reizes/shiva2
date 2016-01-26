package com.reizes.shiva2.etl.core.element;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.reizes.shiva2.etl.core.EtlProcessor;
import com.reizes.shiva2.etl.core.extractor.ArrayExtractor;
import com.reizes.shiva2.etl.core.loader.ListLoader;

public class OffsetLimitProcessorTest {

	@Test
	public void testOffset() throws Exception {
		List<Integer> resultList = new ArrayList<>();
		EtlProcessor testProcessor = new EtlProcessor();
		testProcessor.setExtractor(new ArrayExtractor<Integer>())
		.addElement(new OffsetLimitProcessor(5))
		.addElement(new ListLoader<Integer>(resultList));
		
		Integer[] data = {1,2,3,4,5,6,7,8,9,10};
		Integer[] expected = {6,7,8,9,10};
		testProcessor.doProcess(data);
		
		assertEquals(5, resultList.size());
		assertArrayEquals(expected, resultList.toArray());
	}

	@Test
	public void testLimit() throws Exception {
		List<Integer> resultList = new ArrayList<>();
		EtlProcessor testProcessor = new EtlProcessor();
		testProcessor.setExtractor(new ArrayExtractor<Integer>())
		.addElement(new OffsetLimitProcessor(0, 3))
		.addElement(new ListLoader<Integer>(resultList));
		
		Integer[] data = {1,2,3,4,5,6,7,8,9,10};
		Integer[] expected = {1,2,3};
		testProcessor.doProcess(data);
		
		assertEquals(3, resultList.size());
		assertArrayEquals(expected, resultList.toArray());
	}

	@Test
	public void testOffsetLimit() throws Exception {
		List<Integer> resultList = new ArrayList<>();
		EtlProcessor testProcessor = new EtlProcessor();
		testProcessor.setExtractor(new ArrayExtractor<Integer>())
		.addElement(new OffsetLimitProcessor(2, 4))
		.addElement(new ListLoader<Integer>(resultList));
		
		Integer[] data = {1,2,3,4,5,6,7,8,9,10};
		Integer[] expected = {3, 4, 5, 6};
		testProcessor.doProcess(data);
		
		assertEquals(4, resultList.size());
		assertArrayEquals(expected, resultList.toArray());
	}

}

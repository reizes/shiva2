package com.reizes.shiva2.etl.core.mock;

import java.util.Iterator;
import java.util.List;

import com.reizes.shiva2.core.extractor.AbstractExtractor;

public class MockExtractor extends AbstractExtractor {

	private List<Object> mockData;

	@Override
	public Object doProcess(Object input) throws Exception {
		//System.out.println("MockExtractor doProcess Start");
		Object output = null;
		//int num=1;
		for (Iterator<Object> iter = mockData.iterator(); iter.hasNext();) {
			Object item = iter.next();
			//System.out.println("["+item.toString()+"]");
			output = startProcessItem(item);
			//System.out.println("["+num+"]"+item+" processed");
		}
		//System.out.println("MockExtractor doProcess End");
		return output;
	}

	public void setMockData(List<Object> mockData) {
		this.mockData = mockData;
	}
}

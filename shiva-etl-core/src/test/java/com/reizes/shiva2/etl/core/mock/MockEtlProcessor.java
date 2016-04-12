package com.reizes.shiva2.etl.core.mock;

import com.reizes.shiva2.core.Tasks;
import com.reizes.shiva2.core.extractor.ExtractedItemHandler;

public class MockEtlProcessor extends Tasks implements
		ExtractedItemHandler {

	@Override
	public Object processExtractedItem(Object item) throws Exception {
		return doProcess(item);
	}

}

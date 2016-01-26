package com.reizes.shiva2.etl.core.mock;

import com.reizes.shiva2.etl.core.EtlElementList;
import com.reizes.shiva2.etl.core.extractor.ExtractedItemHandler;

public class MockEtlProcessor extends EtlElementList implements
		ExtractedItemHandler {

	@Override
	public Object processExtractedItem(Object item) throws Exception {
		return doProcess(item);
	}

}

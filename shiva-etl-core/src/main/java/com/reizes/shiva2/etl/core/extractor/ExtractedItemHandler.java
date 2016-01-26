package com.reizes.shiva2.etl.core.extractor;

public interface ExtractedItemHandler {
	public Object processExtractedItem(Object item) throws Exception;
}

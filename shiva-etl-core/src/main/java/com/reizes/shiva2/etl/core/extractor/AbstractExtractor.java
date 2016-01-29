package com.reizes.shiva2.etl.core.extractor;

import com.reizes.shiva2.etl.core.context.ProcessContext;

/**
 * 
 * @author kane
 *
 * @since 2.0.0
 */
public abstract class AbstractExtractor implements Extractor {

	private ExtractedItemHandler extractedItemHandler;

	@Override
	public final void setExtractedItemHandler(ExtractedItemHandler extractedItemHandler) {
		this.extractedItemHandler = extractedItemHandler;
	}

	protected Object startProcessItem(Object item) throws Exception {
		if (this.extractedItemHandler != null) {
			return this.extractedItemHandler.processExtractedItem(item);
		}
		
		throw new NullItemHandlerException("ExtractedItemHandler is NULL");
	}
	
	/**
	 * Will be called after call setExtractor method of EtlProcessor
	 * @since 2.0.0
	 */
	public void onAfterSetExtractorCallback(ProcessContext context) {
		
	}

}

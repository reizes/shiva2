package com.reizes.shiva2.management;

import com.reizes.shiva2.core.context.ProcessContext;
import com.reizes.shiva2.core.extractor.ExtractedItemHandler;
import com.reizes.shiva2.core.extractor.Extractor;
import com.reizes.shiva2.core.extractor.NullItemHandlerException;

/**
 * 
 * @author kane
 *
 * @since 2.0.0
 */
public abstract class AbstractNotificatableExtractor extends AbstractNotificatableTask implements Extractor {

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

package com.reizes.shiva2.etl.core.extractor;

import com.reizes.shiva2.etl.core.EtlElement;
import com.reizes.shiva2.etl.core.context.ProcessContext;

public interface Extractor extends EtlElement {
	public void setExtractedItemHandler(ExtractedItemHandler extractedItemHandler);
	public void onAfterSetExtractorCallback(ProcessContext context);
}

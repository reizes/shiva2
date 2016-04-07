package com.reizes.shiva2.core.reader;

import com.reizes.shiva2.core.Task;
import com.reizes.shiva2.core.context.ProcessContext;

public interface Extractor extends Task {
	public void setExtractedItemHandler(ExtractedItemHandler extractedItemHandler);
	public void onAfterSetExtractorCallback(ProcessContext context);
}

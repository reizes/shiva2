package com.reizes.shiva2.etl.core.extractor;

/**
 * 입력받은 item을 그대로 startProcessItem으로 전달하는 extractor
 * @author reizes
 * @since 2010.1.26
 */
public class BypassExtactor extends AbstractExtractor {
	@Override
	public Object doProcess(Object input) throws Exception {
		return startProcessItem(input);
	}

}

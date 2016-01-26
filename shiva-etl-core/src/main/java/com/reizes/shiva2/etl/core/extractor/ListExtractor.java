package com.reizes.shiva2.etl.core.extractor;

import java.util.List;

/**
 * List의 item들을 하나씩 extract 하는 extractor
 * @author reizes
 * @param <T> - item class
 * @since 2.0.2
 * @since 2010.4.27
 */
public class ListExtractor<T> extends AbstractExtractor {

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		List<T> list = (List<T>)input;

		for (T item : list) {
			startProcessItem(item);
		}

		return list;
	}

}

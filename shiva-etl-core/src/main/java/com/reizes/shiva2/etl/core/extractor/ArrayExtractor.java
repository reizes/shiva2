package com.reizes.shiva2.etl.core.extractor;

/**
 * Array의 item들을 하나씩 extract 하는 extractor
 * @author reizes
 * @param <T> - item class
 * @since 2.0.0
 * @since 2016.1.18
 */
public class ArrayExtractor<T> extends AbstractExtractor {

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		T[] list = (T[])input;

		for (T item : list) {
			startProcessItem(item);
		}

		return list;
	}

}

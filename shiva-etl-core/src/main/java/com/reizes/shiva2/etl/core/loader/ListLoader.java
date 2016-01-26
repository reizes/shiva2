package com.reizes.shiva2.etl.core.loader;

import java.util.List;

/**
 * load item into List
 * 
 * @author reizes
 *
 */
public class ListLoader<T> extends AbstractLoader {
	private List<T> list;

	public ListLoader() {

	}

	public ListLoader(List<T> list) {
		this.list = list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object doProcess(Object input) throws Exception {
		list.add((T) input);

		return input;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}
}

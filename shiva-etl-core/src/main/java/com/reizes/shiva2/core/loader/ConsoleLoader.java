package com.reizes.shiva2.core.loader;

public class ConsoleLoader extends AbstractLoader {
	
	private boolean printNull=false;
	
	@Override
	public Object doProcess(Object input) throws Exception {

		if (input != null) {
			System.out.println(input.toString());
		} else if (printNull) {
			System.out.println("NULL");
		}

		return input;
	}

	public boolean isPrintNull() {
		return printNull;
	}

	public void setPrintNull(boolean printNull) {
		this.printNull = printNull;
	}
}

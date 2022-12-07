package org.moeaframework.util.format;

import java.io.PrintStream;

public interface Formattable<T> {
	
	public TabularData<T> asTabularData();
	
	public default void display() {
		display(System.out);
	}
	
	public default void display(PrintStream out) {
		TabularData<T> data = asTabularData();
		data.display(out);
	}

}

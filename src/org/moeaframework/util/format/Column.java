package org.moeaframework.util.format;

import java.util.function.Function;

public class Column<T, V> {
	
	private final String name;
	
	private final Function<T, V> supplier;
	
	private Formatter<V> customFormatter;
	
	public Column(String name, Function<T, V> supplier) {
		super();
		this.name = name;
		this.supplier = supplier;
	}
	
	public Formatter<V> getCustomFormatter() {
		return customFormatter;
	}

	public void setCustomFormatter(Formatter<V> customFormatter) {
		this.customFormatter = customFormatter;
	}

	public String getName() {
		return name;
	}
	
	public V getValue(T row) {
		return supplier.apply(row);
	}

}

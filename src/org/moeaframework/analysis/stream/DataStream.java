package org.moeaframework.analysis.stream;

import java.util.stream.Stream;

import org.moeaframework.util.format.Column;
import org.moeaframework.util.format.Formattable;
import org.moeaframework.util.format.TabularData;

/**
 * Interface for a data stream.
 * 
 * @param <V> the type of each value in the stream
 */
public interface DataStream<V> extends Formattable<V> {
	
	public Stream<V> stream();
	
	public DataStream<V> sorted();
	
	public DataStream<V> distinct();
	
	@Override
	public default TabularData<V> asTabularData() {
		TabularData<V> table = new TabularData<V>(stream().toList());
		table.addColumn(new Column<V, V>("Data", Mappings.identity()));
		return table;
	}

}

package org.moeaframework.util.format;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;

public class TabularData<T> {
	
	private final Iterable<T> dataSource;
	
	private final List<Column<T, ?>> columns;
	
	private final Map<Class<?>, Formatter<?>> defaultFormatters;
	
	public TabularData(Iterable<T> dataSource) {
		super();
		this.dataSource = dataSource;
		this.columns = new ArrayList<Column<T, ?>>();
		this.defaultFormatters = new HashMap<Class<?>, Formatter<?>>();
		
		addDefaultFormatter(new NumberFormatter());
		addDefaultFormatter(new VariableFormatter());
	}
	
	public void addDefaultFormatter(Formatter<?> formatter) {
		defaultFormatters.put(formatter.getType(), formatter);
	}
	
	public void addColumn(Column<T, ?> column) {
		this.columns.add(column);
	}
	
	private String format(T row, Column<T, ?> column) {
		Object value = column.getValue(row);
		
		if (column.getCustomFormatter() != null) {
			return column.getCustomFormatter().format(value);
		}
		
		for (Class<?> type : defaultFormatters.keySet()) {
			if (TypeUtils.isInstance(value, type)) {
				return defaultFormatters.get(type).format(value);
			}
		}
		
		return value.toString();
	}
	
	public void display() {
		display(System.out);
	}
	
	public void display(PrintStream out) {
		List<String[]> formattedData = new ArrayList<String[]>();
		int[] columnWidths = new int[columns.size()];
		
		for (T record : dataSource) {
			String[] row = new String[columns.size()];
			
			for (int j = 0; j < columns.size(); j++) {
				row[j] = format(record, columns.get(j));
				columnWidths[j] = Math.max(columnWidths[j], row[j].length());
			}
			
			formattedData.add(row);
		}
		
		for (int j = 0; j < columns.size(); j++) {
			String columnName = columns.get(j).getName();
			columnWidths[j] = Math.max(columnWidths[j], columnName.length());
			out.print(StringUtils.rightPad(columnName, columnWidths[j]+1));
		}
		
		out.println();
		
		for (int j = 0; j < columns.size(); j++) {
			out.print(StringUtils.repeat('-', columnWidths[j]));
			out.print(' ');
		}
		
		out.println();
		
		for (int i = 0; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				out.print(StringUtils.rightPad(formattedData.get(i)[j], columnWidths[j]+1));
			}
			
			out.println();
		}
	}

}
package org.moeaframework.util.format;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.moeaframework.core.Settings;

/**
 * Formats and displays tabular data.
 *
 * @param <T> the type of records (rows)
 */
public class TabularData<T> implements Displayable {
	
	private final Iterable<T> dataSource;
	
	private final List<Column<T, ?>> columns;
	
	private final Deque<Formatter<?>> formatters;
	
	/**
	 * Creates a new tabular data object using the given data source.
	 * 
	 * @param dataSource the source of data
	 */
	public TabularData(Iterable<T> dataSource) {
		super();
		this.dataSource = dataSource;
		this.columns = new ArrayList<Column<T, ?>>();
		this.formatters = new LinkedList<Formatter<?>>();
		
		addFormatter(new NumberFormatter());
		addFormatter(new VariableFormatter(this));
	}
	
	/**
	 * Adds a formatter.  This will be used to format any values matching the
	 * formatter's type (see {@link Formatter#getType()}.
	 * 
	 * @param formatter the default formatter
	 */
	public void addFormatter(Formatter<?> formatter) {
		formatters.push(formatter);
	}
	
	/**
	 * Removes any existing formatters.
	 */
	public void removeAllFormatters() {
		formatters.clear();
	}
	
	/**
	 * Adds a column to this data.  The columns will be displayed in the order they
	 * are added.
	 * 
	 * @param column the column
	 */
	public void addColumn(Column<T, ?> column) {
		this.columns.add(column);
	}
	
	/**
	 * Reads the value of the specified column and formats it as a string.
	 * 
	 * @param row the record (row) to read from
	 * @param column the column to read from
	 * @return the formatted value
	 */
	protected String format(T row, Column<T, ?> column) {
		Object value = column.getValue(row);
		
		if (column.getCustomFormatter() != null) {
			return column.getCustomFormatter().format(value);
		}
		
		return formatValue(value);
	}
	
	/**
	 * Formats the value using the default formatter, if available, or with {@link #toString()}.
	 * 
	 * @param value the value to format
	 * @return the formatted value
	 */
	protected String formatValue(Object value) {
		for (Formatter<?> formatter : formatters) {
			if (TypeUtils.isInstance(value, formatter.getType())) {
				return formatter.format(value);
			}
		}
		
		return value.toString();
	}
	
	@Override
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
	
	/**
	 * Saves the data to a CSV file.
	 * 
	 * @param file the resulting file
	 * @throws IOException if an I/O error occurred while writing the file
	 */
	public void saveCSV(File file) throws IOException {
		try (FileWriter writer = new FileWriter(file)) {
			toCSV(writer);
		}
	}
	
	/**
	 * Writes the data formatted as CSV.
	 * 
	 * @param out the output writer
	 * @throws IOException if an I/O error occurred while writing the data
	 */
	public void toCSV(Writer out) throws IOException {
		for (int i = 0; i < columns.size(); i++) {
			if (i > 0) {
				out.write(", ");
			}
			
			out.write(StringEscapeUtils.escapeCsv(columns.get(i).getName()));
		}
		
		out.write(Settings.NEW_LINE);

		for (T record : dataSource) {
			for (int i = 0; i < columns.size(); i++) {
				if (i > 0) {
					out.write(", ");
				}
				
				out.write(StringEscapeUtils.escapeCsv(format(record, columns.get(i))));
			}
			
			out.write(Settings.NEW_LINE);
		}
	}

}

/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.util.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.text.StringEscapeUtils;

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
		
		addFormatter(NumberFormatter.getDefault());
		addFormatter(new VariableFormatter(this));
	}
	
	/**
	 * Adds a formatter.  This will be used to format any values matching the formatter's type (see
	 * {@link Formatter#getType()}.
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
	 * Adds a column to this data.  The columns will be displayed in the order they are added.
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
		List<String[]> formattedData = toFixedWidthFormat();

		for (int j = 0; j < columns.size(); j++) {
			String columnName = columns.get(j).getName();
			out.print(StringUtils.rightPad(columnName, columns.get(j).getWidth()+1));
		}
		
		out.println();
		
		for (int j = 0; j < columns.size(); j++) {
			out.print(StringUtils.repeat('-', columns.get(j).getWidth()));
			out.print(' ');
		}
		
		out.println();
		
		for (int i = 0; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				out.print(StringUtils.rightPad(formattedData.get(i)[j], columns.get(j).getWidth()+1));
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
		try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
			toCSV(out);
		}
	}
	
	/**
	 * Writes the data formatted as CSV.
	 * 
	 * @param out the output stream
	 * @throws IOException if an I/O error occurred while writing the data
	 */
	public void toCSV(PrintStream out) throws IOException {
		List<String[]> formattedData = toFixedWidthFormat();
		
		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(", ");
			}
			
			out.print(StringEscapeUtils.escapeCsv(columns.get(j).getName()));
		}
		
		out.println();

		for (int i = 0; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				if (j > 0) {
					out.print(", ");
				}
				
				out.print(StringEscapeUtils.escapeCsv(formattedData.get(i)[j]));
			}
			
			out.println();
		}
	}
	
	/**
	 * Writes the data formatted as a Markdown table.
	 * 
	 * @param out the output stream
	 * @throws IOException if an I/O error occurred while writing the data
	 */
	public void toMarkdown(PrintStream out) throws IOException {
		List<String[]> formattedData = toFixedWidthFormat();
		
		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(" | ");
			}
			
			String columnName = columns.get(j).getName();
			out.print(StringUtils.rightPad(columnName, columns.get(j).getWidth()));
		}
		
		out.println();
		
		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(" | ");
			}
			
			out.print(StringUtils.repeat('-', columns.get(j).getWidth()));
		}
		
		out.println();
		
		for (int i = 0; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				if (j > 0) {
					out.print(" | ");
				}
				
				out.print(StringUtils.rightPad(formattedData.get(i)[j], columns.get(j).getWidth()));
			}
			
			out.println();
		}
	}
	
	/**
	 * Writes the data formatted as a Latex table.
	 * 
	 * @param out the output stream
	 * @throws IOException if an I/O error occurred while writing the data
	 */
	public void toLatex(PrintStream out) throws IOException {
		List<String[]> formattedData = toFixedWidthFormat();

		out.print("\\begin{tabular}{|");
		
		for (int j = 0; j < columns.size(); j++) {
			out.print("l");
		}
		
		out.println("|}");
		out.println("  \\hline");
		out.print("  ");
		
		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(" & ");
			}
			
			String columnName = columns.get(j).getName();
			out.print(StringUtils.rightPad(columnName, columns.get(j).getWidth()));
		}
		
		out.println(" \\\\");
		out.println("  \\hline");
				
		for (int i = 0; i < formattedData.size(); i++) {
			out.print("  ");
			
			for (int j = 0; j < columns.size(); j++) {
				if (j > 0) {
					out.print(" & ");
				}
				
				out.print(StringUtils.rightPad(formattedData.get(i)[j], columns.get(j).getWidth()));
			}
			
			out.println(" \\\\");
		}
		
		out.println("  \\hline");
		out.println("\\end{tabular}");
	}
	
	/**
	 * Formats the data and computes the fixed column widths.  Note that the returned list only includes data rows
	 * and is not yet padded to the fixed width.
	 * 
	 * @return the formatted data
	 */
	private List<String[]> toFixedWidthFormat() {
		List<String[]> formattedData = new ArrayList<String[]>();
		
		// reset the column width
		for (int j = 0; j < columns.size(); j++) {
			columns.get(j).updateWidth(Column.UNSPECIFIED_WIDTH);
		}
		
		// calculate width for headers
		for (int j = 0; j < columns.size(); j++) {
			String columnName = columns.get(j).getName();
			columns.get(j).updateWidth(columnName.length());
		}
		
		// calculate width for data rows
		for (T record : dataSource) {
			String[] row = new String[columns.size()];
			
			for (int j = 0; j < columns.size(); j++) {
				row[j] = format(record, columns.get(j));
				columns.get(j).updateWidth(row[j].length());
			}
			
			formattedData.add(row);
		}
		
		return formattedData;
	}

}

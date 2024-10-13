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
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.CharSequenceTranslator;
import org.apache.commons.text.translate.LookupTranslator;
import org.moeaframework.util.validate.Validate;

/**
 * Formats and displays tabular data.
 *
 * @param <T> the type of records (rows)
 */
public class TabularData<T> implements Displayable {

	private static final CharSequenceTranslator ESCAPE_PLAINTEXT;

	private static final CharSequenceTranslator ESCAPE_LATEX;

	private static final CharSequenceTranslator ESCAPE_MARKDOWN;

	static {
		Map<CharSequence, CharSequence> escapeStandardMap = Map.of(
				"\b", "",
				"\n", "",
				"\t", "",
				"\f", "",
				"\r", "");

		Map<CharSequence, CharSequence> escapeLatexMap = Map.of(
				"&", "\\&");
		
		Map<CharSequence, CharSequence> escapeMarkdownMap = Map.of(
				"|", "&#124;",
				"\\", "\\\\");

		ESCAPE_PLAINTEXT = new LookupTranslator(escapeStandardMap);

		ESCAPE_LATEX = new AggregateTranslator(
				new LookupTranslator(escapeLatexMap),
				new LookupTranslator(escapeStandardMap));


		ESCAPE_MARKDOWN = new AggregateTranslator(
				new LookupTranslator(escapeMarkdownMap),
				new LookupTranslator(escapeStandardMap));
	}

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
		addFormatter(new ObjectiveFormatter(this));
		addFormatter(new ConstraintFormatter(this));
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
		display(TableFormat.Plaintext, out);
	}

	/**
	 * Displays the data in the given format to the terminal.
	 * 
	 * @param tableFormat the table format
	 */
	public void display(TableFormat tableFormat) {
		display(tableFormat, System.out);
	}

	/**
	 * Displays the data in the given format.
	 * 
	 * @param tableFormat the table format
	 * @param out the output stream
	 */
	public void display(TableFormat tableFormat, PrintStream out) {
		switch (tableFormat) {
		case Plaintext -> toPlaintext(out);
		case CSV -> toCSV(out);
		case Markdown -> toMarkdown(out);
		case Latex -> toLatex(out);
		case Json -> toJson(out);
		default -> Validate.that("tableFormat", tableFormat).failUnsupportedOption();
		}
	}

	/**
	 * Saves the data to a file in the requested format.
	 * 
	 * @param tableFormat the resulting table format
	 * @param file the resulting file
	 * @throws IOException if an I/O error occurred while writing the file
	 */
	public void save(TableFormat tableFormat, File file) throws IOException {
		try (PrintStream out = new PrintStream(new FileOutputStream(file))) {
			display(tableFormat, out);
		}
	}

	/**
	 * Writes the data in the default, plaintext format.
	 * 
	 * @param out the output stream
	 */
	protected void toPlaintext(PrintStream out) {
		List<String[]> formattedData = format(ESCAPE_PLAINTEXT);

		for (int j = 0; j < columns.size(); j++) {
			out.print(StringUtils.rightPad(formattedData.get(0)[j], columns.get(j).getWidth()+1));
		}

		out.println();

		for (int j = 0; j < columns.size(); j++) {
			out.print(StringUtils.repeat('-', columns.get(j).getWidth()));
			out.print(' ');
		}

		out.println();

		for (int i = 1; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				out.print(StringUtils.rightPad(formattedData.get(i)[j], columns.get(j).getWidth()+1));
			}

			out.println();
		}
	}

	/**
	 * Writes the data formatted as CSV.
	 * 
	 * @param out the output stream
	 */
	protected void toCSV(PrintStream out) {
		List<String[]> formattedData = format(StringEscapeUtils.ESCAPE_CSV);

		for (int i = 0; i < formattedData.size(); i++) {
			for (int j = 0; j < columns.size(); j++) {
				if (j > 0) {
					out.print(", ");
				}

				out.print(formattedData.get(i)[j]);
			}

			out.println();
		}
	}

	/**
	 * Writes the data formatted as a Markdown table.
	 * 
	 * @param out the output stream
	 */
	protected void toMarkdown(PrintStream out) {
		List<String[]> formattedData = format(ESCAPE_MARKDOWN);

		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(" | ");
			}

			out.print(StringUtils.rightPad(formattedData.get(0)[j], columns.get(j).getWidth()));
		}

		out.println();

		for (int j = 0; j < columns.size(); j++) {
			if (j > 0) {
				out.print(" | ");
			}

			out.print(StringUtils.repeat('-', columns.get(j).getWidth()));
		}

		out.println();

		for (int i = 1; i < formattedData.size(); i++) {
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
	 */
	protected void toLatex(PrintStream out) {
		List<String[]> formattedData = format(ESCAPE_LATEX);

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

			out.print(StringUtils.rightPad(formattedData.get(0)[j], columns.get(j).getWidth()));
		}

		out.println(" \\\\");
		out.println("  \\hline");

		for (int i = 1; i < formattedData.size(); i++) {
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
	 * Writes the data formatted as a Json object.  This format is designed to be compatible with Pandas, matching
	 * the output produced by {@code orient='record'}.
	 * <pre>
	 *   import pandas as pd
	 *   
	 *   df = pd.DataFrame(...)
	 *   df.to_json("data.json", orient='record')
	 *   
	 *   df = pd.read_json("data.json")
	 * </pre>
	 * 
	 * @param out the output stream
	 */
	protected void toJson(PrintStream out) {
		List<String[]> formattedData = format(StringEscapeUtils.ESCAPE_JSON);
		boolean[] isNumeric = new boolean[columns.size()];
		Pattern pattern = Pattern.compile("^-?[0-9]+(\\.[0-9]+(E-?[0-9]+)?)?$");

		// Scan data to determine if field is numeric or string
		for (int j = 0; j < columns.size(); j++) {
			isNumeric[j] = true;

			for (int i = 1; i < formattedData.size(); i++) {
				if (!pattern.matcher(formattedData.get(i)[j]).matches()) {
					isNumeric[j] = false;
					break;
				}
			}
		}

		out.print("[");

		for (int i = 1; i < formattedData.size(); i++) {
			if (i > 1) {
				out.print(",");
			}

			out.print("{");

			for (int j = 0; j < columns.size(); j++) {
				if (j > 0) {
					out.print(",");
				}

				out.print("\"");
				out.print(formattedData.get(0)[j]);
				out.print("\":");

				if (isNumeric[j]) {
					out.print(formattedData.get(i)[j]);
				} else {
					out.print("\"");
					out.print(formattedData.get(i)[j]);
					out.print("\"");
				}
			}

			out.print("}");
		}

		out.print("]");
		out.println();
	}

	/**
	 * Formats the data, escapes the resulting data for the target language, and computes the fixed column widths.
	 * The first row in the result contains the column headers, with remaining rows containing the row data.
	 * <p>
	 * Note that this method only computes the fixed width; it is the responsibility of the caller to apply any padding
	 * to the text as some output formats might not require or support padding.
	 * 
	 * @param translator translator that provides character escaping for the target language
	 * @return the formatted data
	 */
	private List<String[]> format(CharSequenceTranslator translator) {
		List<String[]> formattedData = new ArrayList<String[]>();

		// reset the column width
		for (int j = 0; j < columns.size(); j++) {
			columns.get(j).updateWidth(Column.UNSPECIFIED_WIDTH);
		}

		// calculate width for headers
		String[] header = new String[columns.size()];

		for (int j = 0; j < columns.size(); j++) {
			header[j] = translator.translate(columns.get(j).getName());
			columns.get(j).updateWidth(header[j].length());
		}

		formattedData.add(header);

		// calculate width for data rows
		for (T record : dataSource) {
			String[] row = new String[columns.size()];

			for (int j = 0; j < columns.size(); j++) {
				row[j] = translator.translate(format(record, columns.get(j)));
				columns.get(j).updateWidth(row[j].length());
			}

			formattedData.add(row);
		}

		return formattedData;
	}

}

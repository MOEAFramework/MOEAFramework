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

import java.util.function.Function;

/**
 * Defines a column in a {@link TabularData} instance.
 *
 * @param <T> the type for each record (row)
 * @param <V> the type of value in this column
 */
public class Column<T, V> {
	
	static final int UNSPECIFIED_WIDTH = -1;
	
	private final String name;
	
	private final Function<T, V> supplier;
	
	private Formatter<V> customFormatter;
	
	private int width;
	
	/**
	 * Creates a new column.
	 * 
	 * @param name the name of this column
	 * @param supplier function to read the value from each record
	 */
	public Column(String name, Function<T, V> supplier) {
		super();
		this.name = name;
		this.supplier = supplier;
		
		width = UNSPECIFIED_WIDTH;
	}
	
	/**
	 * Gets the custom formatter assigned to this column.
	 * 
	 * @return the custom formatter or {@code null} if none is assigned
	 */
	public Formatter<V> getCustomFormatter() {
		return customFormatter;
	}

	/**
	 * Sets a custom formatter used to display strings.  If none is set, the default formatting is used.
	 * 
	 * @param customFormatter the custom formatter
	 */
	public void setCustomFormatter(Formatter<V> customFormatter) {
		this.customFormatter = customFormatter;
	}

	/**
	 * The name of this column.  This may be displayed as the column header depending on the output format.
	 * 
	 * @return the name of this column
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Reads the value of this column from the given record (row).
	 * 
	 * @param row the record
	 * @return the value of this column
	 */
	public V getValue(T row) {
		return supplier.apply(row);
	}
	
	/**
	 * Returns the current width or {@value UNSPECIFIED_WIDTH} if not yet specified.
	 * 
	 * @return the current width
	 */
	int getWidth() {
		return width;
	}
	
	/**
	 * Updates the width by taking the larger of the current width or the given (requested) width.  A negative value
	 * will unset the width, allowing it to be recomputed.
	 * 
	 * @param width the requested width in characters
	 */
	void updateWidth(int width) {
		if (width < 0) {
			width = UNSPECIFIED_WIDTH;
		} else {
			this.width = Math.max(this.width, width);
		}
	}

}

/* Copyright 2009-2018 David Hadka
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
package org.moeaframework.analysis.collector;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringEscapeUtils;
import org.moeaframework.core.Settings;

/**
 * An accumulator stores collected data from a single run of an algorithm.
 */
public class Accumulator implements Serializable {

	private static final long serialVersionUID = -7483439787468468601L;
	
	/**
	 * The error message displayed when attempting to access and invalid key
	 * that does not exist in an accumulator.
	 */
	private static final String INVALID_KEY =
			"key not defined in accumulator: {0}";

	/**
	 * The internal storage of data.
	 */
	private final Map<String, List<Serializable>> data;

	/**
	 * Constructs an empty accumulator.
	 */
	public Accumulator() {
		data = new HashMap<String, List<Serializable>>();
	}

	/**
	 * Adds the data to the sequence of observations with the specified key.
	 * 
	 * @param key the key of this observation
	 * @param value the value of this observation
	 */
	public void add(String key, Serializable value) {
		List<Serializable> entries = data.get(key);
		
		if (entries == null) {
			entries = new ArrayList<Serializable>();
			data.put(key, entries);
		}

		entries.add(value);
	}

	/**
	 * Returns the set of keys stored in this accumulator.
	 * 
	 * @return the set of keys stored in this accumulator
	 */
	public Set<String> keySet() {
		return data.keySet();
	}

	/**
	 * Returns the value at the specified index for the specified key.
	 * 
	 * @param key the key
	 * @param index the index
	 * @return the value at the specified index for the specified key
	 * @throws IllegalArgumentException if the key was not contained in this
	 *         accumulator
	 * @throws IndexOutOfBoundsException if the index is out of range {@code
	 *         (index < 0 || index >= size(key))}
	 */
	public Serializable get(String key, int index) {
		List<Serializable> entries = data.get(key);
		
		if (entries == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					INVALID_KEY, key));
		} else {
			return entries.get(index);
		}
	}

	/**
	 * Returns the number of values stored for the specified key.
	 * 
	 * @param key the key
	 * @return the number of values stored for the specified key
	 * @throws IllegalArgumentException if the key was not contained in this
	 *         accumulator
	 */
	public int size(String key) {
		List<Serializable> entries = data.get(key);
		
		if (entries == null) {
			throw new IllegalArgumentException(MessageFormat.format(
					INVALID_KEY, key));
		} else {
			return entries.size();
		}
	}
	
	/**
	 * Saves the contests of this accumulator to a CSV file.
	 * 
	 * @param file the file to create
	 * @throws IOException if an I/O error occurred
	 */
	public void saveCSV(File file) throws IOException {
		FileWriter writer = null;
		
		try {
			writer = new FileWriter(file);
			writer.write(toCSV());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
	/**
	 * Returns the contents of this accumulator as a string in CSV format.
	 * 
	 * @return the contents of this accumulator as a string in CSV format
	 */
	public String toCSV() {
		StringBuilder sb = new StringBuilder();
		boolean firstValue = true;
		
		// determine the ordering of the fields
		Set<String> fields = new LinkedHashSet<String>();
		fields.add("NFE");
		
		if (data.containsKey("Elapsed Time")) {
			fields.add("Elapsed Time");
		}
		
		fields.addAll(keySet());
		
		// create the header
		for (String field : fields) {
			if (!firstValue) {
				sb.append(", ");
			}
			
			sb.append(StringEscapeUtils.escapeCsv(field));
			firstValue = false;
		}
		
		// create the data
		for (int i = 0; i < size("NFE"); i++) {
			sb.append(Settings.NEW_LINE);
			firstValue = true;
			
			for (String field : fields) {
				if (!firstValue) {
					sb.append(", ");
				}
				
				sb.append(StringEscapeUtils.escapeCsv(get(field, i).toString()));
				firstValue = false;
			}
		}
		
		return sb.toString();
	}

}

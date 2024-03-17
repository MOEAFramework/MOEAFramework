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

import java.util.Locale;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

/**
 * Formatter for displaying numeric data.  This supports all primitive numeric types as well as their boxed types.
 */
public class NumberFormatter implements Formatter<Number> {
	
	private static NumberFormatter DEFAULT;
	
	private int width = -1;
	
	private int precision = 6;
	
	private boolean leadingSpaceForSign = false;
	
	private boolean localeSpecificGroupings = false;
	
	private boolean scientificNotation = false;
	
	private Locale locale = Locale.getDefault();
	
	/**
	 * Returns the default number formatter.  Changes made to this formatter will be reflected everywhere
	 * the default formatter is used.
	 * 
	 * @return the default number formatter
	 */
	public static NumberFormatter getDefault() {
		return DEFAULT;
	}
	
	/**
	 * Sets the default number formatter.
	 * 
	 * @param formatter the formatter to use as the default; if {@code null}, resets to the default settings.
	 */
	public static void setDefault(NumberFormatter formatter) {
		DEFAULT = formatter != null ? formatter : new NumberFormatter();
	}
	
	/**
	 * Initializes the default number formatter.
	 */
	static {
		setDefault(null);
	}
	
	/**
	 * Creates a new formatter for numeric types with default settings.
	 */
	public NumberFormatter() {
		super();
	}
	
	@Override
	public Class<Number> getType() {
		return Number.class;
	}
	
	/**
	 * Returns the width of this field.
	 * 
	 * @return the width
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Sets the width of this field.  See {@link String#format(String, Object...)} for details.
	 * 
	 * @param width the width
	 */
	public void setWidth(int width) {
		this.width = width;
	}

	/**
	 * Returns the precision of this field.
	 * 
	 * @return the precision
	 */
	public int getPrecision() {
		return precision;
	}

	/**
	 * Sets the precision of this field.  See {@link String#format(String, Object...)} for details.
	 * 
	 * @param precision the precision
	 */
	public void setPrecision(int precision) {
		this.precision = precision;
	}

	/**
	 * Returns {@code true} if this format reserves a leading space for the sign.
	 * 
	 * @return {@code true} if this format reserves a leading space for the sign; {@code false} otherwise
	 */
	public boolean isLeadingSpaceForSign() {
		return leadingSpaceForSign;
	}

	/**
	 * Set to {@code true} to reserve a leading space for the sign; {@code false} otherwise.  This
	 * is equivalent to using the {@code "-"} format option.  See {@link String#format(String, Object...)}
	 * for details.
	 * 
	 * @param leadingSpaceForSign {@code true} to reserve a leading space for the sign; {@code false} otherwise
	 */
	public void setLeadingSpaceForSign(boolean leadingSpaceForSign) {
		this.leadingSpaceForSign = leadingSpaceForSign;
	}

	/**
	 * Returns {@code true} if this formatting uses locale-specific groupings.
	 * 
	 * @return {@code true} if this formatting uses locale-specific groupings; {@code false} otherwise
	 */
	public boolean isLocaleSpecificGroupings() {
		return localeSpecificGroupings;
	}

	/**
	 * Set to {@code true} to use locale-specific groupings.  For example, this may include adding a
	 * comma or other character to split thousands-places.
	 * 
	 * @param localeSpecificGroupings {@code true} if this formatting uses locale-specific groupings;
	 *        {@code false} otherwise
	 */
	public void setLocaleSpecificGroupings(boolean localeSpecificGroupings) {
		this.localeSpecificGroupings = localeSpecificGroupings;
	}

	/**
	 * Returns {@code true} if this formatting uses scientific notation.
	 * 
	 * @return {@code true} if this formatting uses scientific notation; {@code false} otherwise
	 */
	public boolean isScientificNotation() {
		return scientificNotation;
	}

	/**
	 * Set to {@code true} to use scientific notation.  When enabled, the number will include the exponent and is
	 * useful when expressing very small or large numbers.
	 * 
	 * @param scientificNotation {@code true} if this formatting uses scientific notation; {@code false} otherwise
	 */
	public void setScientificNotation(boolean scientificNotation) {
		this.scientificNotation = scientificNotation;
	}
	
	/**
	 * Returns the locale used when formatting the data.
	 * 
	 * @return the locale used when formatting the data
	 */
	public Locale getLocale() {
		return locale;
	}
	
	/**
	 * Sets the locale used when formatting the data.  If unset, the default system locale is used.
	 * 
	 * @param locale the new locale
	 */
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	/**
	 * Creates the formatting string used by {@link String#format(String, Object...)} to display decimal values.
	 * 
	 * @return the formatting string
	 */
	protected String createDecimalFormatString() {
		return "%" + (localeSpecificGroupings ? "," : "") +
				(leadingSpaceForSign ? " " : "") + 
				(width < 0 ? "" : ""+width) +
				(precision < 0 ? "" : "." + precision) + 
				(scientificNotation ? "e" : "f");
	}
	
	/**
	 * Creates the formatting string used by {@link String#format(String, Object...)} to display integer values.
	 * 
	 * @return the formatting string
	 */
	protected String createIntegerFormatString() {
		return "%" + (localeSpecificGroupings ? "," : "") +
				(leadingSpaceForSign ? " " : "") + 
				(width < 0 ? "" : ""+width) + "d";
	}

	@Override
	public String format(Object value) {
		if (value instanceof Float || value instanceof Double) {
			return String.format(locale, createDecimalFormatString(), value);
		} else {
			return String.format(locale, createIntegerFormatString(), value);
		}
	}
	
	/**
	 * Formats an array of double values into a string representation.
	 * 
	 * @param values the values
	 * @return the string representation
	 */
	public String format(double[] values) {
		return format(DoubleStream.of(values).boxed());
	}
	
	/**
	 * Formats an array of integer values into a string representation.
	 * 
	 * @param values the values
	 * @return the string representation
	 */
	public String format(int[] values) {
		return format(IntStream.of(values).boxed());
	}
	
}

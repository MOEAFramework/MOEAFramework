package org.moeaframework.util.format;

/**
 * Formatter for numeric data.  This supports all primitive numeric types as well
 * as their boxed types.
 */
public class NumberFormatter implements Formatter<Number> {
	
	private int width = -1;
	
	private int precision = 6;
	
	private boolean leadingSpaceForSign = false;
	
	private boolean localeSpecificGroupings = false;
	
	private boolean scientificNotation = false;
	
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
	 * Set to {@code true} to use scientific notation.  When enabled, the number will include the exponent
	 * and is useful when expressing very small or large numbers.
	 * 
	 * @param scientificNotation {@code true} if this formatting uses scientific notation; {@code false} otherwise
	 */
	public void setScientificNotation(boolean scientificNotation) {
		this.scientificNotation = scientificNotation;
	}

	/**
	 * Creates the formatting string used by {@link String#format(String, Object...)} to display
	 * decimal values.
	 * 
	 * @return the formatting string
	 */
	protected String createDecimalFormatString() {
		return "%" + (localeSpecificGroupings ? "," : "") + (leadingSpaceForSign ? " " : "") + 
				(width < 0 ? "" : ""+width) + (precision < 0 ? "" : "." + precision) + 
				(scientificNotation ? "e" : "f");
	}
	
	/**
	 * Creates the formatting string used by {@link String#format(String, Object...)} to display
	 * integer values.
	 * 
	 * @return the formatting string
	 */
	protected String createIntegerFormatString() {
		return "%" + (localeSpecificGroupings ? "," : "") + (leadingSpaceForSign ? " " : "") + 
				(width < 0 ? "" : ""+width) + "d";
	}

	@Override
	public String format(Object value) {
		if (value instanceof Float || value instanceof Double) {
			return String.format(createDecimalFormatString(), value);
		} else {
			return String.format(createIntegerFormatString(), value);
		}
	}

}

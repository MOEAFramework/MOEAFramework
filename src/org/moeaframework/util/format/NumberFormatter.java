package org.moeaframework.util.format;

public class NumberFormatter implements Formatter<Number> {
	
	private int width = -1;
	
	private int precision = 6;
	
	private boolean leadingSpaceForSign = false;
	
	private boolean localeSpecificGroupings = false;
	
	private boolean scientificNotation = false;
	
	public NumberFormatter() {
		super();
	}
	
	public Class<Number> getType() {
		return Number.class;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getPrecision() {
		return precision;
	}

	public void setPrecision(int precision) {
		this.precision = precision;
	}

	public boolean isLeadingSpaceForSign() {
		return leadingSpaceForSign;
	}

	public void setLeadingSpaceForSign(boolean leadingSpaceForSign) {
		this.leadingSpaceForSign = leadingSpaceForSign;
	}

	public boolean isLocaleSpecificGroupings() {
		return localeSpecificGroupings;
	}

	public void setLocaleSpecificGroupings(boolean localeSpecificGroupings) {
		this.localeSpecificGroupings = localeSpecificGroupings;
	}

	public boolean isScientificNotation() {
		return scientificNotation;
	}

	public void setScientificNotation(boolean scientificNotation) {
		this.scientificNotation = scientificNotation;
	}

	public String createDecimalFormatString() {
		return "%" + (localeSpecificGroupings ? "," : "") + (leadingSpaceForSign ? " " : "") + 
				(width < 0 ? "" : ""+width) + (precision < 0 ? "" : "." + precision) + 
				(scientificNotation ? "e" : "f");
	}
	
	public String createIntegerFormatString() {
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

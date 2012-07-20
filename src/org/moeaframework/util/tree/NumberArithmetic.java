package org.moeaframework.util.tree;

/**
 * Provides many arithmetic and trigonometric functions that operate on
 * {@link Number}s, performing any necessary implicit casting.  An integer
 * number remains an integer unless the specific function requires
 * floating-point values.
 */
public class NumberArithmetic {
	
	private NumberArithmetic() {
		super();
	}
	
	public static boolean equals(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() == b.doubleValue();
		} else {
			return a.longValue() == b.longValue();
		}
	}
	
	public static boolean lessThan(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() < b.doubleValue();
		} else {
			return a.longValue() < b.longValue();
		}
	}
	
	public static boolean lessThanOrEqual(Number a, Number b) {
		return lessThan(a, b) || equals(a, b);
	}
	
	public static boolean greaterThan(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() > b.doubleValue();
		} else {
			return a.longValue() > b.longValue();
		}
	}
	
	public static boolean greaterThanOrEqual(Number a, Number b) {
		return greaterThan(a, b) || equals(a, b);
	}
	
	public static Number add(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() + b.doubleValue();
		} else {
			return a.longValue() + b.longValue();
		}
	}
	
	public static Number sqrt(Number a) {
		return Math.sqrt(a.doubleValue());
	}
	
	public static Number pow(Number a, Number b) {
		return Math.pow(a.doubleValue(), b.doubleValue());
	}
	
	public static Number sub(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() - b.doubleValue();
		} else {
			return a.longValue() - b.longValue();
		}
	}
	
	public static Number mul(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() * b.doubleValue();
		} else {
			return a.longValue() * b.longValue();
		}
	}
	
	public static Number div(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() / b.doubleValue();
		} else {
			return a.longValue() / b.longValue();
		}
	}
	
	public static Number mod(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() % b.doubleValue();
		} else {
			return a.longValue() % b.longValue();
		}
	}
	
	public static Number floor(Number a) {
		if (isFloatingPoint(a)) {
			return Math.floor(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	public static Number ceil(Number a) {
		if (isFloatingPoint(a)) {
			return Math.ceil(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	public static Number round(Number a) {
		if (isFloatingPoint(a)) {
			return Math.round(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	public static Number abs(Number a) {
		if (isFloatingPoint(a)) {
			return Math.abs(a.doubleValue());
		} else {
			return Math.abs(a.longValue());
		}
	}
	
	public static Number log(Number a) {
		return Math.log(a.doubleValue());
	}
	
	public static Number log10(Number a) {
		return Math.log10(a.doubleValue());
	}
	
	public static Number exp(Number a) {
		return Math.exp(a.doubleValue());
	}
	
	public static Number sin(Number a) {
		return Math.sin(a.doubleValue());
	}
	
	public static Number cos(Number a) {
		return Math.cos(a.doubleValue());
	}
	
	public static Number tan(Number a) {
		return Math.tan(a.doubleValue());
	}
	
	public static Number asin(Number a) {
		return Math.asin(a.doubleValue());
	}
	
	public static Number acos(Number a) {
		return Math.acos(a.doubleValue());
	}
	
	public static Number atan(Number a) {
		return Math.atan(a.doubleValue());
	}
	
	public static Number sinh(Number a) {
		return Math.sinh(a.doubleValue());
	}
	
	public static Number cosh(Number a) {
		return Math.cosh(a.doubleValue());
	}
	
	public static Number tanh(Number a) {
		return Math.tanh(a.doubleValue());
	}
	
	public static Number max(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return Math.max(a.doubleValue(), b.doubleValue());
		} else {
			return Math.max(a.longValue(), b.longValue());
		}
	}
	
	public static Number min(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return Math.max(a.doubleValue(), b.doubleValue());
		} else {
			return Math.max(a.longValue(), b.longValue());
		}
	}
	
	public static Number sign(Number a) {
		if (isFloatingPoint(a)) {
			return Math.signum(a.doubleValue());
		} else {
			return Long.signum(a.longValue());
		}
	}
	
	public static boolean isFloatingPoint(Number a) {
		return (a instanceof Float) || (a instanceof Double);
	}

}

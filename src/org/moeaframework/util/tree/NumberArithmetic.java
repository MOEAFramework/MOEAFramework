/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.util.tree;

import org.apache.commons.math3.util.FastMath;
import org.moeaframework.core.Settings;

/**
 * Provides many arithmetic and trigonometric functions that operate on
 * {@link Number}s, performing any necessary implicit casting.  An integer
 * number remains an integer unless the specific function requires
 * floating-point values.  These methods favor {@link Long} and {@link Double}
 * representations for integer and floating-point values, respectively.
 * <p>
 * The arithmetic functions provided herein support optional function
 * protection, which is enabled by default.  Function protection prevents
 * values like {@code Inf} and {@code NaN} from appearing due to invalid
 * inputs.  For example, this protects against division-by-zero.  To disable
 * function protection, set the property
 * {@code org.moeaframework.util.tree.protected_functions = false} in the file
 * {@code global.properties}.  {@code Inf} and {@code NaN} values can still
 * occur with function protection enabled, so outputs should still be
 * validated.
 */
public class NumberArithmetic {
	
	/**
	 * Private constructor to prevent instantiation.
	 */
	private NumberArithmetic() {
		super();
	}
	
	/**
	 * Returns {@code true} if the two numbers are equal; {@code false}
	 * otherwise.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return {@code true} if the two numbers are equal; {@code false}
	 *         otherwise
	 */
	public static boolean equals(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() == b.doubleValue();
		} else {
			return a.longValue() == b.longValue();
		}
	}
	
	/**
	 * Returns {@code true} if the first number is less than the second;
	 * {@code false} otherwise.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return {@code true} if the first number is less than the second;
	 *         {@code false} otherwise
	 */
	public static boolean lessThan(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() < b.doubleValue();
		} else {
			return a.longValue() < b.longValue();
		}
	}
	
	/**
	 * Returns {@code true} if the first number is less than or equal to the
	 * second; {@code false} otherwise.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return {@code true} if the first number is less than or equal to the
	 *         second; {@code false} otherwise
	 */
	public static boolean lessThanOrEqual(Number a, Number b) {
		return lessThan(a, b) || equals(a, b);
	}
	
	/**
	 * Returns {@code true} if the first number is greater than the second;
	 * {@code false} otherwise.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return {@code true} if the first number is greater than the second;
	 *         {@code false} otherwise
	 */
	public static boolean greaterThan(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() > b.doubleValue();
		} else {
			return a.longValue() > b.longValue();
		}
	}
	
	/**
	 * Returns {@code true} if the first number is greater than or equal to the
	 * second; {@code false} otherwise.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return {@code true} if the first number is greater than or equal to the
	 *         second; {@code false} otherwise
	 */
	public static boolean greaterThanOrEqual(Number a, Number b) {
		return greaterThan(a, b) || equals(a, b);
	}
	
	/**
	 * Returns the value of adding the two numbers
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the value of adding the two numbers
	 */
	public static Number add(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() + b.doubleValue();
		} else {
			return a.longValue() + b.longValue();
		}
	}
	
	/**
	 * Returns the square root of the number.  If the number is less than zero
	 * and function protection is enabled, this functions the square root of
	 * the absolute value of the number.
	 * 
	 * @param a the number
	 * @return the square root of the number
	 * @see Math#sqrt(double)
	 */
	public static Number sqrt(Number a) {
		if ((a.doubleValue() < 0.0) && Settings.isProtectedFunctions()) {
			return Math.sqrt(Math.abs(a.doubleValue()));
		} else {
			return Math.sqrt(a.doubleValue());
		}
	}
	
	/**
	 * Returns the value of the first number to the power of the second.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the value of the first number to the power of the second
	 * @see Math#pow(double, double)
	 */
	public static Number pow(Number a, Number b) {
		return Math.pow(a.doubleValue(), b.doubleValue());
	}
	
	/**
	 * Returns the value of subtracting the first from the second number.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the value of subtracting the first from the second number
	 */
	public static Number sub(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() - b.doubleValue();
		} else {
			return a.longValue() - b.longValue();
		}
	}
	
	/**
	 * Returns the value of multiplying the two numbers.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the value of multiplying the two numbers
	 */
	public static Number mul(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return a.doubleValue() * b.doubleValue();
		} else {
			return a.longValue() * b.longValue();
		}
	}
	
	/**
	 * Returns the value of dividing the first number by the second.  If the
	 * second argument is {@code 0} and function protection is enabled, this
	 * function returns {@code 1} regardless of the first argument's value.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the value of dividing the first number by the second
	 */
	public static Number div(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			if ((Math.abs(b.doubleValue()) < Settings.EPS) &&
					Settings.isProtectedFunctions()) {
				return 1.0;
			} else {
				return a.doubleValue() / b.doubleValue();
			}
		} else {
			if ((b.longValue() == 0) && Settings.isProtectedFunctions()) {
				return 1L;
			} else {
				return a.longValue() / b.longValue();
			}
		}
	}
	
	/**
	 * Returns the remainder from dividing the first number by the second.  If
	 * the second argument is {@code 0} and function protection is enabled, 
	 * this function returns {@code 0} regardless of the first argument's
	 * value.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the remainder from dividing the first number by the second
	 */
	public static Number mod(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			if ((Math.abs(b.doubleValue()) < Settings.EPS) &&
					Settings.isProtectedFunctions()) {
				return 0.0;
			} else {
				return a.doubleValue() % b.doubleValue();
			}
		} else {
			if ((b.longValue() == 0) && Settings.isProtectedFunctions()) {
				return 0L;
			} else {
				return a.longValue() % b.longValue();
			}
		}
	}
	
	/**
	 * Returns the largest integer value less than or equal to the given
	 * number.
	 * 
	 * @param a the number
	 * @return the largest integer value less than or equal to the given
	 *         number
	 * @see Math#floor(double)
	 */
	public static Number floor(Number a) {
		if (isFloatingPoint(a)) {
			return Math.floor(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	/**
	 * Returns the smallest integer value greater than or equal to the given
	 * number.
	 * 
	 * @param a the number
	 * @return the smallest integer value greater than or equal to the given
	 *         number
	 * @see Math#ceil(double)
	 */
	public static Number ceil(Number a) {
		if (isFloatingPoint(a)) {
			return Math.ceil(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	/**
	 * Returns the value of the number rounded to the nearest integer.
	 * 
	 * @param a the number
	 * @return the value of the number rounded to the nearest integer
	 * @see Math#round(double)
	 */
	public static Number round(Number a) {
		if (isFloatingPoint(a)) {
			return Math.round(a.doubleValue());
		} else {
			return a.longValue();
		}
	}
	
	/**
	 * Returns the absolute value of the number.
	 * 
	 * @param a the number
	 * @return the absolute value of the number
	 * @see Math#abs(long)
	 * @see Math#abs(double)
	 */
	public static Number abs(Number a) {
		if (isFloatingPoint(a)) {
			return Math.abs(a.doubleValue());
		} else {
			return Math.abs(a.longValue());
		}
	}
	
	/**
	 * Returns the natural logarithm of the number.  If the numbers is
	 * negative and function protection is enabled, then this function returns
	 * the natural logarithm of the absolute value of the number.  If the
	 * number is near zero and function protection is enabled, this function
	 * returns {@code 0.0}.
	 * 
	 * @param a the number
	 * @return the natural logarithm of the number
	 * @see Math#log(double)
	 */
	public static Number log(Number a) {
		if ((a.doubleValue() < Settings.EPS) && 
				Settings.isProtectedFunctions()) {
			double value = Math.abs(a.doubleValue());
			
			if (value < Settings.EPS) {
				return 0.0;
			} else {
				return Math.log(value);
			}
		} else {
			return Math.log(a.doubleValue());
		}
	}
	
	/**
	 * Returns the base-10 logarithm of the number.  If the numbers is
	 * negative and function protection is enabled, then this function returns
	 * the base-10 logarithm of the absolute value of the number.  If the
	 * number is near zero and function protection is enabled, this function
	 * returns {@code 0.0}.
	 * 
	 * @param a the number
	 * @return the base-10 logarithm of the number
	 * @see Math#log10(double)
	 */
	public static Number log10(Number a) {
		if ((a.doubleValue() < Settings.EPS) && 
				Settings.isProtectedFunctions()) {
			double value = Math.abs(a.doubleValue());
			
			if (value < Settings.EPS) {
				return 0.0;
			} else {
				return Math.log10(value);
			}
		} else {
			return Math.log10(a.doubleValue());
		}
	}
	
	/**
	 * Returns the value from taking Euler's number <i>e</i> to the power of
	 * the given number.
	 * 
	 * @param a the number
	 * @return  the value from taking Euler's number <i>e</i> to the power of
	 *          the given number
	 * @see Math#exp(double)
	 */
	public static Number exp(Number a) {
		return Math.exp(a.doubleValue());
	}
	
	/**
	 * Returns the trigonometric sine of the number.
	 * 
	 * @param a the number
	 * @return the trigonometric sine of the number
	 * @see Math#sin(double)
	 */
	public static Number sin(Number a) {
		return Math.sin(a.doubleValue());
	}
	
	/**
	 * Returns the trigonometric cosine of the number.
	 * 
	 * @param a the number
	 * @return the trigonometric cosine of the number
	 * @see Math#cos(double)
	 */
	public static Number cos(Number a) {
		return Math.cos(a.doubleValue());
	}
	
	/**
	 * Returns the trigonometric tangent of the number.
	 * 
	 * @param a the number
	 * @return the trigonometric tangent of the number
	 * @see Math#tan(double)
	 */
	public static Number tan(Number a) {
		return Math.tan(a.doubleValue());
	}
	
	/**
	 * Returns the arc sine of the number.
	 * 
	 * @param a the number
	 * @return the arc sine of the number
	 * @see Math#asin(double)
	 */
	public static Number asin(Number a) {
		return Math.asin(a.doubleValue());
	}
	
	/**
	 * Returns the arc cosine of the number.
	 * 
	 * @param a the number
	 * @return the arc cosine of the number
	 * @see Math#acos(double)
	 */
	public static Number acos(Number a) {
		return Math.acos(a.doubleValue());
	}
	
	/**
	 * Returns the arc tangent of the number.
	 * 
	 * @param a the number
	 * @return the arc tangent of the number
	 * @see Math#atan(double)
	 */
	public static Number atan(Number a) {
		return Math.atan(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic sine of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic sine of the number
	 * @see Math#sinh(double)
	 */
	public static Number sinh(Number a) {
		return Math.sinh(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic cosine of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic cosine of the number
	 * @see Math#cosh(double)
	 */
	public static Number cosh(Number a) {
		return Math.cosh(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic tangent of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic tangent of the number
	 * @see Math#tanh(double)
	 */
	public static Number tanh(Number a) {
		return Math.tanh(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic arc sine of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic arc sine of the number
	 * @see FastMath#asinh(double)
	 */
	public static Number asinh(Number a) {
		return FastMath.asinh(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic arc cosine of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic arc cosine of the number
	 * @see FastMath#acosh(double)
	 */
	public static Number acosh(Number a) {
		return FastMath.acosh(a.doubleValue());
	}
	
	/**
	 * Returns the hyperbolic arc tangent of the number.
	 * 
	 * @param a the number
	 * @return the hyperbolic arc tangent of the number
	 * @see FastMath#atanh(double)
	 */
	public static Number atanh(Number a) {
		return FastMath.atanh(a.doubleValue());
	}
	
	/**
	 * Returns the maximum value of two numbers.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the maximum value of two numbers
	 * @see Math#min(long, long)
	 * @see Math#min(double, double)
	 */
	public static Number max(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return Math.max(a.doubleValue(), b.doubleValue());
		} else {
			return Math.max(a.longValue(), b.longValue());
		}
	}
	
	/**
	 * Returns the minimum value of two numbers.
	 * 
	 * @param a the first number
	 * @param b the second number
	 * @return the minimum value of two numbers
	 * @see Math#max(long, long)
	 * @see Math#max(double, double)
	 */
	public static Number min(Number a, Number b) {
		if (isFloatingPoint(a) || isFloatingPoint(b)) {
			return Math.min(a.doubleValue(), b.doubleValue());
		} else {
			return Math.min(a.longValue(), b.longValue());
		}
	}
	
	/**
	 * Returns the sign of the number.
	 * 
	 * @param a the number
	 * @return the sign of the number
	 * @see Long#signum(long)
	 * @see Math#signum(double)
	 */
	public static Number sign(Number a) {
		if (isFloatingPoint(a)) {
			return Math.signum(a.doubleValue());
		} else {
			return Long.signum(a.longValue());
		}
	}
	
	/**
	 * Returns {@code true} if the number is a floating-point value;
	 * {@code false} otherwise.
	 * 
	 * @param a the number
	 * @return {@code true} if the number is a floating-point value;
	 *         {@code false} otherwise
	 */
	public static boolean isFloatingPoint(Number a) {
		return (a instanceof Float) || (a instanceof Double);
	}

}

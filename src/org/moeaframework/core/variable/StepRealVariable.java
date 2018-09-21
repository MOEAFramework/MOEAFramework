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
package org.moeaframework.core.variable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;

import java.text.MessageFormat;

/**
 * Decision variable for doubleegers encoded as a binary string.  Note that if
 * {@code upperBound-lowerBound} is not a power of 2, then some values will
 * occur more frequently after a variation operator.
 */
public class StepRealVariable implements Variable {

    private static final String VALUE_OUT_OF_BOUNDS =
            "value out of bounds (value: {0}, min: {1}, max: {2})";
    private static final long serialVersionUID = 1529279478471159425L;
    /**
     * The current value of this decision variable.
     */
    private double value;
    /**
     * The lower bound of this decision variable.
     */
    private final double lowerBound;

    /**
     * The upper bound of this decision variable.
     */
    private final double upperBound;

//    private final BinaryIntegerVariable binInt;

    public double getStep() {
        return step;
    }

    /**
     * The step of this decision variable.
     */
    private final double step;

    /**
     * Constructs a real variable in the range {@code lowerBound <= x <=
     * upperBound} with an uninitialized value.
     *
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     */
    public StepRealVariable(double lowerBound, double upperBound, double step) {
        this(Double.NaN, lowerBound, upperBound, step);
    }

    /**
     * Constructs an doubleeger-valued variable in the range
     * {@code lowerBound <= x <= upperBound} with an uninitialized value.
     *
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     * @param step
     */
    public StepRealVariable(double value, double lowerBound, double upperBound, double step) {
        super();
        this.value = value;
//        this.binInt = new BinaryIntegerVariable(0, (int) ((upperBound - lowerBound) / step));
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.step = step;
    }


    /**
     * Returns the current value of this decision variable.
     *
     * @return the current value of this decision variable
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of this decision variable.
     *
     * @param value the new value for this decision variable
     * @throws IllegalArgumentException if the value is out of bounds
     *         {@code (value < getLowerBound()) || (value > getUpperBound())}
     */
    public void setValue(double value) {
        if ((value < lowerBound) || (value > upperBound)) {
            throw new IllegalArgumentException(MessageFormat.format(
                    VALUE_OUT_OF_BOUNDS, value, lowerBound, upperBound));
        }

        this.value = value;
    }


    /**
     * Returns the lower bound of this decision variable.
     *
     * @return the lower bound of this decision variable, inclusive
     */
    public double getLowerBound() {
        return lowerBound;
    }

    /**
     * Returns the upper bound of this decision variable.
     *
     * @return the upper bound of this decision variable, inclusive
     */
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public StepRealVariable copy() {


        return new StepRealVariable(getValue(),
                lowerBound, upperBound, step);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(lowerBound)
                .append(upperBound)
                .append(step)
                .append(getValue())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if ((obj == null) || (obj.getClass() != getClass())) {
            return false;
        } else {
            StepRealVariable rhs = (StepRealVariable) obj;

            return new EqualsBuilder()
                    .append(lowerBound, rhs.lowerBound)
                    .append(upperBound, rhs.upperBound)
                    .append(step, rhs.step)
                    .append(getValue(), rhs.getValue())
                    .isEquals();
        }
    }

    @Override
    public String toString() {
        return Double.toString(getValue());
    }

    @Override
    public void randomize() {
        setValue(lowerBound + PRNG.nextInt(0, (int) ((upperBound - lowerBound) / step))*step);
    }

}

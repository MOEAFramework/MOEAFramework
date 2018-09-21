/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */
package org.moeaframework.core.variable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.PRNG;

/**
 * Decision variable for integers encoded as a binary string.  Note that if
 * {@code upperBound-lowerBound} is not a power of 2, then some values will
 * occur more frequently after a variation operator.
 */
public class BinaryRealStepVariable extends BinaryVariable {


    private static final String VALUE_OUT_OF_BOUNDS =
            "value out of bounds (value: {0}, min: {1}, max: {2},step: {3})";
    private static final long serialVersionUID = 7266669064652277949L;

    /**
     * The lower bound of this decision variable.
     */
    private final double lowerBound;

    /**
     * The upper bound of this decision variable.
     */
    private final double upperBound;

    private final double step;

    private final double numSteps;

    /**
     * If {@code true}, the binary representation uses gray coding.  Gray
     * coding ensures that two successive values differ in only one bit.
     */
    private final boolean gray;

    /**
     * Constructs an integer-valued variable in the range
     * {@code lowerBound <= x <= upperBound} with an uninitialized value.
     * Uses gray coding by default.
     *
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     */
    public BinaryRealStepVariable(double lowerBound, double upperBound, double step) {
        this(lowerBound, upperBound, step, true);
    }

    /**
     * Constructs an integer-valued variable in the range
     * {@code lowerBound <= x <= upperBound} with the specified initial value.
     * Uses gray coding by default.
     *
     * @param value      the initial value of this decision variable
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     * @throws IllegalArgumentException if the value is out of bounds
     *                                  {@code (value < lowerBound) || (value > upperBound)}
     */
    public BinaryRealStepVariable(double value, double lowerBound, double upperBound, double step) {
        this(value, lowerBound, upperBound, step,true);
    }

    /**
     * Constructs an integer-valued variable in the range
     * {@code lowerBound <= x <= upperBound} with an uninitialized value.
     *
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     * @param gray       if the binary representation uses gray coding
     */
    public BinaryRealStepVariable(double lowerBound, double upperBound, double step, boolean gray) {
        super(getNumberOfBits(lowerBound, upperBound, step));
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.step = step;
        this.gray = gray;
        this.numSteps=(upperBound-lowerBound)/step;
    }

    /**
     * Constructs an integer-valued variable in the range
     * {@code lowerBound <= x <= upperBound} with the specified initial value.
     * Uses gray coding by default.
     *
     * @param value      the initial value of this decision variable
     * @param lowerBound the lower bound of this decision variable, inclusive
     * @param upperBound the upper bound of this decision variable, inclusive
     * @param gray       if the binary representation uses gray coding
     * @throws IllegalArgumentException if the value is out of bounds
     *                                  {@code (value < lowerBound) || (value > upperBound)}
     */
    public BinaryRealStepVariable(double value, double lowerBound, double upperBound, double step,
                                  boolean gray) {
        this(lowerBound, upperBound, step, gray);
        setValue(value);
    }

    /**
     * Returns the minimum number of bits required to represent an integer
     * within the given bounds.
     *
     * @param lowerBound the lower bound
     * @param upperBound the upper bound
     * @return the minimum number of bits required to represent an integer
     * within the given bounds
     */
    public static final int getNumberOfBits(double lowerBound, double upperBound, double step) {
        return Integer.SIZE - Integer.numberOfLeadingZeros((int) ((upperBound - lowerBound) / step));
    }

    /**
     * Returns the current value of this decision variable.
     *
     * @return the current value of this decision variable
     */
    public double getValue() {
        if (gray) {
            EncodingUtils.grayToBinary(this);
        }

        double value = EncodingUtils.decode(this);

        if (gray) {
            EncodingUtils.binaryToGray(this);
        }

        // if difference is not a power of 2, then the decoded value may be
        // larger than the difference
        if (value > numSteps) {
            value = value % numSteps;
        }

        return lowerBound + value*step;
    }

    /**
     * Sets the value of this decision variable.
     *
     * @param value the new value for this decision variable
     * @throws IllegalArgumentException if the value is out of bounds
     *                                  {@code (value < getLowerBound()) || (value > getUpperBound())}
     */
    public void setValue(double value) {
        double value1=value;
        while (value1 < lowerBound) value1+=(upperBound-lowerBound);
        value1=((value1-lowerBound)/step) % numSteps;
        EncodingUtils.encode((int)Math.round(value1), this);

        if (gray) {
            EncodingUtils.binaryToGray(this);
        }
    }

    /**
     * Returns {@code true} if the binary representation using gray coding.
     * Gray coding ensures that two successive values differ by only one bit.
     *
     * @return {@code true} if the binary representation using gray coding;
     * {@code false} otherwise
     */
    protected boolean isGray() {
        return gray;
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
    public BinaryRealStepVariable copy() {
        BinaryRealStepVariable result = new BinaryRealStepVariable(getValue(),
                lowerBound, upperBound,step);

        // ensure the copy has the same internal binary string
        for (int i = 0; i < result.getNumberOfBits(); i++) {
            result.set(i, get(i));
        }

        return result;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(lowerBound)
                .append(upperBound)
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
            BinaryRealStepVariable rhs = (BinaryRealStepVariable) obj;

            return new EqualsBuilder()
                    .append(lowerBound, rhs.lowerBound)
                    .append(upperBound, rhs.upperBound)
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
        setValue(lowerBound + PRNG.nextInt(0, (int) ((upperBound - lowerBound) / step)) * step);
    }

}

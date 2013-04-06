package org.moeaframework.core;

/**
 * A constraint, defining both the constraint type (i.e., equality vs
 * inequality) and the constraint value.  Whether or not this value violates
 * the constraint depends on the constraint type.  All constraints use a
 * threshold (cutoff) of {@code 0.0}.
 * <p>
 * <table width="100%" border="1" cellpadding="3" cellspacing="0">
 *   <tr class="TableHeadingColor">
 *     <th width="25%" align="left">Constraint Type</th>
 *     <th width="75%" align="left">Feasible When</th>
 *   </tr>
 *   <tr>
 *     <td>{@code EQUAL}</td>
 *     <td>{@code value == 0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code LESS_THAN_OR_EQUAL}</td>
 *     <td>{@code value <= 0}</td>
 *   </tr>
 *   <tr>
 *     <td>{@code GREATER_THAN_OR_EQUAL}</td>
 *     <td>{@code value >= 0}</td>
 *   </tr>
 * </table>
 * 
 * The methods {@link #isConstraintViolated()} and {@link #getAbsoluteValue()}
 * should be used to check if the constraint is violated and its magnitude,
 * respectively.
 */
public class Constraint {
	
	/**
	 * The type of constraint (i.e., equality vs inequality).
	 */
	private final ConstraintType type;
	
	/**
	 * The value of this constraint.
	 */
	private double value;
	
	/**
	 * Internal variable that stores the absolute value of the constraint
	 * violation.  To improve performance, this value is computed in
	 * {@link #setValue(double)}.  
	 */
	private double absoluteValue;
	
	/**
	 * Constructs a new constraint.
	 * 
	 * @param type the type of constraint (i.e., equality vs inequality)
	 */
	public Constraint(ConstraintType type) {
		super();
		this.type = type;
		this.value = Double.NaN;
		this.absoluteValue = Double.NaN;
	}
	
	/**
	 * Copy constructor.
	 * 
	 * @param constraint the constraint to copy
	 */
	protected Constraint(Constraint constraint) {
		this(constraint.getType());
		setValue(constraint.getValue());
	}

	/**
	 * Returns the value of this constraint.  Whether or not this value
	 * violates the constraint depends on the constraint type.  Use
	 * {@link #isConstraintViolated()} to test for constraint violations.
	 * 
	 * @return the value of this constraint
	 */
	public double getValue() {
		return value;
	}

	/**
	 * Sets the value of this constraint.  Whether or not this value violates
	 * the constraint depends on the constraint type.
	 * 
	 * @param value the value of this constraint
	 */
	public void setValue(double value) {
		this.value = value;
		
		switch (type) {
		case EQUAL:
			absoluteValue = Math.abs(value);
			break;
		case LESS_THAN_OR_EQUAL:
			absoluteValue = value <= 0.0 ? 0.0 : value;
			break;
		case GREATER_THAN_OR_EQUAL:
			absoluteValue = value >= 0.0 ? 0.0 : -value;
			break;
		default:
			throw new IllegalStateException();
		}
	}

	/**
	 * Returns the type of constraint (i.e., equality vs inequality).
	 * 
	 * @return the type of constraint
	 */
	public ConstraintType getType() {
		return type;
	}
	
	/**
	 * Returns {@code true} if this constraint is violated; {@code false}
	 * otherwise.
	 * 
	 * @return {@code true} if this constraint is violated; {@code false}
	 *         otherwise
	 */
	public boolean isConstraintViolated() {
		return absoluteValue != 0.0;
	}

	/**
	 * Returns the absolute constraint violation, defined as {@code 0.0} when
	 * this constraint is satisfied and {@code abs(getValue())} when this
	 * constraint is violated.
	 * 
	 * @return the absolute constraint violation
	 */
	public double getAbsoluteValue() {
		return absoluteValue;
	}
	
	/**
	 * Creates and returns a copy of this constraint.  The copy is completely
	 * independent from the original.
	 * 
	 * @return a copy of this constraint
	 */
	public Constraint copy() {
		return new Constraint(this);
	}

}

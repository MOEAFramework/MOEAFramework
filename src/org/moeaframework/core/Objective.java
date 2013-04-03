package org.moeaframework.core;

/**
 * An objective, defining both the objective value and the optimization
 * direction.
 * <p>
 * This class distinguishes between the actual objective value and the
 * <em>canonical objective value</em>.  Since the MOEA Framework supports both
 * minimized and maximized objectives, it would be necessary to check the
 * direction every time two objectives are compared.  This is problematic for
 * two reasons.  First, this would litter the code with many if/else cases to
 * handle both minimized and maximized objectives.  Second, since millions or
 * even billions of such comparisons can occur throughout a single run, this
 * would negatively impact performance.  To avoid this performance bottleneck,
 * the MOEA Framework internally stores the objective values in their
 * <em>canonical</em> form.  It's fairly straightforward: the value of
 * maximized objectives are negated.  Minimized objectives remain unchanged.
 * As a result, all objectives, regardless of their direction, are treated
 * internally as being minimized.
 * <p>
 * Just remember: when comparing objective values, use
 * {@link #getCanonicalValue()}; when displaying objective values to the user,
 * use {@link #getValue()}.
 */
public class Objective {
	
	/**
	 * The direction of optimization (i.e., minimized or maximized).
	 */
	private final Direction direction;
	
	/**
	 * The objective value.
	 */
	private double value;
	
	/**
	 * Constructs a new, minimized objective.
	 */
	public Objective() {
		this(Direction.MINIMIZE);
	}
	
	/**
	 * Constructs a new objective.
	 * 
	 * @param direction the direction of optimization (i.e., minimized or
	 *        maximized)
	 */
	public Objective(Direction direction) {
		super();
		this.direction = direction;
		this.value = Double.NaN;
	}

	/**
	 * Returns the objective value.
	 * 
	 * @return the objective value
	 */
	public double getValue() {
		if (direction == Direction.MINIMIZE) {
			return value;
		} else {
			return -value;
		}
	}

	/**
	 * Sets the objective value.
	 * 
	 * @param value the objective value
	 */
	public void setValue(double value) {
		if (direction == Direction.MINIMIZE) {
			this.value = value;
		} else {
			this.value = -value;
		}
	}
	
	/**
	 * Returns the canonical value of this objective.
	 * 
	 * @return the canonical value of this objective
	 */
	public double getCanonicalValue() {
		return value;
	}
	
	/**
	 * Sets the canonical value of this objective.
	 * 
	 * @param value the canonical value of this objective
	 */
	public void setCanonicalValue(double value) {
		this.value = value;
	}

	/**
	 * Returns the direction of optimization (i.e., minimized or maximized).
	 * 
	 * @return the direction of optimization
	 */
	public Direction getDirection() {
		return direction;
	}
	
	/**
	 * Creates and returns a copy of this objective.  The copy is completely
	 * independent from the original.
	 * 
	 * @return a copy of this objective
	 */
	public Objective copy() {
		Objective copy = new Objective(direction);
		copy.setCanonicalValue(value);
		return copy;
	}

}

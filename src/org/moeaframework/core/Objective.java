package org.moeaframework.core;

/**
 * An objective, defining both the objective value and the optimization
 * direction.  
 */
public class Objective {
	
	/**
	 * The direction of optimization (i.e., minimized or maximized).
	 */
	private Direction direction;
	
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
		return value;
	}

	/**
	 * Sets the objective value.
	 * 
	 * @param value the objective value
	 */
	public void setValue(double value) {
		this.value = value;
	}

	/**
	 * Sets the direction of optimization (i.e., minimized or maximized).
	 * 
	 * @param direction the direction of optimization
	 */
	public void setDirection(Direction direction) {
		this.direction = direction;
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
		copy.setValue(value);
		return copy;
	}

}

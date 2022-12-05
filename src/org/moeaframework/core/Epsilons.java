package org.moeaframework.core;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Stores the &epsilon; values for an &epsilon;-dominance archive.  In particular, if the
 * given &epsilon; value or array of values does not match the number of objectives, the
 * last &epsilon; value is repeated for each remaining objective.
 */
public class Epsilons {
	
	/**
	 * The epsilon values.
	 */
	private final double[] epsilons;
	
	/**
	 * Defines a single &epsilon; value repeated for each objective.
	 * 
	 * @param epsilon the &epsilon; value
	 */
	public Epsilons(double epsilon) {
		this(new double[] { epsilon });
	}
	
	/**
	 * Defines an array of &epsilon; values.
	 * 
	 * @param epsilons the array of epsilon values
	 */
	public Epsilons(double[] epsilons) {
		super();
		this.epsilons = epsilons.clone();
	}
	
	/**
	 * Returns the array of &epsilon; values.
	 * 
	 * @return the array of &epsilon; values
	 */
	public double[] toArray() {
		return epsilons;
	}
	
	/**
	 * Returns the &epsilon; value for the specified objective.
	 * 
	 * @param objective the index of the objective
	 * @return the &epsilon; value
	 */
	public double get(int objective) {
		return epsilons[objective < epsilons.length ? objective : epsilons.length - 1];
	}
	
	/**
	 * Returns the number of defined &epsilon; values.
	 * 
	 * @return the number of defined &epsilon; values
	 */
	public int size() {
		return epsilons.length;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(epsilons)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Epsilons rhs = (Epsilons)obj;
			
			return new EqualsBuilder()
					.append(epsilons, rhs.epsilons)
					.isEquals();
		}
	}
	
	@Override
	public String toString() {
		return Arrays.toString(epsilons);
	}

}

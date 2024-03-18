/* Copyright 2009-2024 David Hadka
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
package org.moeaframework.core;

import java.util.Arrays;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Stores the &epsilon; values for an &epsilon;-dominance archive.  In particular, if the given &epsilon; value or
 * array of values does not match the number of objectives, the last &epsilon; value is repeated for each remaining
 * objective.
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
	 * @throws IllegalArgumentException if the array is empty or any value is {@code <= 0.0}
	 */
	public Epsilons(double[] epsilons) {
		super();
		this.epsilons = epsilons.clone();
		
		if (this.epsilons.length < 1) {
			throw new IllegalArgumentException("at least one epsilon must be provided");
		}
		
		for (double eps : epsilons) {
			if (eps <= 0.0) {
				throw new IllegalArgumentException("epsilons must be > 0.0");
			}
		}
	}
	
	/**
	 * Returns the array of &epsilon; values.
	 * 
	 * @return the array of &epsilon; values
	 */
	public double[] toArray() {
		return epsilons.clone();
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
	
	/**
	 * Creates an {@code Epsilons} instance with the given epsilon values.  This method provides compile-time checking
	 * of the number of inputs.
	 * 
	 * @param e1 the first epsilon
	 * @param es the remaining epsilons, if any
	 * @return the epsilons object
	 */
	public static Epsilons of(double e1, double... es) {
		double[] epsilons = new double[es.length + 1];
		
		epsilons[0] = e1;
		
		for (int i = 0; i < es.length; i++) {
			epsilons[i+1] = es[i];
		}
		
		return new Epsilons(epsilons);
	}

}

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
package org.moeaframework.core.objective;

import java.io.Serializable;

import org.moeaframework.core.Constructable;
import org.moeaframework.core.Copyable;

/**
 * Defines an objective for optimization, including the value and the direction.
 * <p>
 * Each objective also defines a "canonical" value, which converts the objective value into a minimized form with the
 * ideal or target value of {@value Double#NEGATIVE_INFINITY}.  This is useful as many published codes are developed
 * for a single optimization direction, typically minimization, and can use the canonical value for consistency.
 * For example, the canonical value for a maximized objective could be the negated value, as minimizing the negated
 * value is equivalent to maximizing the original value.
 */
public interface Objective extends Comparable<Objective>, Copyable<Objective>, Serializable, Constructable {
	
	// TODO: Add tests
	
	/**
	 * Returns the objective value.
	 * 
	 * @return the objective value
	 */
	public double getValue();
	
	/**
	 * Sets the objective value.
	 * 
	 * @param value the objective value
	 */
	public void setValue(double value);
	
	/**
	 * Returns the canonical value of this objective.
	 * 
	 * @return the canonical value
	 */
	public double getCanonicalValue();
	
	/**
	 * Similar to {@link Comparable#compareTo(Object)}, compares this objective to a given value.
	 * 
	 * @param value the value
	 * @return {@code -1}, {@code 0}, or {@code 1} depending if this objective is less than, equal to, or greater than
	 *         the given value
	 */
	public int compareTo(double value);
	
	/**
	 * Returns a normalized objective that is:
	 * <ol>
	 *   <li>scaled between the minimum and maximum bounds, typically producing a value falling between
	 *       {@code [0, 1]}, and
	 *   <li>has an ideal or target value of {@code Double#NEGATIVE_INFINITY}.
	 * </ol>
	 * 
	 * @param minimum the minimum bound
	 * @param maximum the maximum bound
	 * @return the normalized objective
	 */
	public NormalizedObjective normalize(double minimum, double maximum);
	
	/**
	 * Returns the index used by epsilon-dominance.  This is used by
	 * {@link org.moeaframework.core.population.EpsilonBoxDominanceArchive} in its dominance calculations.
	 * 
	 * @param epsilon the epsilon value
	 * @return the index
	 */
	public int getEpsilonIndex(double epsilon);
	
	/**
	 * The distance this objective must change, in the direction of the ideal value, to fall within the next epsilon
	 * box.  This is used by {@link org.moeaframework.core.population.EpsilonBoxDominanceArchive} when comparing solutions within
	 * the same epsilon box.
	 * 
	 * @param epsilon the epsilon value
	 * @return the distance
	 */
	public double getEpsilonDistance(double epsilon);
	
	public double applyWeight(double weight);
	
	public double getIdealValue();
	
	@Override
	public default int compareTo(Objective other) {
		if (getClass() != other.getClass()) {
			throw new IllegalArgumentException("unable to compare objective values between " +
					getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
		}
		
		return compareTo(other.getValue());
	}

	public default double distanceTo(Objective other) {
		return Math.abs(other.getValue() - getValue());
	}
	
	public default double distanceTo(double value) {
		return Math.abs(value - getValue());
	}
	
	public static Objective ideal(Objective first, Objective second) {
		int flag = first.compareTo(second);
		return flag <= 0 ? first : second;
	}
	
	public static double ideal(double first, Objective second) {
		int flag = second.compareTo(first);
		return flag < 0 ? second.getValue() : first;
	}
	
	public static double ideal(Objective first, double second) {
		int flag = first.compareTo(second);
		return flag <= 0 ? first.getValue() : second;
	}
	
	/**
	 * Returns a new instance of the default optimization objective.
	 * 
	 * @return the default optimization objective
	 */
	public static Objective createDefault() {
		return new Minimize();
	}
	
}

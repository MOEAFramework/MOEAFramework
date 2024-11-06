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
import org.moeaframework.core.Named;

/**
 * Defines an objective for optimization, including the value and the direction.
 * <p>
 * Always prefer using one of the methods, such as {@link #compareTo(Objective)}, for performing operations on
 * objectives rather than using the value directly, as the methods accounts for the direction.
 */
public interface Objective extends Comparable<Objective>, Copyable<Objective>, Serializable, Constructable, Named {

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
	 * <p>
	 * The canonical value is the objective converted into a minimized form, with the target or ideal towards
	 * {@value Double#NEGATIVE_INFINITY}.  This allows an implementation to use the numeric value of the objective
	 * directly, without needing to be aware of the optimization direction.  This works since minimizing {@code -f(x)}
	 * is equivalent to maximizing {@code f(x)}.
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
	 * <p>
	 * This calculation is based on the canonical value of the objective, and as such the returned value is
	 * <strong>minimized</strong>.
	 * 
	 * @param epsilon the epsilon value
	 * @return the index
	 */
	public default int getEpsilonIndex(double epsilon) {
		return (int)Math.floor(getCanonicalValue() / epsilon);
	}
	
	/**
	 * The distance this objective must change, in the direction of the ideal value, to fall within the next epsilon
	 * box.  This is used by {@link org.moeaframework.core.population.EpsilonBoxDominanceArchive} when comparing
	 * solutions within the same epsilon box.
	 * 
	 * @param epsilon the epsilon value
	 * @return the distance
	 */
	public default double getEpsilonDistance(double epsilon) {
		return Math.abs(getCanonicalValue() - getEpsilonIndex(epsilon) * epsilon);
	}
	
	/**
	 * Applies a weight to this objective.
	 * <p>
	 * This calculation is based on the canonical value of the objective, and as such the returned value is
	 * <strong>minimized</strong>.
	 * 
	 * @param weight the weight
	 * @return the weighted objective value
	 */
	public default double applyWeight(double weight) {
		return weight * getCanonicalValue();
	}
	
	/**
	 * Returns the ideal or best possible value for this objective.
	 * 
	 * @return the ideal objective value
	 */
	public double getIdealValue();
	
	@Override
	public default int compareTo(Objective other) {
		if (getClass() != other.getClass()) {
			throw new IllegalArgumentException("unable to compare objective values between " +
					getClass().getSimpleName() + " and " + other.getClass().getSimpleName());
		}
		
		return compareTo(other.getValue());
	}

	/**
	 * Computes the distance between two objectives.
	 * 
	 * @param other the other objective
	 * @return the distance, a non-negative number
	 */
	public default double distanceTo(Objective other) {
		return Math.abs(other.getValue() - getValue());
	}
	
	/**
	 * Computes the distance between two objectives.
	 * 
	 * @param value the other objective value
	 * @return the distance, a non-negative number
	 */
	public default double distanceTo(double value) {
		return Math.abs(value - getValue());
	}
	
	/**
	 * Returns the objective considered better or more ideal.  If the objectives are equivalent, either objective will
	 * be returned.
	 * 
	 * @param first the first objective
	 * @param second the second objective
	 * @return the more ideal objective
	 * @see #compareTo(Objective)
	 */
	public static Objective ideal(Objective first, Objective second) {
		int flag = first.compareTo(second);
		return flag <= 0 ? first : second;
	}
	
	/**
	 * Returns the objective value considered better or more ideal.
	 * 
	 * @param first the first objective value
	 * @param second the second objective
	 * @return the ideal objective value
	 * @see #compareTo(double)
	 */
	public static double ideal(double first, Objective second) {
		int flag = second.compareTo(first);
		return flag < 0 ? second.getValue() : first;
	}
	
	/**
	 * Returns the objective value considered better or more ideal.
	 * 
	 * @param first the first objective value
	 * @param second the second objective
	 * @return the ideal objective value
	 * @see #compareTo(double)
	 */
	public static double ideal(Objective first, double second) {
		int flag = first.compareTo(second);
		return flag <= 0 ? first.getValue() : second;
	}
	
	/**
	 * Returns a new instance of the default optimization objective.  This is the objective used if not explicitly
	 * configured by the problem or user.
	 * 
	 * @return the default optimization objective
	 */
	public static Objective createDefault() {
		return new Minimize();
	}
	
	/**
	 * Returns the name of the objective, using either the name assigned to the objective or deriving the name from its
	 * index.
	 * 
	 * @param objective the objective
	 * @param index the index of the objective
	 * @return the name of the objective
	 */
	public static String getNameOrDefault(Objective objective, int index) {
		return objective.getName() == null ? "Obj" + (index + 1) : objective.getName();
	}
	
}

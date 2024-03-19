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
package org.moeaframework.util.weights;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.math3.util.CombinatoricsUtils;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.ConfigurationException;
import org.moeaframework.core.configuration.Validate;
import org.moeaframework.util.TypedProperties;

/**
 * Stores the number of reference point divisions and calculations based on those values.
 */
public class NormalBoundaryDivisions {

	private final int outerDivisions;
	
	private final int innerDivisions;
	
	/**
	 * Use a single layer approach with a single division argument.
	 * 
	 * @param divisions the number of divisions
	 */
	public NormalBoundaryDivisions(int divisions) {
		this(divisions, 0);
	}
	
	/**
	 * Use the two-layer approach with inner and outer divisions.
	 * 
	 * @param outerDivisions the number of outer divisions
	 * @param innerDivisions the number of inner divisions
	 * @throws IllegalArgumentException if {@code outerDivisions} or {@code innerDivisions} are {@code < 0}
	 */
	public NormalBoundaryDivisions(int outerDivisions, int innerDivisions) {
		super();
		this.outerDivisions = outerDivisions;
		this.innerDivisions = innerDivisions;
		
		Validate.greaterThanOrEqual("outerDivisions", 0, outerDivisions);
		Validate.greaterThanOrEqual("innerDivisions", 0, innerDivisions);
	}
	
	/**
	 * The number of outer divisions.
	 * 
	 * @return the number of outer divisions
	 */
	public int getOuterDivisions() {
		return outerDivisions;
	}
	
	/**
	 * Returns the number of inner divisions.  If {@code 0}, then only {@code divisionsOuter} is used.
	 * If non-zero, then the two-layer generation approach is taken.
	 * 
	 * @return the number of inner divisions
	 */
	public int getInnerDivisions() {
		return innerDivisions;
	}
	
	/**
	 * Determines the number of reference points that would be produced using the given number of objectives
	 * and divisions.
	 * 
	 * @param problem the problem
	 * @return the number of reference points
	 */
	public int getNumberOfReferencePoints(Problem problem) {
		return getNumberOfReferencePoints(problem.getNumberOfObjectives());
	}
	
	/**
	 * Determines the number of reference points that would be produced using the given number of objectives
	 * and divisions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @return the number of reference points
	 */
	public int getNumberOfReferencePoints(int numberOfObjectives) {
		return (int)(CombinatoricsUtils.binomialCoefficient(numberOfObjectives + outerDivisions - 1, outerDivisions) +
				(innerDivisions == 0 ? 0 : CombinatoricsUtils.binomialCoefficient(numberOfObjectives + innerDivisions - 1, innerDivisions)));
	}
	
	/**
	 * Returns {@code true} if this represents the two-layer approach where inner and outer divisions are specified.
	 * 
	 * @return {@code true} if the two-layer approach is used; {@code false} otherwise
	 */
	public boolean isTwoLayer() {
		return innerDivisions > 0;
	}
	
	/**
	 * Returns the properties used to configure an identical number of divisions.
	 * 
	 * @return the properties
	 */
	public TypedProperties toProperties() {
		TypedProperties properties = new TypedProperties();
		
		if (isTwoLayer()) {
			properties.setInt("divisionsInner", innerDivisions);
			properties.setInt("divisionsOuter", outerDivisions);
		} else {
			properties.setInt("divisions", outerDivisions);
		}
		
		return properties;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(innerDivisions)
				.append(outerDivisions)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			NormalBoundaryDivisions rhs = (NormalBoundaryDivisions)obj;
			
			return new EqualsBuilder()
					.append(innerDivisions, rhs.innerDivisions)
					.append(outerDivisions, rhs.outerDivisions)
					.isEquals();
		}
	}
	
	/**
	 * Reads the divisions properties, if set, or provides default values for the problem.
	 * 
	 * @param properties the properties
	 * @param problem the problem
	 * @return the divisions
	 */
	public static NormalBoundaryDivisions fromProperties(TypedProperties properties, Problem problem) {
		NormalBoundaryDivisions divisions = tryFromProperties(properties);
		
		if (divisions != null) {
			return divisions;
		}
		
		return forProblem(problem);
	}
	
	/**
	 * Reads the division properties, if set.  Otherwise, returns {@code null}.
	 * 
	 * @param properties the properties
	 * @return the divisions or {@code null}
	 */
	public static NormalBoundaryDivisions tryFromProperties(TypedProperties properties) {
		if (properties.contains("divisionsOuter") && properties.contains("divisionsInner")) {
			return new NormalBoundaryDivisions(
					properties.getTruncatedInt("divisionsOuter"),
					properties.getTruncatedInt("divisionsInner"));
		} 
		
		if (properties.contains("divisionsOuter") || properties.contains("divisionsInner")) {
			throw new ConfigurationException("must include both divisionsOuter and divisionsInner");
		}
		
		if (properties.contains("divisions")) {
			return new NormalBoundaryDivisions(properties.getTruncatedInt("divisions"));
		}
		
		return null;
	}
	
	/**
	 * Returns the default number of divisions based on the number of objectives.
	 * 
	 * @param problem the problem
	 * @return the reference point divisions
	 */
	public static NormalBoundaryDivisions forProblem(Problem problem) {
		return switch (problem.getNumberOfObjectives()) {
			case 1 -> new NormalBoundaryDivisions(100, 0);
			case 2 -> new NormalBoundaryDivisions(99, 0);
			case 3 -> new NormalBoundaryDivisions(12, 0);
			case 4 -> new NormalBoundaryDivisions(8, 0);
			case 5 -> new NormalBoundaryDivisions(6, 0);
			case 6 -> new NormalBoundaryDivisions(4, 1);
			case 7, 8, 9, 10 -> new NormalBoundaryDivisions(3, 2);
			default -> new NormalBoundaryDivisions(2, 1);
		};
	}

}

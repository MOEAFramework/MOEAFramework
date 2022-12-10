/* Copyright 2009-2022 David Hadka
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

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
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
	 */
	public NormalBoundaryDivisions(int outerDivisions, int innerDivisions) {
		super();
		this.outerDivisions = outerDivisions;
		this.innerDivisions = innerDivisions;
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
	 * Determines the number of reference points that would be produced using the given
	 * number of objectives and divisions.
	 * 
	 * @param problem the problem
	 * @return the number of reference points
	 */
	public int getNumberOfReferencePoints(Problem problem) {
		return getNumberOfReferencePoints(problem.getNumberOfObjectives());
	}
	
	/**
	 * Determines the number of reference points that would be produced using the given
	 * number of objectives and divisions.
	 * 
	 * @param numberOfObjectives the number of objectives
	 * @return the number of reference points
	 */
	public int getNumberOfReferencePoints(int numberOfObjectives) {
		return (int)(CombinatoricsUtils.binomialCoefficient(numberOfObjectives + outerDivisions - 1, outerDivisions) +
				(innerDivisions == 0 ? 0 : CombinatoricsUtils.binomialCoefficient(numberOfObjectives + innerDivisions - 1, innerDivisions)));
	}
	
	/**
	 * Returns {@code true} if this represents the two-layer approach where inner and outer
	 * divisions are specified.
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
					properties.getInt("divisionsOuter"),
					properties.getInt("divisionsInner"));
		} 
		
		if (properties.contains("divisionsOuter") || properties.contains("divisionsInner")) {
			throw new FrameworkException("must include both divisionsOuter and divisionsInner");
		}
		
		if (properties.contains("divisions")) {
			return new NormalBoundaryDivisions(properties.getInt("divisions"));
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
		int divisionsOuter;
		int divisionsInner;
		
		if (problem.getNumberOfObjectives() == 1) {
			divisionsOuter = 100;
			divisionsInner = 0;
		} else if (problem.getNumberOfObjectives() == 2) {
			divisionsOuter = 99;
			divisionsInner = 0;
		} else if (problem.getNumberOfObjectives() == 3) {
			divisionsOuter = 12;
			divisionsInner = 0;
		} else if (problem.getNumberOfObjectives() == 4) {
			divisionsOuter = 8;
			divisionsInner = 0;
		} else if (problem.getNumberOfObjectives() == 5) {
			divisionsOuter = 6;
			divisionsInner = 0;
		} else if (problem.getNumberOfObjectives() == 6) {
			divisionsOuter = 4;
			divisionsInner = 1;
		} else if (problem.getNumberOfObjectives() == 7) {
			divisionsOuter = 3;
			divisionsInner = 2;
		} else if (problem.getNumberOfObjectives() == 8) {
			divisionsOuter = 3;
			divisionsInner = 2;
		} else if (problem.getNumberOfObjectives() == 9) {
			divisionsOuter = 3;
			divisionsInner = 2;
		} else if (problem.getNumberOfObjectives() == 10) {
			divisionsOuter = 3;
			divisionsInner = 2;
		} else {
			divisionsOuter = 2;
			divisionsInner = 1;
		}
		
		return new NormalBoundaryDivisions(divisionsOuter, divisionsInner);
	}

}

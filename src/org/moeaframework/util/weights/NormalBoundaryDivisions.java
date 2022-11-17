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
	
	public NormalBoundaryDivisions(int divisions) {
		this(divisions, 0);
	}
	
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
	 * Reads the divisions properties, if set, or provides default values for the problem.
	 * 
	 * @param properties the properties
	 * @param problem the problem
	 * @return the reference point divisions
	 */
	public static NormalBoundaryDivisions fromProperties(TypedProperties properties, Problem problem) {
		if (properties.contains("divisionsOuter") && properties.contains("divisionsInner")) {
			return new NormalBoundaryDivisions(
					(int)properties.getDouble("divisionsOuter", 4),
					(int)properties.getDouble("divisionsInner", 0));
		} 
		
		if (properties.contains("divisionsOuter") || properties.contains("divisionsInner")) {
			throw new FrameworkException("must include both divisionsOuter and divisionsInner");
		}
		
		if (properties.contains("divisions")) {
			return new NormalBoundaryDivisions((int)properties.getDouble("divisions", 4));
		}
		
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

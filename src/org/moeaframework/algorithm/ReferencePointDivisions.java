package org.moeaframework.algorithm;

import org.apache.commons.math3.util.CombinatoricsUtils;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Problem;
import org.moeaframework.util.TypedProperties;

/**
 * Stores the number of reference point divisions and calculations based on those values.
 */
public class ReferencePointDivisions {

	private final int divisionsOuter;
	
	private final int divisionsInner;
	
	private ReferencePointDivisions(int divisions) {
		this(divisions, 0);
	}
	
	private ReferencePointDivisions(int divisionsOuter, int divisionsInner) {
		super();
		this.divisionsOuter = divisionsOuter;
		this.divisionsInner = divisionsInner;
	}
	
	/**
	 * The number of outer divisions.
	 * 
	 * @return the number of outer divisions
	 */
	public int getDivisionsOuter() {
		return divisionsOuter;
	}
	
	/**
	 * Returns the number of inner divisions.  If {@code 0}, then only {@code divisionsOuter} is used.
	 * If non-zero, then the two-layer generation approach is taken.
	 * 
	 * @return the number of inner divisions
	 */
	public int getDivisionsInner() {
		return divisionsInner;
	}
	
	/**
	 * Determines the number of reference points that would be produced using the given
	 * number of objectives and divisions.
	 * 
	 * @param problem the problem
	 * @return the number of reference points
	 */
	public int getNumberOfReferencePoints(Problem problem) {
		return (int)(CombinatoricsUtils.binomialCoefficient(problem.getNumberOfObjectives() + divisionsOuter - 1, divisionsOuter) +
				(divisionsInner == 0 ? 0 : CombinatoricsUtils.binomialCoefficient(problem.getNumberOfObjectives() + divisionsInner - 1, divisionsInner)));
	}
	
	/**
	 * Reads the divisions properties, if set, or provides default values for the problem.
	 * 
	 * @param properties the properties
	 * @param problem the problem
	 * @return the reference point divisions
	 */
	public static ReferencePointDivisions fromProperties(TypedProperties properties, Problem problem) {
		if (properties.contains("divisionsOuter") && properties.contains("divisionsInner")) {
			return new ReferencePointDivisions(
					(int)properties.getDouble("divisionsOuter", 4),
					(int)properties.getDouble("divisionsInner", 0));
		} 
		
		if (properties.contains("divisionsOuter") || properties.contains("divisionsInner")) {
			throw new FrameworkException("must include both divisionsOuter and divisionsInner");
		}
		
		if (properties.contains("divisions")) {
			return new ReferencePointDivisions((int)properties.getDouble("divisions", 4));
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
		
		return new ReferencePointDivisions(divisionsOuter, divisionsInner);
	}

}

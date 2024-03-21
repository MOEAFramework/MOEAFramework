/* The following source code is copied from the Coco Framework available at <https://github.com/numbbo/coco>
 * under the 3-clause BSD license.
 * 
 * The original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS file located in the Coco Framework
 * repository for more details.
 */
package org.moeaframework.problem.BBOB2016;

/**
 * The problem contains some basic properties of the coco_problem_t structure that can be accessed through its getter
 * functions.
 */
public class Problem {

	private long pointer; // Pointer to the coco_problem_t object
	
	private int dimension;
	private int number_of_objectives;
	private int number_of_constraints;
	
	private double[] lower_bounds;
	private double[] upper_bounds;
	private int number_of_integer_variables;
	
	private String id;
	private String name;
	
	private long index;

	/**
	 * Constructs the problem from the pointer.
	 */
	public Problem(long pointer) throws Exception {
		super();
		try {		
			this.dimension = CocoJNI.cocoProblemGetDimension(pointer);
			this.number_of_objectives = CocoJNI.cocoProblemGetNumberOfObjectives(pointer);
			this.number_of_constraints = CocoJNI.cocoProblemGetNumberOfConstraints(pointer);
			
			this.lower_bounds = CocoJNI.cocoProblemGetSmallestValuesOfInterest(pointer);
			this.upper_bounds = CocoJNI.cocoProblemGetLargestValuesOfInterest(pointer);
			this.number_of_integer_variables = CocoJNI.cocoProblemGetNumberOfIntegerVariables(pointer);
			
			this.id = CocoJNI.cocoProblemGetId(pointer);
			this.name = CocoJNI.cocoProblemGetName(pointer);

			this.index = CocoJNI.cocoProblemGetIndex(pointer);
			
			this.pointer = pointer;
		} catch (Exception e) {
			throw new Exception("Problem constructor failed.\n" + e.toString());
		}
	}
	
	/**
	 * Evaluates the function in point x and returns the result as an array of doubles. 
	 */
	public double[] evaluateFunction(double[] x) {
		return CocoJNI.cocoEvaluateFunction(this.pointer, x);
	}

	/**
	 * Evaluates the constraint in point x and returns the result as an array of doubles. 
	 */
	public double[] evaluateConstraint(double[] x) {
		return CocoJNI.cocoEvaluateConstraint(this.pointer, x);
	}

	public long getPointer() {
		return this.pointer;
	}
	
	public int getDimension() {
		return this.dimension;
	}
	
	public int getNumberOfObjectives() {
		return this.number_of_objectives;
	}
	
	public int getNumberOfConstraints() {
		return this.number_of_constraints;
	}
	
	public double[] getSmallestValuesOfInterest() {
		return this.lower_bounds;
	}
	
	public double getSmallestValueOfInterest(int index) {
		return this.lower_bounds[index];
	}

	public double[] getLargestValuesOfInterest() {
		return this.upper_bounds;
	}
	
	public double getLargestValueOfInterest(int index) {
		return this.upper_bounds[index];
	}
	
	public int getNumberOfIntegerVariabls() {
		return this.number_of_integer_variables;
	}

	public double[] getLargestFValuesOfInterest() {
		return CocoJNI.cocoProblemGetLargestFValuesOfInterest(pointer);
	}
	
	public String getId() {
		return this.id;
	}

	public String getName() {
		return this.name;
	}
	
	public long getEvaluations() {
		return CocoJNI.cocoProblemGetEvaluations(pointer);
	}
	
	public long getEvaluationsConstraints() {
		return CocoJNI.cocoProblemGetEvaluationsConstraints(pointer);
	}
	
	public long getIndex() {
		return this.index;
	}
	
	public boolean isFinalTargetHit() {
		return (CocoJNI.cocoProblemIsFinalTargetHit(pointer) == 1);
	}
	
	@Override
	public String toString() {		
		return this.getId();
	}
}

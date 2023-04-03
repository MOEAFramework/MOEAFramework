/* The following source code is copied from the Coco Framework available at <https://github.com/numbbo/coco>
 * under the 3-clause BSD license.
 * 
 * The original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS file located in the Coco Framework
 * repository for more details.
 */
package org.moeaframework.problem.BBOB2016;

/**
 * This class contains the declaration of all the CocoJNI functions. 
 */
public class CocoJNI {

	/* Load the library */
	static {
		System.loadLibrary("CocoJNI");
	}

	/* Native methods */
	public static native void cocoSetLogLevel(String logLevel);
	
	// Observer
	public static native long cocoGetObserver(String observerName, String observerOptions);
	public static native void cocoFinalizeObserver(long observerPointer);
	public static native long cocoProblemAddObserver(long problemPointer, long observerPointer);
	public static native long cocoProblemRemoveObserver(long problemPointer, long observerPointer);

	// Suite
	public static native long cocoGetSuite(String suiteName, String suiteInstance, String suiteOptions);
	public static native void cocoFinalizeSuite(long suitePointer);

	// Problem
	public static native long cocoSuiteGetNextProblem(long suitePointer, long observerPointer);
	public static native long cocoSuiteGetProblem(long suitePointer, long problemIndex);

	// Functions
	public static native double[] cocoEvaluateFunction(long problemPointer, double[] x);
	public static native double[] cocoEvaluateConstraint(long problemPointer, double[] x);

	// Getters
	public static native int cocoProblemGetDimension(long problemPointer);
	public static native int cocoProblemGetNumberOfObjectives(long problemPointer);
	public static native int cocoProblemGetNumberOfConstraints(long problemPointer);

	public static native double[] cocoProblemGetSmallestValuesOfInterest(long problemPointer);
	public static native double[] cocoProblemGetLargestValuesOfInterest(long problemPointer);
	public static native int cocoProblemGetNumberOfIntegerVariables(long problemPointer);
	
	public static native double[] cocoProblemGetLargestFValuesOfInterest(long problemPointer);

	public static native String cocoProblemGetId(long problemPointer);
	public static native String cocoProblemGetName(long problemPointer);
	
	public static native long cocoProblemGetEvaluations(long problemPointer);
	public static native long cocoProblemGetEvaluationsConstraints(long problemPointer);
	public static native long cocoProblemGetIndex(long problemPointer); 
	
	public static native int cocoProblemIsFinalTargetHit(long problemPointer);
}

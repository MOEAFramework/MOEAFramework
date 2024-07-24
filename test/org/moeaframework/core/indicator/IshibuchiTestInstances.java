package org.moeaframework.core.indicator;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.ProblemStub;

// Test instances found in Section 4 in:
//
//   H. Ishibuchi, H. Masuda, Y. Tanigaki and Y. Nojima, “Modified distance calculation in generational distance and
//   inverted generational distance,” Proc. of 8th International Conference on Evolutionary Multi-Criterion
//   Optimization, Part I, pp. 110-125, Guimarães, Portugal, March 29-April 1, 2015.
//
// Note that the paper uses GD and IGD indicators with powers of 1 and no normalization.  Custom versions of these
// indicators should be used when comparing results against the paper.
public class IshibuchiTestInstances {
	
	public static final NondominatedPopulation Example1RefSet;
	
	public static final NondominatedPopulation Example1SetA;
	
	public static final NondominatedPopulation Example1SetB;
	
	public static final NondominatedPopulation Example1SetC;
	
	public static final NondominatedPopulation Example2RefSet;
	
	public static final NondominatedPopulation Example2SetA;
	
	public static final NondominatedPopulation Example2SetB;
	
	public static final NondominatedPopulation Example3SetD;
	
	public static final NondominatedPopulation Example4RefSet;
	
	public static final NondominatedPopulation Example4SetA;
	
	public static final NondominatedPopulation Example4SetB;
	
	public static final NondominatedPopulation Example5RefSet;
	
	public static final NondominatedPopulation Example5SetA;
	
	public static final NondominatedPopulation Example5SetB;
	
	public static final NondominatedPopulation Example6RefSet;
	
	public static final NondominatedPopulation Example6SetA;
	
	public static final NondominatedPopulation Example6SetB;
	
	public static final NondominatedPopulation Example8RefSet;
	
	public static final NondominatedPopulation Example8SetA;
	
	public static final NondominatedPopulation Example8SetB;
	
	public static final NondominatedPopulation Example9RefSet;
	
	public static final NondominatedPopulation Example9SetA;
	
	public static final NondominatedPopulation Example9SetB;
	
	static {
		Example1RefSet = new NondominatedPopulation();
		Example1RefSet.add(MockSolution.of().withObjectives(0.0, 10.0));
		Example1RefSet.add(MockSolution.of().withObjectives(1.0, 0.0));
		
		Example1SetA = new NondominatedPopulation();
		Example1SetA.add(MockSolution.of().withObjectives(2.0, 5.0));
		
		Example1SetB = new NondominatedPopulation();
		Example1SetB.add(MockSolution.of().withObjectives(3.0, 9.0));
		
		Example1SetC = new NondominatedPopulation();
		Example1SetC.add(MockSolution.of().withObjectives(10.0, 10.0));
		
		Example2RefSet = new NondominatedPopulation();
		Example2RefSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		Example2RefSet.add(MockSolution.of().withObjectives(10.0, 0.0));
		
		Example2SetA = new NondominatedPopulation();
		Example2SetA.add(MockSolution.of().withObjectives(5.0, 2.0));
		
		Example2SetB = new NondominatedPopulation();
		Example2SetB.add(MockSolution.of().withObjectives(11.0, 3.0));
		
		Example3SetD = new NondominatedPopulation();
		Example3SetD.add(MockSolution.of().withObjectives(2.0, 1.0));
		
		Example4RefSet = new NondominatedPopulation();
		Example4RefSet.add(MockSolution.of().withObjectives(0.0, 10.0));
		Example4RefSet.add(MockSolution.of().withObjectives(1.0, 6.0));
		Example4RefSet.add(MockSolution.of().withObjectives(2.0, 2.0));
		Example4RefSet.add(MockSolution.of().withObjectives(6.0, 1.0));
		Example4RefSet.add(MockSolution.of().withObjectives(10.0, 0.0));
		
		Example4SetA = new NondominatedPopulation();
		Example4SetA.add(MockSolution.of().withObjectives(2.0, 4.0));
		Example4SetA.add(MockSolution.of().withObjectives(3.0, 3.0));
		Example4SetA.add(MockSolution.of().withObjectives(4.0, 2.0));
		
		Example4SetB = new NondominatedPopulation();
		Example4SetB.add(MockSolution.of().withObjectives(2.0, 8.0));
		Example4SetB.add(MockSolution.of().withObjectives(4.0, 4.0));
		Example4SetB.add(MockSolution.of().withObjectives(8.0, 2.0));
		
		Example5RefSet = new NondominatedPopulation();
		Example5RefSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		Example5RefSet.add(MockSolution.of().withObjectives(10.0, 0.0));
		
		Example5SetA = new NondominatedPopulation();
		Example5SetA.add(MockSolution.of().withObjectives(5.0, 2.0));
		
		Example5SetB = new NondominatedPopulation();
		Example5SetB.add(MockSolution.of().withObjectives(6.0, 4.0));
		Example5SetB.add(MockSolution.of().withObjectives(10.0, 3.0));
		
		Example6RefSet = new NondominatedPopulation();
		Example6RefSet.add(MockSolution.of().withObjectives(4.0, 4.0));
		
		Example6SetA = new NondominatedPopulation();
		Example6SetA.add(MockSolution.of().withObjectives(1.0, 5.0));
		
		Example6SetB = new NondominatedPopulation();
		Example6SetB.add(MockSolution.of().withObjectives(5.0, 6.0));
		
		Example8RefSet = new NondominatedPopulation();
		Example8RefSet.add(MockSolution.of().withObjectives(0.0, 0.0));
		
		Example8SetA = new NondominatedPopulation();
		Example8SetA.add(MockSolution.of().withObjectives(1.0, 8.0));
		Example8SetA.add(MockSolution.of().withObjectives(2.0, 2.0));
		Example8SetA.add(MockSolution.of().withObjectives(8.0, 1.0));
		
		Example8SetB = new NondominatedPopulation();
		Example8SetB.add(MockSolution.of().withObjectives(4.0, 3.0));
		
		Example9RefSet = new NondominatedPopulation();
		Example9RefSet.add(MockSolution.of().withObjectives(0.0, 0.0));
		
		Example9SetA = new NondominatedPopulation();
		Example9SetA.add(MockSolution.of().withObjectives(1.0, 8.0));
		Example9SetA.add(MockSolution.of().withObjectives(2.0, 2.0));
		Example9SetA.add(MockSolution.of().withObjectives(8.0, 1.0));
		
		Example9SetB = new NondominatedPopulation();
		Example9SetB.add(MockSolution.of().withObjectives(2.0, 2.0));
	}
	
	private IshibuchiTestInstances() {
		super();
	}
	
	public static double computeGD(NondominatedPopulation referenceSet, NondominatedPopulation approximationSet) {
		return new GenerationalDistance(new ProblemStub(2), referenceSet, Normalizer.none(), 1.0)
				.evaluate(approximationSet);
	}
	
	public static double computeIGD(NondominatedPopulation referenceSet, NondominatedPopulation approximationSet) {
		return new InvertedGenerationalDistance(new ProblemStub(2), referenceSet, Normalizer.none(), 1.0)
				.evaluate(approximationSet);
	}
	
	public static double computeGDPlus(NondominatedPopulation referenceSet, NondominatedPopulation approximationSet) {
		return new GenerationalDistancePlus(new ProblemStub(2), referenceSet, Normalizer.none())
				.evaluate(approximationSet);
	}
	
	public static double computeIGDPlus(NondominatedPopulation referenceSet, NondominatedPopulation approximationSet) {
		return new InvertedGenerationalDistancePlus(new ProblemStub(2), referenceSet, Normalizer.none())
				.evaluate(approximationSet);
	}

}

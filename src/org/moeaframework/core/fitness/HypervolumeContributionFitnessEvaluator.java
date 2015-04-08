//package org.moeaframework.core.fitness;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.List;
//
//import org.moeaframework.core.FitnessEvaluator;
//import org.moeaframework.core.Population;
//import org.moeaframework.core.Problem;
//import org.moeaframework.core.Solution;
//import org.moeaframework.core.indicator.Hypervolume;
//
//public class HypervolumeContributionFitnessEvaluator implements FitnessEvaluator {
//	
//	private final Problem problem;
//	
//	private final double offset;
//	
//	public HypervolumeContributionFitnessEvaluator(Problem problem) {
//		this(problem, 100.0);
//	}
//	
//	public HypervolumeContributionFitnessEvaluator(Problem problem, double offset) {
//		super();
//		this.problem = problem;
//		this.offset = offset;
//	}
//
//	@Override
//	public void evaluate(Population population) {
//		if (population.size() <= 2) {
//			for (Solution solution : population) {
//				solution.setAttribute(FITNESS_ATTRIBUTE, 0.0);
//			}
//		} else {
//			int numberOfObjectives = problem.getNumberOfObjectives();
//			List<Solution> solutions = toList(population);
//			List<Solution> solutionsCopy = new ArrayList<Solution>(solutions);
//			double totalVolume = Hypervolume.calculateHypervolume(solutionsCopy, solutionsCopy.size(), numberOfObjectives);
//			
//			for (int i = 0; i < population.size(); i++) {
//				solutionsCopy = new ArrayList<Solution>(solutions);
//				solutionsCopy.remove(i);
//				
//				double volume = Hypervolume.calculateHypervolume(solutionsCopy, solutionsCopy.size(), numberOfObjectives);
//				population.get(i).setAttribute(FITNESS_ATTRIBUTE, totalVolume - volume);
//			}
//		}
//	}
//	
//	private List<Solution> toList(Population population) {
//		List<Solution> result = new ArrayList<Solution>();
//		
//		double[] min = new double[problem.getNumberOfObjectives()];
//		double[] max = new double[problem.getNumberOfObjectives()];
//		
//		Arrays.fill(min, Double.POSITIVE_INFINITY);
//		Arrays.fill(max, Double.NEGATIVE_INFINITY);
//		
//		for (Solution solution : population) {
//			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
//				min[i] = Math.min(min[i], solution.getObjective(i));
//				max[i] = Math.max(max[i], solution.getObjective(i));
//			}
//		}
//		
//		for (Solution solution : population) {
//			Solution newSolution = solution.copy();
//			
//			for (int i = 0; i < problem.getNumberOfObjectives(); i++) {
//				newSolution.setObjective(i, (max[i] - (newSolution.getObjective(i) - min[i]) + offset) / (max[i] - min[i]));
//			}
//
//			result.add(newSolution);
//		}
//		
//		return result;
//	}
//
//	@Override
//	public boolean areLargerValuesPreferred() {
//		return true;
//	}
//
//}

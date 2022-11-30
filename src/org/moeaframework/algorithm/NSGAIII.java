package org.moeaframework.algorithm;

import org.moeaframework.core.Initialization;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.ChainedComparator;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.operator.AbstractCompoundVariation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.operator.TournamentSelection;
import org.moeaframework.core.operator.real.PM;
import org.moeaframework.core.operator.real.SBX;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.weights.NormalBoundaryDivisions;

/**
 * Implementation of NSGA-III.
 * 
 * References:
 * <ol>
 *   <li>Deb, K. and Jain, H.  "An Evolutionary Many-Objective Optimization
 *       Algorithm Using Reference-Point-Based Nondominated Sorting Approach,
 *       Part I: Solving Problems With Box Constraints."  IEEE Transactions on
 *       Evolutionary Computation, 18(4):577-601, 2014.
 *   <li>Deb, K. and Jain, H.  "Handling Many-Objective Problems Using an
 *       Improved NSGA-II Procedure.  WCCI 2012 IEEE World Contress on
 *       Computational Intelligence, Brisbane, Australia, June 10-15, 2012.
 * </ol>
 *
 */
public class NSGAIII extends NSGAII {
	
	/**
	 * Creates a new NSGA-III instance with default settings.
	 * 
	 * @param problem the problem to solve
	 */
	public NSGAIII(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));
	}
	
	/**
	 * Creates a new NSGA-III instance with the given number of reference point divisions.
	 * 
	 * @param problem the problem to solve
	 * @param divisions the number of divisions for generating reference points
	 */
	public NSGAIII(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem,
				new ReferencePointNondominatedSortingPopulation(problem.getNumberOfObjectives(), divisions),
				getDefaultSelection(problem),
				getDefaultVariation(problem),
				getDefaultInitialization(problem, divisions));
	}
	
	/**
	 * Constructs a new NSGA-III instance with the specified components.
	 * 
	 * @param problem the problem being solved
	 * @param population the reference point population used to store solutions
	 * @param selection the selection operator
	 * @param variation the variation operator
	 * @param initialization the initialization method
	 */
	public NSGAIII(Problem problem, ReferencePointNondominatedSortingPopulation population,
			Selection selection, Variation variation, Initialization initialization) {
		super(problem,
				population,
				null,
				selection,
				variation,
				initialization);
	}
	
	/**
	 * Returns the default initialization method that creates one population member for each reference point.
	 * The population size is rounded up to the nearest multiple of 4.
	 * 
	 * @param problem the problem
	 * @param divisions the number of divisions for generating reference points
	 * @return the initialization method
	 */
	private static final Initialization getDefaultInitialization(Problem problem, NormalBoundaryDivisions divisions) {
		int referencePoints = divisions.getNumberOfReferencePoints(problem);
		int populationSize = (int)Math.ceil(referencePoints / 4.0) * 4;
		
		return new RandomInitialization(problem, populationSize);
	}
	
	private static final Variation getDefaultVariation(Problem problem) {
		Variation result = OperatorFactory.getInstance().getVariation(problem);
		fixDefaultParameters(result);
		return result;
	}
	
	/**
	 * Fixes the parameters used by SBX and PM, if used, to match the original NSGA-III paper
	 * (thanks to Haitham Seada for identifying this discrepancy).
	 * 
	 * @param variation the variation operator
	 */
	private static final void fixDefaultParameters(Variation variation) {
		if (variation instanceof AbstractCompoundVariation<?>) {
			for (Variation innerVariation : (AbstractCompoundVariation<?>)variation) {
				fixDefaultParameters(innerVariation);
			}
		} else if (variation instanceof SBX) {
			((SBX)variation).setSwap(false);
			((SBX)variation).setDistributionIndex(30.0);
		} else if (variation instanceof PM) {
			((PM)variation).setDistributionIndex(20.0);
		}
	}
	
	/**
	 * Gets the default selection operator.  If the problem is unconstrained, then the parents
	 * are selected randomly from the population.  If the problem has constraints, then solutions
	 * are selected first using the aggregate constraint violation followed by random selection.
	 * 
	 * @param problem the problem
	 * @return the selection operator
	 */
	private static final Selection getDefaultSelection(Problem problem) {
		if (problem.getNumberOfConstraints() == 0) {
			return new Selection() {
	
				@Override
				public Solution[] select(int arity, Population population) {
					Solution[] result = new Solution[arity];
					
					for (int i = 0; i < arity; i++) {
						result[i] = population.get(PRNG.nextInt(population.size()));
					}
					
					return result;
				}
				
			};
		} else {
			return new TournamentSelection(2, new ChainedComparator(
					new AggregateConstraintComparator(),
					new DominanceComparator() {

						@Override
						public int compare(Solution solution1, Solution solution2) {
							return PRNG.nextBoolean() ? -1 : 1;
						}
						
					}));
		}
	}
	
	@Override
	public ReferencePointNondominatedSortingPopulation getPopulation() {
		return (ReferencePointNondominatedSortingPopulation)super.getPopulation();
	}

}

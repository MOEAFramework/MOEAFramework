package org.moeaframework.algorithm;

import org.moeaframework.core.AdaptiveGridArchive;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.Variation;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.operator.RandomInitialization;

/**
 * Implementation of the (1+1) Pareto Archived Evolution Strategy (PAES).  PAES
 * uses an adaptive grid archive to maintain a diverse set of solutions.
 * <p>
 * References:
 * <ol>
 *   <li>Knowles, J.D. and D. W. Corne (1999). The Pareto Archived Evolution
 *       Strategy: A New Baseline Algorithm for Pareto Multiobjective
 *       Optimisation. In Proceedings of the 1999 Congress on Evolutionary
 *       Computation (CEC'99), pp. 98-105.
 * </ol>
 */
public class PAES extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The mutation operator
	 */
	private final Variation variation;
	
	/**
	 * The dominance comparator.
	 */
	private final DominanceComparator comparator;
	
	/**
	 * Constructs a new PAES instance.
	 * 
	 * @param problem the problem
	 * @param variation the mutation operator
	 * @param bisections the number of bisections in the adaptive grid archive
	 * @param archiveSize the capacity of the adaptive grid archive
	 * @throws IllegalArgumentExceptio if the variation operator requires more
	 *         than one parent
	 */
	public PAES(Problem problem, Variation variation, int bisections,
			int archiveSize) {
		super(problem,
				new Population(),
				new AdaptiveGridArchive(archiveSize, problem, bisections),
				null);
		this.variation = variation;
		
		if (variation.getArity() != 1) {
			throw new IllegalArgumentException(
					"PAES only supports mutation operators with 1 parent");
		}
		
		comparator = new ParetoDominanceComparator();
	}

	@Override
	public AdaptiveGridArchive getArchive() {
		return (AdaptiveGridArchive)super.getArchive();
	}
	
	@Override
	protected void initialize() {
		// avoid calling super.initialize() since no initializer is set
		if (initialized) {
			throw new AlgorithmInitializationException(this, 
					"algorithm already initialized");
		}

		initialized = true;
		
		Solution solution = new RandomInitialization(problem, 1).initialize()[0];
		evaluate(solution);
		population.add(solution);
		archive.add(solution);
	}

	/**
	 * The test procedure to determine which solution, the parent or offspring,
	 * moves on to the next generation.  The solution in a lower density region
	 * in the archive is returned.
	 * 
	 * @param parent the parent solution
	 * @param offspring the offspring solution
	 * @return the solution moving on to the next genreation
	 */
	public Solution test(Solution parent, Solution offspring) {
		AdaptiveGridArchive archive = getArchive();
		int parentIndex = archive.findIndex(parent);
		int offspringIndex = archive.findIndex(offspring);
		
		if (parentIndex == -1) {
			// the parent is no longer in the archive
			return offspring;
		} else if (offspringIndex == -1) {
			// the offspring is no longer in the archive
			return parent;
		} else if (archive.getDensity(parentIndex) > archive.getDensity(offspringIndex)) {
			// the offspring is in a less dense region
			return offspring;
		} else {
			// the parent is in a less dense region
			return parent;
		}
	}

	@Override
	protected void iterate() {
		Solution parent = population.get(0);
		Solution offspring = variation.evolve(new Solution[] { parent })[0];
		
		evaluate(offspring);
		
		int flag = comparator.compare(parent, offspring);
		
		if (flag == 1) {
			// the offspring dominates the parent
			population.remove(0);
			population.add(offspring);
			archive.add(offspring);
		} else if (flag == 0) {
			// the parent and offspring are non-dominated
			if (archive.add(offspring)) {
				population.remove(0);
				population.add(test(parent, offspring));
			}
		}
	}

}

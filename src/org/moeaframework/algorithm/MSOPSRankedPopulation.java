/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.algorithm;

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.algorithm.single.MinMaxObjectiveComparator;
import org.moeaframework.algorithm.single.VectorAngleDistanceScalingComparator;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.RankComparator;

/**
 * Population implementing the ranking scheme used by the Multiple Single
 * Objective Pareto Sampling (MSOPS) algorithm.  Solutions are ranked with 
 * respect to T weight vectors based on two metrics --- the weighted min-max
 * aggregate and the vector angle distance scaling (VADS) metric --- producing
 * 2*T scores/rankings.  The overall ranking of a solution is computed using a
 * lexicographical ordering, where the best rank is given to the solution with
 * the best individual ranking among the T weight vectors.
 * <p>
 * Three methods of constraint handling are described in the literature.  In the
 * original paper, [1], E. Hughes recommends ranking feasible and infeasible
 * solutions separately and ensuring the ranking if worse for the infeasible
 * solutions.  In the published Matlab code, [2], E. Hughes penalizes the
 * objective values based on the magnitude of the constraint violation.  Lastly,
 * in [3], E. Hughes states that the infeasible solutions are ranked in order
 * by their aggregate constraint violation.  Regardless of these differences,
 * all three methods ensure the ranking is always worse for infeasible
 * solutions.  This implementation follows this practice by penalizing the
 * scores based on the magnitude of the constraint violation (similar to [2]).
 * <p>
 * References:
 * <ol>
 *   <li>E. J. Hughes.  "Multiple Single Objective Pareto Sampling."  2003
 *       Congress on Evolutionary Computation, pp. 2678-2684.
 *   <li>Matlab source code available from
 *       <a href="http://code.evanhughes.org/">http://code.evanhughes.org/</a>.
 *   <li>E. J. Hughes.  "MSOPS-II: A general-purpose many-objective optimizer."
 *       2007 IEEE Congress on Evolutionary Computation, pp. 3944-3951.
 *   <li>E. J. Hughes.  "Evolutionary many-objective optimization: many once or
 *       one many."  2005 IEEE Congress on Evolutionary Computation,
 *       pp. 222-227.
 * </ol>
 */
public class MSOPSRankedPopulation extends Population {
	
	/**
	 * {@code true} if the population has been modified but the ranking method
	 * has not yet been invoked; {@code false} otherwise.
	 */
	private boolean modified;
	
	/**
	 * The weight vectors.
	 */
	private List<double[]> weights;
	
	/**
	 * Matrix of scores, updated by calling {@link #update()}.
	 */
	double[][] scores;
	
	/**
	 * Matrix of rankings, updated by calling {@link #update()}.
	 */
	int[][] ranks;
	
	/**
	 * Matrix of rankings sorted on each row, updated by calling
	 * {@link #update()}.
	 */
	int[][] sortedRanks;

	/**
	 * Constructs an empty population that maintains the {@code rank} and
	 * attribute for its solutions using the MSOPS ranking method.
	 * 
	 * @param weights the weight vectors
	 */
	public MSOPSRankedPopulation(List<double[]> weights) {
		super();
		this.weights = weights;
	}

	/**
	 * Constructs a population initialized with the specified solutions that 
	 * maintains the {@code rank} attribute for its solutions using the MSOPS
	 * ranking method.
	 * 
	 * @param weights the weight vectors
	 * @param iterable the solutions used to initialize this population
	 */
	public MSOPSRankedPopulation(List<double[]> weights,
			Iterable<? extends Solution> iterable) {
		this(weights);
		addAll(iterable);
	}

	@Override
	public boolean add(Solution solution) {
		modified = true;
		return super.add(solution);
	}

	@Override
	public void replace(int index, Solution solution) {
		modified = true;
		super.replace(index, solution);
	}

	@Override
	public Solution get(int index) {
		if (modified) {
			update();
		}

		return super.get(index);
	}

	@Override
	public void remove(int index) {
		modified = true;
		super.remove(index);
	}

	@Override
	public boolean remove(Solution solution) {
		modified = true;
		return super.remove(solution);
	}

	@Override
	public void clear() {
		modified = true;
		super.clear();
	}

	@Override
	public Iterator<Solution> iterator() {
		if (modified) {
			update();
		}

		return super.iterator();
	}

	@Override
	public void sort(Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.sort(comparator);
	}

	@Override
	public void truncate(int size, Comparator<? super Solution> comparator) {
		if (modified) {
			update();
		}

		super.truncate(size, comparator);
	}

	/**
	 * Equivalent to calling {@code truncate(size, new RankComparator())}.
	 * 
	 * @param size the target population size after truncation
	 */
	public void truncate(int size) {
		truncate(size, new RankComparator());
	}
	
	/**
	 * Returns the neighborhood of solutions nearest to and including the
	 * given solution.
	 * 
	 * @param index the index of the solution at the center of the neighborhood
	 * @param size the size of the neighborhood
	 * @return the solutions in the neighborhood
	 */
	public Population findNearest(int index, int size) {
		if (modified) {
			update();
		}
		
		int P = size();
		int T = weights.size();
		
		// first identify the best/closest weight vectors for this solution
		List<Integer> bestWeights = new ArrayList<Integer>();
		
		for (int i = 0; i < 2*T; i++) {
			if (ranks[index][i] == sortedRanks[index][0]) {
				bestWeights.add(i);
			}
		}
		
		// randomly select one of these weight vectors
		final int selectedWeight = PRNG.nextItem(bestWeights);
		
		// sort the column to determine which solutions are ranked nearest to
		// this vector
		Integer[] indices = new Integer[P];
		
		for (int i = 0; i < P; i++) {
			indices[i] = i;
		}
		
		Arrays.sort(indices, new Comparator<Integer>() {

			@Override
			public int compare(Integer i1, Integer i2) {
				int rank1 = sortedRanks[i1][selectedWeight];
				int rank2 = sortedRanks[i2][selectedWeight];
				
				return rank1 < rank2 ? -1 : rank1 > rank2 ? 1 : 0;
			}
			
		});
		
		// create and return the population of nearest neighbors
		Population result = new Population();
		result.add(get(index));
		
		for (int i = 0; i < size; i++) {
			if (indices[i] != index) {
				result.add(get(indices[i]));
			}
		}
		
		return result;
	}

	/**
	 * Updates the rank attribute of all solutions in this population using the
	 * MSOPS ranking method.  This method will in general be called
	 * automatically when the population is modified.  However, only changes
	 * made to this population can be tracked; changes made directly to the
	 * contained solutions will not be detected.  Therefore, it may be necessary
	 * to invoke {@link #update()} manually.
	 */
	public void update() {
		modified = false;
		
		final int P = size();
		final int T = weights.size();
		double maxScore = Double.NEGATIVE_INFINITY;
		
		scores = new double[P][2*T];
		ranks = new int[P][2*T];
		sortedRanks = new int[P][];
		
		// first compute the raw score
		for (int i = 0; i < P; i++) {
			Solution solution = get(i);
			
			for (int j = 0; j < T; j++) {
				scores[i][j] = MinMaxObjectiveComparator.calculateFitness(solution, weights.get(j));
				scores[i][j+T] = VectorAngleDistanceScalingComparator.calculateFitness(solution, weights.get(j), 100.0);
				maxScore = Math.max(maxScore, Math.max(scores[i][j], scores[i][j+T]));
			}
		}
		
		// offset score to handle constraints
		for (int i = 0; i < P; i++) {
			Solution solution = get(i);
			
			if (solution.violatesConstraints()) {
				for (int j = 0; j < T; j++) {
					scores[i][j] = scores[i][j] + maxScore +
							AggregateConstraintComparator.getConstraints(solution);
				}
			}
		}
		
		// convert from raw score to rank
		for (int i = 0; i < 2*T; i++) {
			final double[] weightScores = new double[size()];
			Integer[] indices = new Integer[size()];
			
			for (int j = 0; j < P; j++) {
				weightScores[j] = scores[j][i];
				indices[j] = j;
			}
			
			Arrays.sort(indices, new Comparator<Integer>() {

				@Override
				public int compare(Integer i1, Integer i2) {
					return Double.compare(weightScores[i1], weightScores[i2]);
				}
				
			});
			
			for (int j = 0; j < P; j++) {
				ranks[indices[j]][i] = j;
			}
		}
		
		// sort each row by rank
		for (int i = 0; i < P; i++) {
			int[] row = ranks[i].clone();
			Arrays.sort(row);
			sortedRanks[i] = row;
		}
		
		// assign fitness to each individual
		Integer[] indices = new Integer[P];
		
		for (int i = 0; i < P; i++) {
			indices[i] = i;
		}
		
		Arrays.sort(indices, new Comparator<Integer>() {

			@Override
			public int compare(Integer i1, Integer i2) {
				int[] ranks1 = sortedRanks[i1];
				int[] ranks2 = sortedRanks[i2];
				
				for (int i = 0; i < 2*T; i++) {
					if (ranks1[i] < ranks2[i]) {
						return -1;
					} else if (ranks1[i] > ranks2[i]) {
						return 1;
					}
				}

				return 0;
			}
			
		});
		
		for (int i = 0; i < P; i++) {
			get(indices[i]).setAttribute(RANK_ATTRIBUTE, i);
		}
	}

}

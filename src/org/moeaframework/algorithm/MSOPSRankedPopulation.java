package org.moeaframework.algorithm;

import static org.moeaframework.core.FastNondominatedSorting.RANK_ATTRIBUTE;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.moeaframework.algorithm.single.TchebychevObjectiveComparator;
import org.moeaframework.core.Population;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.AggregateConstraintComparator;
import org.moeaframework.core.comparator.RankComparator;

/**
 * Population implementing the ranking scheme used by the Multiple Single
 * Objective Pareto Sampling algorithm.
 */
public class MSOPSRankedPopulation extends Population {
	
	/**
	 * {@code true} if the population has been modified but the ranking method
	 * has not yet been invoked; {@code false} otherwise.
	 */
	private boolean modified;
	
	private List<double[]> weights;

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
		final double[][] scores = new double[P][T];
		final int[][] ranks = new int[P][T];
		double maxScore = Double.NEGATIVE_INFINITY;
		
		// first compute the raw score
		for (int i = 0; i < P; i++) {
			Solution solution = get(i);
			
			for (int j = 0; j < T; j++) {
				scores[i][j] = calculateScore(solution, weights.get(j));
				maxScore = Math.max(maxScore, scores[i][j]);
			}
		}
		
		// offset score to handle constraints (not included in [1])
		for (int i = 0; i < P; i++) {
			Solution solution = get(i);
			
			if (solution.violatesConstraints()) {
				for (int j = 0; j < T; j++) {
					scores[i][j] = scores[i][j] + maxScore +
							AggregateConstraintComparator.getConstraints(solution);
				}
			}
		}
		
//		System.out.println("Scores:");
//		for (int i = 0; i < P; i++) {
//			for (int j = 0; j < T; j++) {
//				if (j > 0) {
//					System.out.print(", ");
//				}
//				System.out.print(scores[i][j]);
//			}
//			System.out.println();
//		}
		
		// convert from raw score to rank
		for (int i = 0; i < T; i++) {
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
			Arrays.sort(ranks[i]);
		}
		
//		System.out.println("Ranks:");
//		for (int i = 0; i < P; i++) {
//			for (int j = 0; j < T; j++) {
//				if (j > 0) {
//					System.out.print(", ");
//				}
//				System.out.print(ranks[i][j]);
//			}
//			System.out.println();
//		}
		
		// assign fitness to each individual
		Integer[] indices = new Integer[P];
		
		for (int i = 0; i < P; i++) {
			indices[i] = i;
		}
		
		Arrays.sort(indices, new Comparator<Integer>() {

			@Override
			public int compare(Integer i1, Integer i2) {
				int[] ranks1 = ranks[i1];
				int[] ranks2 = ranks[i2];
				
				for (int i = 0; i < T; i++) {
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
		
//		System.out.println("Fitness:");
//		for (int i = 0; i < P; i++) {
//			System.out.println(get(i).getAttribute(RANK_ATTRIBUTE));
//		}
	}
	
	public double calculateScore(Solution solution, double[] weights) {
		return TchebychevObjectiveComparator.calculateFitness(solution, weights);
	}

}

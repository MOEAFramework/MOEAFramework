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
package org.moeaframework.core.operator;

import org.moeaframework.core.PRNG;
import org.moeaframework.core.Population;
import org.moeaframework.core.Selection;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;

/**
 * Deterministic tournament selection operator. The tournament size specifies
 * the number of candidate solutions selected randomly from a population. The
 * winner of a tournament is the best solution in the pool. Unlike probabilistic
 * tournament selection, the best solution is always returned in deterministic
 * tournament selection. If two or more solutions are the best in the pool, one
 * solution is randomly selected with equal probability as the tournament
 * winner.
 * <p>
 * Binary tournament selection exhibits the same selection pressure as linear
 * ranking but without the computational overhead required for ranking. Larger
 * tournament sizes result in greedier selection. Solutions are selected with
 * replacement.
 */
public class TournamentSelection implements Selection {

	/**
	 * The comparator used to determine the tournament winner.
	 */
	private final DominanceComparator comparator;

	/**
	 * The tournament size. This is the number of solutions sampled from which
	 * the tournament winner is selected.
	 */
	private int size;

	/**
	 * Constructs a binary tournament selection operator using Pareto dominance.
	 */
	public TournamentSelection() {
		this(2);
	}

	/**
	 * Constructs a binary tournament selection operator using the specified
	 * dominance comparator.
	 * 
	 * @param comparator the comparator used to determine the tournament winner
	 */
	public TournamentSelection(DominanceComparator comparator) {
		this(2, comparator);
	}

	/**
	 * Constructs a tournament selection operator of the specified size using
	 * Pareto dominance.
	 * 
	 * @param size the tournament size
	 */
	public TournamentSelection(int size) {
		this(size, new ParetoDominanceComparator());
	}

	/**
	 * Constructs a tournament selection operator of the specified size and
	 * using the specified dominance comparator.
	 * 
	 * @param size the tournament size
	 * @param comparator the comparator used to determine the tournament winner
	 */
	public TournamentSelection(int size, DominanceComparator comparator) {
		super();
		this.size = size;
		this.comparator = comparator;
	}

	/**
	 * Returns the tournament size.
	 * 
	 * @return the tournament size
	 */
	public int getSize() {
		return size;
	}

	/**
	 * Sets the tournament size.
	 * 
	 * @param size the new tournament size
	 */
	public void setSize(int size) {
		this.size = size;
	}

	/**
	 * Returns the dominance comparator used to determine the tournament winner.
	 * 
	 * @return the comparator used to determine the tournament winner
	 */
	public DominanceComparator getComparator() {
		return comparator;
	}

	@Override
	public Solution[] select(int arity, Population population) {
		Solution[] result = new Solution[arity];

		for (int i = 0; i < arity; i++) {
			result[i] = select(population);
		}

		return result;
	}

	/**
	 * Performs deterministic tournament selection with the specified
	 * population, returning the tournament winner. If more than one solution is
	 * a winner, one of the winners is returned with equal probability.
	 * 
	 * @param population the population from which candidate solutions are
	 *        selected
	 * @return the winner of tournament selection
	 */
	private Solution select(Population population) {
		Solution winner = population.get(PRNG.nextInt(population.size()));

		for (int i = 1; i < size; i++) {
			Solution candidate = population.get(PRNG.nextInt(population.size()));

			int flag = comparator.compare(winner, candidate);

			if (flag > 0) {
				winner = candidate;
			}
		}

		return winner;
	}
	
	/**
	 * Performs binary tournament selection.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @param comparator the comparison operator
	 * @return the solution that wins the tournament
	 */
	public static Solution binaryTournament(Solution solution1,
			Solution solution2, DominanceComparator comparator) {
		int flag = comparator.compare(solution1, solution2);

		if (flag > 0) {
			return solution2;
		} else {
			return solution1;
		}
	}
	
	/**
	 * Performs binary tournament selection with Pareto dominance.
	 * 
	 * @param solution1 the first solution
	 * @param solution2 the second solution
	 * @return the solution that wins the tournament
	 */
	public static Solution binaryTournament(Solution solution1,
			Solution solution2) {
		return binaryTournament(solution1, solution2,
				new ParetoDominanceComparator());
	}

}

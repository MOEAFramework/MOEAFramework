/* Copyright 2009-2024 David Hadka
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

import org.apache.commons.math3.util.ArithmeticUtils;
import org.moeaframework.core.AdaptiveGridArchive;
import org.moeaframework.core.Population;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.comparator.DominanceComparator;
import org.moeaframework.core.comparator.ParetoDominanceComparator;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.operator.Mutation;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Implementation of the (1+1) Pareto Archived Evolution Strategy (PAES).  PAES uses an adaptive grid archive to
 * maintain a diverse set of solutions.
 * <p>
 * References:
 * <ol>
 *   <li>Knowles, J.D. and D. W. Corne (1999). The Pareto Archived Evolution Strategy: A New Baseline Algorithm for
 *       Pareto Multiobjective Optimisation. In Proceedings of the 1999 Congress on Evolutionary Computation (CEC'99),
 *       pp. 98-105.
 * </ol>
 */
public class PAES extends AbstractEvolutionaryAlgorithm {
	
	/**
	 * The dominance comparator.
	 */
	private final DominanceComparator comparator;
	
	/**
	 * Constructs a new PAES instance with default settings.
	 * 
	 * @param problem the problem
	 */
	public PAES(Problem problem) {
		this(problem, OperatorFactory.getInstance().getMutation(problem), 8, 100);
	}
	
	/**
	 * Constructs a new PAES instance.
	 * 
	 * @param problem the problem
	 * @param mutation the mutation operator
	 * @param bisections the number of bisections in the adaptive grid archive
	 * @param archiveSize the capacity of the adaptive grid archive
	 */
	public PAES(Problem problem, Mutation mutation, int bisections, int archiveSize) {
		super(problem,
				1,
				new Population(),
				new AdaptiveGridArchive(archiveSize, problem, ArithmeticUtils.pow(2, bisections)),
				new RandomInitialization(problem),
				mutation);
		
		comparator = new ParetoDominanceComparator();
	}
	
	/**
	 * Replaces the mutation operator to be used by this algorithm.
	 * 
	 * @param mutation the mutation operator
	 */
	@Property("operator")
	public void setVariation(Mutation mutation) {
		super.setVariation(mutation);
	}
	
	@Override
	public Mutation getVariation() {
		return (Mutation)super.getVariation();
	}

	@Override
	public AdaptiveGridArchive getArchive() {
		return (AdaptiveGridArchive)super.getArchive();
	}
	
	/**
	 * The test procedure to determine which solution, the parent or offspring, moves on to the next generation.  The
	 * solution in a lower density region in the archive is returned.
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
		Population population = getPopulation();
		AdaptiveGridArchive archive = getArchive();
		Solution parent = population.get(0);
		Solution offspring = getVariation().mutate(parent);
		
		evaluate(offspring);
		
		int flag = comparator.compare(parent, offspring);
		
		if (flag == 1) {
			// the offspring dominates the parent
			population.replace(0, offspring);
			archive.add(offspring);
		} else if (flag == 0) {
			// the parent and offspring are non-dominated
			if (archive.add(offspring)) {
				population.replace(0, test(parent, offspring));
			}
		}
	}
	
	@Override
	public void applyConfiguration(TypedProperties properties) {
		if (properties.contains("archiveSize") || properties.contains("bisections")) {
			int archiveSize = properties.getInt("archiveSize", getArchive().getCapacity());
			int bisections = properties.getInt("bisections", getArchive().getBisections());
			setArchive(new AdaptiveGridArchive(archiveSize, problem, ArithmeticUtils.pow(2, bisections)));
		}
		
		super.applyConfiguration(properties);
	}

	@Override
	public TypedProperties getConfiguration() {
		TypedProperties properties = super.getConfiguration();
		
		properties.setInt("archiveSize", getArchive().getCapacity());
		properties.setInt("bisections", getArchive().getBisections());
		
		return properties;
	}

}

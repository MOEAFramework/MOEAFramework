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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Solution;
import org.moeaframework.core.initialization.RandomInitialization;
import org.moeaframework.core.population.Population;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.io.Resources;
import org.moeaframework.util.io.Resources.ResourceOption;
import org.moeaframework.util.weights.GeneralizedDecomposition;
import org.moeaframework.util.weights.NormalBoundaryDivisions;
import org.moeaframework.util.weights.NormalBoundaryIntersectionGenerator;

/**
 * MOEA/D with weights produced by generalized decomposition (GD).
 * <p>
 * Very briefly, MOEA/D typically uses randomly-generated weights that aim to produce a diverse set of solutions.
 * While newer algorithms like NSGA-III have the option to supply "target points" where the algorithm can focus search,
 * picking weights for MOEA/D that produce solutions near a given target is non-trivial.  This is further complicated
 * when the Pareto front shape is disjoint / multimodal.
 * <p>
 * With GD, we can take a target point (or points) and compute the ideal weights for MOEA/D or any other algorithm
 * using the Chebychev / Tchebycheff scalarizing function.  Alternatively, we can use GD to compute a set of weights
 * that should yield better and more uniformly-distributed results than randomly-generated weights.
 * <p>
 * References:
 * <ol>
 *   <li>Giagkiozis, I., R. C. Purshouse, and P. J. Fleming (2013).  "Generalized Decomposition."  Evolutionary
 *       Multi-Criterion Optimization, 7th International Conference, pp. 428-442.
 * </ol>
 */
public class GDMOEAD extends MOEAD {

	public GDMOEAD(Problem problem) {
		this(problem, NormalBoundaryDivisions.forProblem(problem));
	}
	
	public GDMOEAD(Problem problem, String targets) {
		this(problem, new GeneralizedDecomposition(loadTargets(targets)));
	}
	
	public GDMOEAD(Problem problem, NormalBoundaryDivisions divisions) {
		this(problem, new GeneralizedDecomposition(new NormalBoundaryIntersectionGenerator(
				problem.getNumberOfObjectives(), divisions)));
	}
	
	public GDMOEAD(Problem problem, GeneralizedDecomposition weightGenerator) {
		super(problem,
				weightGenerator.size(),
				20, //neighborhoodSize
				weightGenerator,
				new RandomInitialization(problem),
				OperatorFactory.getInstance().getVariation(problem.isType(RealVariable.class)? "de+pm": null, problem),
				0.9, //delta
				2, //eta
				-1); //updateUtility
	}
	
	private static List<double[]> loadTargets(String resource) {
		try {
			File file = Resources.asFile(GDMOEAD.class, "/" + resource, ResourceOption.REQUIRED,
					ResourceOption.TEMPORARY, ResourceOption.FILE);
			
			Population population = Population.load(file);
			
			List<double[]> targets = new ArrayList<double[]>();
			
			for (Solution solution : population) {
				targets.add(solution.getObjectiveValues());
			}
			
			return targets;
		} catch (IOException e) {
			throw new FrameworkException("failed to load " + resource, e);
		}
	}
	
}

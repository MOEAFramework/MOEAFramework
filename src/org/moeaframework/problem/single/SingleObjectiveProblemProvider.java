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
package org.moeaframework.problem.single;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.RegisteredProblemProvider;

/**
 * Problem provider for the single objective test problems.
 */
public class SingleObjectiveProblemProvider extends RegisteredProblemProvider {

	/**
	 * Constructs and registers the single objective problems.
	 */
	public SingleObjectiveProblemProvider() {
		super();
		
		register("Ackley", Ackley::new, null);
		register("Beale", Beale::new, null);
		register("Griewank", Griewank::new, null);
		register("Himmelblau", Himmelblau::new, null);
		register("Rastrigin", Rastrigin::new, null);
		register("Rosenbrock", Rosenbrock::new, null);
		register("Schwefel", Schwefel::new, null);
		register("Sphere", Sphere::new, null);
		register("Zakharov", Zakharov::new, null);
	}
	
	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		Problem problem = getProblem(name);
		
		if (problem instanceof AbstractSingleObjectiveProblem singleObjectiveProblem) {
			return singleObjectiveProblem.getReferenceSet();
		}
		
		return null;
	}
}

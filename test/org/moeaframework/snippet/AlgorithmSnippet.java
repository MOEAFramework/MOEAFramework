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
package org.moeaframework.snippet;

import org.junit.Test;
import org.moeaframework.Executor;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.configuration.Property;
import org.moeaframework.core.operator.Variation;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.misc.Srinivas;

@SuppressWarnings("unused")
public class AlgorithmSnippet {

	@Test
	public void direct() {
		// begin-example:direct
		Problem problem = new UF1();

		NSGAII algorithm = new NSGAII(problem);
		algorithm.setInitialPopulationSize(250);
		algorithm.run(10000);

		NondominatedPopulation result = algorithm.getResult();
		// end-example:direct
	}
	
	@Test
	public void executor() {
		// begin-example:executor
		NondominatedPopulation results = new Executor()
			    .withProblem("UF1")
			    .withAlgorithm("NSGA-II")
			    .withProperty("populationSize", 250)
			    .withMaxEvaluations(10000)
			    .run();
		// end-example:executor
	}
	
}

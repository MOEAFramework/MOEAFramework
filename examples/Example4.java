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
import java.io.IOException;
import org.moeaframework.algorithm.MOEAD;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.pso.OMOPSO;
import org.moeaframework.analysis.IndicatorStatistics;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.CEC2009.UF1;

/**
 * In Example 2, we computed the hypervolume and generational distance for a single
 * run.  We can perform more extensive experiments comparing multiple algorithms
 * using multiple random seeds to statistically compare results.
 */
public class Example4 {

	public static void main(String[] args) throws IOException {
		Problem problem = new UF1();
		NondominatedPopulation referenceSet = NondominatedPopulation.load("pf/UF1.dat");

		// Collect statistics for the hypervolume indicator
		Hypervolume hypervolume = new Hypervolume(problem, referenceSet);
		IndicatorStatistics statistics = new IndicatorStatistics(hypervolume);
		
		// Run each algorithm with 10 random seeds
		for (int seed = 0; seed < 10; seed++) {
			PRNG.setSeed(seed);
			
			NSGAII algorithm1 = new NSGAII(problem);
			algorithm1.run(10000);
			statistics.add("NSGA-II", algorithm1.getResult());
			
			MOEAD algorithm2 = new MOEAD(problem);
			algorithm2.run(10000);
			statistics.add("MOEA/D", algorithm2.getResult());
			
			OMOPSO algorithm3 = new OMOPSO(problem);
			algorithm3.run(10000);
			statistics.add("OMOPSO", algorithm3.getResult());
		}

		// Display the statistics
		statistics.display();
	}
	
}

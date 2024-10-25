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

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

/**
 * Quality indicators are used to compare results between different algorithms.  Here,
 * we calculate the indicators in relation to a known reference set. These reference sets
 * contain optimal solutions to the problem.  Reference sets for most test problems can
 * be found in the ./pf/ directory.
 */
public class Example2 {

	public static void main(String[] args) throws IOException {
		// solve the 2-D DTLZ2 problem
		Problem problem = new DTLZ2(2);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		NondominatedPopulation approximationSet = algorithm.getResult();
		
		// load the reference set and evaluate the quality indicators
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.2D.pf");
		
		Indicators indicators = Indicators.all(problem, referenceSet);
		indicators.apply(approximationSet).display();
	}

}

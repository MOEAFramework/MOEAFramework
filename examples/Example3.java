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
import org.moeaframework.core.EpsilonBoxDominanceArchive;
import org.moeaframework.core.Problem;
import org.moeaframework.core.operator.real.PCX;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * We can also configure algorithms with different parameters.  In this example,
 * we setup NSGA-II to use the Parent Centric Crossover (PCX) operator, use a
 * population size of 250, and include an archive to store the best solutions.
 */
public class Example3 {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.setInitialPopulationSize(250);
		algorithm.setVariation(new PCX(5, 2));
		algorithm.setArchive(new EpsilonBoxDominanceArchive(0.01));
		
		algorithm.run(10000);

		algorithm.getResult().display();
	}

}

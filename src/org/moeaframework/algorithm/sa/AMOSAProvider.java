/* Copyright 2018-2019 Ibrahim DEMIR
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
package org.moeaframework.algorithm.sa;

import java.util.Properties;

import org.moeaframework.algorithm.sa.AMOSA;
import org.moeaframework.core.Algorithm;
import org.moeaframework.core.Initialization;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Variation;
import org.moeaframework.core.operator.RandomInitialization;
import org.moeaframework.core.spi.AlgorithmProvider;
import org.moeaframework.core.spi.OperatorFactory;
import org.moeaframework.util.TypedProperties;

public class AMOSAProvider extends AlgorithmProvider {

	@Override
	public Algorithm getAlgorithm(String name, Properties properties, Problem problem) {
		if (name.equalsIgnoreCase("AMOSA")) {
			// if the user requested the RandomWalker algorithm
			TypedProperties typedProperties = new TypedProperties(properties);
			
			// to be used at initialization the archive by the size of gamma*SL (default to 100)(gamma > 1)
			double gamma = typedProperties.getDouble("gamma", 2.0d);
			gamma = gamma<1.0d?2.0d:gamma;
			
			// Soft Limit SL (default to 100)
			int softLimit = typedProperties.getInt("SL", 100);
			
			// Hard Limit HL (default to 10)
			int hardLimit = typedProperties.getInt("HL", 10);

			double tMin = typedProperties.getDouble("tMin", 0.0000001d);
			double tMax = typedProperties.getDouble("tMax", 200d);
			double alpha = typedProperties.getDouble("alpha", 0.8d);
			int numberOfIterationPerTemperature = typedProperties.getInt("iter", 500);
			int numberOfHillClimbingIterationsForRefinement = typedProperties.getInt("hillClimbIter", 20);
			
			// Initialize the algorithm with randomly-generated solutions
			Initialization initialization = new RandomInitialization(problem, (int)gamma*softLimit);
			
			// Use the operator factory that problem provides
			Variation variation = OperatorFactory.getInstance().getVariation(
					OperatorFactory.getInstance().getDefaultMutation(problem), 
					properties,
					problem);
			
			// Construct and return the RandomWalker algorithm
			return new AMOSA(problem, initialization, variation, softLimit, hardLimit, tMin, tMax, alpha, numberOfIterationPerTemperature, numberOfHillClimbingIterationsForRefinement);
		} else {
			// return null if the user requested a different algorithm
			return null;
		}
	}

}

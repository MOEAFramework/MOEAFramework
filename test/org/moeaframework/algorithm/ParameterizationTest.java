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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.analysis.sensitivity.Parameter;
import org.moeaframework.analysis.sensitivity.ParameterFile;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.AlgorithmFactory;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.util.TypedProperties;

/**
 * Tests the parameterization of algorithms, ensuring the parameters defined in
 * parameter description files in the directory {@value directory} are actually
 * accessed by the algorithm.
 */
public class ParameterizationTest {
	
	/**
	 * The directory storing the parameter description files.
	 */
	private static final String directory = "./params/";
	
	/**
	 * The algorithms being tested.
	 */
	private final String[] algorithms = { "eMOEA", "eNSGAII", "GDE3",
			"IBEA", "MOEAD", "NSGAII", "OMOPSO", "SPEA2" };
	
	/**
	 * Tests all listed algorithms to ensure all parameters in their associated
	 * parameter description file are accessed by the algorithm.  This test
	 * excludes the parameter {@code maxEvaluations}.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void test() throws IOException {
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		
		for (String algorithm : algorithms) {
			File file = new File(directory, algorithm + "_Params");
			
			if (!file.exists()) {
				System.err.println(file + " does not exist, skipping test");
				continue;
			}
			
			ParameterFile pf = new ParameterFile(file);
			TypedProperties properties = new TypedProperties();

			AlgorithmFactory.getInstance().getAlgorithm(algorithm, properties, 
					problem);
			
			for (int i=0; i<pf.size(); i++) {
				Parameter parameter = pf.get(i);
				
				if (parameter.getName().equals("maxEvaluations")) {
					continue;
				}
				
				Assert.assertTrue(algorithm + " not accessing all parameters",
						properties.getAccessedProperties().contains(parameter.getName()));
			}
			
		}
	}

}

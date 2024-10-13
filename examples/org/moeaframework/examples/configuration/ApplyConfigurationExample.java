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
package org.moeaframework.examples.configuration;

import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.Problem;
import org.moeaframework.core.TypedProperties;
import org.moeaframework.problem.DTLZ.DTLZ2;

/**
 * Demonstrates applying a configuration stored in {@link TypedProperties}.  You might notice similaries between
 * this approach and using an {@link Executor}.
 */
public class ApplyConfigurationExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		NSGAII algorithm = new NSGAII(problem);
		
		TypedProperties properties = new TypedProperties();
		properties.setInt("populationSize", 250);
		properties.setString("operator", "pcx");
		properties.setInt("pcx.parents", 10);
		properties.setInt("pcx.offspring", 2);
				
		algorithm.applyConfiguration(properties);
				
		algorithm.run(10000);
		algorithm.getResult().display();
	}

}

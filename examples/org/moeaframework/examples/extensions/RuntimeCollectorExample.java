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
package org.moeaframework.examples.extensions;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.algorithm.extension.FrequencyType;
import org.moeaframework.algorithm.extension.RuntimeCollectorExtension;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.Problem;
import org.moeaframework.problem.misc.Srinivas;

/**
 * Demonstrates using the extension to collect runtime data.
 */
public class RuntimeCollectorExample {
	
	public static void main(String[] args) throws IOException {
		Problem problem = new Srinivas();
		File file = new File("runtime.dat");
		
		try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, file)) {
			NSGAII algorithm = new NSGAII(problem);
			algorithm.addExtension(new RuntimeCollectorExtension(writer, 1000, FrequencyType.EVALUATIONS));
			algorithm.run(100000);
		}
	}

}

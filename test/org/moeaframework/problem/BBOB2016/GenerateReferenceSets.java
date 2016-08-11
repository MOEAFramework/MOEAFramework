/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.problem.BBOB2016;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.moeaframework.Analyzer;
import org.moeaframework.Executor;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.PopulationIO;

public class GenerateReferenceSets {
	
	public static void main(String[] args) throws IOException {
		// capture the output to collect all BBOB2016-Biobj problem names
		PrintStream oldOutput = System.out;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		System.setOut(new PrintStream(baos));
		
		CocoProblemWrapper.printProblemNames();
		
		System.out.close();
		System.setOut(oldOutput);
		
		// parse the output and test each problem instance
		BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(baos.toByteArray())));
		String problemName = null;
		
		while ((problemName = reader.readLine()) != null) {
			System.out.println(problemName);
			
			Executor executor = new Executor()
					.withProblem(problemName)
					.withMaxEvaluations(100000);
			
			Analyzer analyzer = new Analyzer()
					.withEpsilon(0.1)
					.withSameProblemAs(executor);
			
			for (String algorithm : new String[] { "NSGAII", "GDE3", "OMOPSO" }) {
				analyzer.addAll(algorithm, executor.withAlgorithm(algorithm).runSeeds(10));
			}
				
			NondominatedPopulation referenceSet = analyzer.getReferenceSet();
			
			PopulationIO.writeObjectives(new File(problemName + ".pf"), referenceSet);
		}
	}

}

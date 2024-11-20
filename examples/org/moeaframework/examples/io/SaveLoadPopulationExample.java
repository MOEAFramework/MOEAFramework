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
package org.moeaframework.examples.io;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.core.population.Population;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;
import org.moeaframework.util.format.TableFormat;

/**
 * Demonstrates the various ways to save, load, and output populations.
 */
public class SaveLoadPopulationExample {

	public static void main(String[] args) throws IOException {
		Problem problem = new DTLZ2(2);
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		// Save population to various file formats
		algorithm.getResult().save(TableFormat.CSV, new File("NSGAII_DTLZ2.csv"));
		algorithm.getResult().save(TableFormat.Markdown, new File("NSGAII_DTLZ2.md"));
		algorithm.getResult().save(TableFormat.Latex, new File("NSGAII_DTLZ2.tex"));
		algorithm.getResult().save(TableFormat.Json, new File("NSGAII_DTLZ2.json"));
		algorithm.getResult().save(TableFormat.ARFF, new File("NSGAII_DTLZ2.arff"));
		
		// Save the population to a result file
		algorithm.getResult().save(new File("NSGAII_DTLZ2.res"));
		
		// Load the population from the result file
		Population result = Population.load(new File("NSGAII_DTLZ2.res"));
		result.display();
	}

}

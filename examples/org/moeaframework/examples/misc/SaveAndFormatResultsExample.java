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
package org.moeaframework.examples.misc;

import java.io.File;
import java.io.IOException;

import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.problem.misc.Srinivas;
import org.moeaframework.util.format.NumberFormatter;
import org.moeaframework.util.format.TableFormat;

/**
 * Demonstrates saving results to a CSV file and altering formatting.
 */
public class SaveAndFormatResultsExample {
	
	public static void main(String[] args) throws IOException {
		NSGAII algorithm = new NSGAII(new Srinivas());
		algorithm.run(10000);
		
		// Display results to console
		algorithm.getResult().display();

		// Save results to various file formats
		algorithm.getResult().save(TableFormat.CSV, new File("solutions.csv"));
		algorithm.getResult().save(TableFormat.Markdown, new File("solutions.md"));
		algorithm.getResult().save(TableFormat.Latex, new File("solutions.tex"));
		algorithm.getResult().save(TableFormat.Json, new File("solution.json"));

		// Change formatting of output
		NumberFormatter.getDefault().setPrecision(10);
		algorithm.getResult().display();
	}

}

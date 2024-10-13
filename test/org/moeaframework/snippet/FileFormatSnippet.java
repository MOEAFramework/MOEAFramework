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
package org.moeaframework.snippet;

import java.io.IOException;

import org.junit.Test;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.io.MetricFileReader;
import org.moeaframework.analysis.io.MetricFileWriter;
import org.moeaframework.analysis.io.ResultEntry;
import org.moeaframework.analysis.io.ResultFileReader;
import org.moeaframework.analysis.io.ResultFileWriter;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.population.NondominatedPopulation;
import org.moeaframework.core.population.Population;
import org.moeaframework.mock.MockSolution;
import org.moeaframework.problem.misc.Schaffer;
import org.moeaframework.util.TypedProperties;

import static org.moeaframework.TempFiles.File;

@SuppressWarnings("unused")
public class FileFormatSnippet {
	
	@Test
	public void objectives() throws IOException {
		Population population = new Population();
		population.add(MockSolution.of().withObjectives(1.0, 1.0).build());
		
		// begin-example:objectives
		population.saveObjectives(new File("population.dat"));
		Population.loadObjectives(new File("population.dat"));
		// end-example:objectives
	}
	
	@Test
	public void referenceSet() throws IOException {
		// begin-example:referenceSet
		NondominatedPopulation referenceSet = NondominatedPopulation.loadReferenceSet("pf/DTLZ2.2D.pf");
		// end-example:referenceSet
	}

	@Test
	public void binary() throws IOException {
		Population population = new Population();
		population.add(MockSolution.of().withObjectives(1.0, 1.0).build());
		
		// begin-example:binary
		population.saveBinary(new File("population.bin"));
		Population.loadBinary(new File("population.bin"));
		// end-example:binary
	}
	
	@Test
	public void resultFile() throws IOException {
		Problem problem = new Schaffer();
		NSGAII algorithm = new NSGAII(problem);
		
		// begin-example:resultFile-overwrite
		try (ResultFileWriter writer = ResultFileWriter.overwrite(problem, new File("result.dat"))) {
			for (int i = 0; i < 1000; i++) {
				algorithm.step();
				
				TypedProperties properties = new TypedProperties();
				properties.setInt("NFE", algorithm.getNumberOfEvaluations());
				
				writer.append(new ResultEntry(algorithm.getResult(), properties));
			}
		}
		// end-example:resultFile-overwrite
		
		// begin-example:resultFile-append
		try (ResultFileWriter writer = ResultFileWriter.append(problem, new File("result.dat"))) {
			int existingEntries = writer.getNumberOfEntries();
			
			// if existingEntries > 0, we are appending to an existing file
		}
		// end-example:resultFile-append
		
		// begin-example:resultFile-open
		try (ResultFileReader reader = ResultFileReader.open(problem, new File("result.dat"))) {
			while (reader.hasNext()) {
				ResultEntry entry = reader.next();
				
				TypedProperties metadata = entry.getProperties();
				NondominatedPopulation set = entry.getPopulation();
			}
		}
		// end-example:resultFile-open
	}
	
	@Test
	public void metricFile() throws IOException {
		Problem problem = new Schaffer();
		NSGAII algorithm = new NSGAII(problem);
		Indicators indicators = Indicators.standard(problem, NondominatedPopulation.loadReferenceSet("pf/Schaffer.pf"));
		
		try (MetricFileWriter writer = MetricFileWriter.overwrite(indicators, new File("metrics.dat"))) {
			for (int i = 0; i < 1000; i++) {
				algorithm.step();
				writer.append(new ResultEntry(algorithm.getResult()));
			}
		}
		
		try (MetricFileWriter writer = MetricFileWriter.append(indicators, new File("metrics.dat"))) {
			int existingEntries = writer.getNumberOfEntries();
			
			// if existingEntries > 0, we are appending to an existing file
		}
		
		try (MetricFileReader reader = MetricFileReader.open(new File("metrics.dat"))) {
			while (reader.hasNext()) {
				double[] metrics = reader.next();
			}
		}
	}
	
}

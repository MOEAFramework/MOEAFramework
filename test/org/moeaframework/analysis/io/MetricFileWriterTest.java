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
package org.moeaframework.analysis.io;

import java.io.File;
import java.io.IOException;

import org.apache.commons.cli.ParseException;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.io.MetricFileWriter.MetricFileWriterSettings;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.indicator.Indicators;
import org.moeaframework.core.spi.ProblemFactory;
import org.moeaframework.mock.MockSolution;

public class MetricFileWriterTest {

	@Test
	public void testAppend() throws IOException {
		File file = TempFiles.createFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		Indicators indicators = Indicators.standard(problem, referenceSet);

		NondominatedPopulation approximationSet = new NondominatedPopulation();
		approximationSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		approximationSet.add(MockSolution.of().withObjectives(1.0, 0.0));

		try (MetricFileWriter writer = MetricFileWriter.append(indicators, file)) {
			Assert.assertEquals(0, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));
			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(2, writer.getNumberOfEntries());
		}

		try (MetricFileWriter writer = MetricFileWriter.append(indicators, file)) {
			Assert.assertEquals(2, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(3, writer.getNumberOfEntries());
		}

		try (MetricFileReader reader = new MetricFileReader(file)) {
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testOverwrite() throws IOException {
		File file = TempFiles.createFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		Indicators indicators = Indicators.standard(problem, referenceSet);

		NondominatedPopulation approximationSet = new NondominatedPopulation();
		approximationSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		approximationSet.add(MockSolution.of().withObjectives(1.0, 0.0));

		try (MetricFileWriter writer = MetricFileWriter.overwrite(indicators, file)) {
			Assert.assertEquals(0, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));
			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(2, writer.getNumberOfEntries());
		}

		try (MetricFileWriter writer = MetricFileWriter.overwrite(indicators, file)) {
			Assert.assertEquals(0, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(1, writer.getNumberOfEntries());
		}

		try (MetricFileReader reader = new MetricFileReader(file)) {
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testSettings() throws ParseException {
		MetricFileWriterSettings settings = MetricFileWriterSettings.getDefault();
		Assert.assertTrue(settings.isAppend());
		
		settings = MetricFileWriterSettings.overwrite();
		Assert.assertFalse(settings.isAppend());
	}
	
	@Test
	public void testFileTimestamp() throws IOException, InterruptedException {
		File file = TempFiles.createFile();
		
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance().getReferenceSet("DTLZ2_2");
		Indicators indicators = Indicators.standard(problem, referenceSet);

		NondominatedPopulation approximationSet = new NondominatedPopulation();
		approximationSet.add(MockSolution.of().withObjectives(0.0, 1.0));
		approximationSet.add(MockSolution.of().withObjectives(1.0, 0.0));

		try (MetricFileWriter writer = MetricFileWriter.append(indicators, file)) {
			Assert.assertEquals(0, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));
			writer.append(new ResultEntry(approximationSet));
		}
		
		long originalTimestamp = file.lastModified();
		
		Thread.sleep(100);
		
		try (MetricFileWriter writer = MetricFileWriter.append(indicators, file)) {
			Assert.assertEquals(2, writer.getNumberOfEntries());
		}
		
		Assert.assertEquals(originalTimestamp, file.lastModified());
		
		Thread.sleep(100);
		
		try (MetricFileWriter writer = MetricFileWriter.append(indicators, file)) {
			Assert.assertEquals(2, writer.getNumberOfEntries());
			writer.append(new ResultEntry(approximationSet));
		}
		
		Assert.assertNotEquals(originalTimestamp, file.lastModified());
	}

}

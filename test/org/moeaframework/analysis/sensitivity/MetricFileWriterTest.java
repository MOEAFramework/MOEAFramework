/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.analysis.sensitivity;

import java.io.File;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.QualityIndicator;
import org.moeaframework.core.spi.ProblemFactory;

/**
 * Tests the {@link MetricFileWriter} class.
 */
public class MetricFileWriterTest {

	/**
	 * Tests normal writing operations.
	 * 
	 * @throws IOException if an I/O error occurred
	 */
	@Test
	public void test() throws IOException {
		File file = TestUtils.createTempFile();
		Problem problem = ProblemFactory.getInstance().getProblem("DTLZ2_2");
		NondominatedPopulation referenceSet = ProblemFactory.getInstance()
				.getReferenceSet("DTLZ2_2");
		QualityIndicator qualityIndicator = new QualityIndicator(problem,
				referenceSet);
		MetricFileWriter writer = null;

		NondominatedPopulation approximationSet = new NondominatedPopulation();
		approximationSet.add(new Solution(new double[] { 0.0, 1.0 }));
		approximationSet.add(new Solution(new double[] { 1.0, 0.0 }));

		try {
			writer = new MetricFileWriter(qualityIndicator, file);

			Assert.assertEquals(0, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));
			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(2, writer.getNumberOfEntries());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		try {
			writer = new MetricFileWriter(qualityIndicator, file);

			Assert.assertEquals(2, writer.getNumberOfEntries());

			writer.append(new ResultEntry(approximationSet));

			Assert.assertEquals(3, writer.getNumberOfEntries());
		} finally {
			if (writer != null) {
				writer.close();
			}
		}

		MetricFileReader reader = null;

		try {
			reader = new MetricFileReader(file);
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertTrue(reader.hasNext());
			reader.next();
			Assert.assertFalse(reader.hasNext());
		} finally {
			if (reader != null) {
				reader.close();
			}
		}

	}

}

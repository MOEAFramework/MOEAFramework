/* Copyright 2009-2025 David Hadka
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
package org.moeaframework.analysis;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TempFiles;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.parameter.Enumeration;
import org.moeaframework.analysis.parameter.Parameter;
import org.moeaframework.analysis.parameter.ParameterSet;
import org.moeaframework.analysis.sample.Samples;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.core.PRNG;
import org.moeaframework.problem.DTLZ.DTLZ2;
import org.moeaframework.problem.Problem;

public class ConcurrencyTest {
	
	private static final int SEEDS = 16; //Runtime.getRuntime().availableProcessors();
	
	private static final int TRIALS = 5;
	
	@Test
	public void test() throws IOException, InterruptedException, ExecutionException {
		Problem problem = new DTLZ2(2);

		Enumeration<Long> seed = Parameter.named("seed").asLong().rangeExclusive(0, SEEDS);

		ParameterSet parameters = new ParameterSet(seed);
		Samples samples = parameters.enumerate();

		File tempDirectory = TempFiles.createDirectory();
		DataStore dataStore = new FileSystemDataStore(tempDirectory);

		for (int i = 0; i < TRIALS; i++) {
			samples.distributeAll(sample -> {			
				Reference reference = sample.getReference();
				Container container = dataStore.getContainer(reference);
				Blob blob = container.getBlob("result");
				
				PRNG.setSeed(sample.getLong("seed"));
				
				NSGAII algorithm = new NSGAII(problem);
				algorithm.applyConfiguration(sample);
				algorithm.run(10000);
				
				try (StringWriter writer = new StringWriter()) {
					algorithm.getResult().save(writer);
					
					if (blob.exists()) {
						Assert.assertEquals(writer.toString(), blob.extractText());
					} else {
						blob.storeText(writer.toString());
					}
				} catch (IOException e) {
					throw new UncheckedIOException(e);
				}
			});
		}
	}

}

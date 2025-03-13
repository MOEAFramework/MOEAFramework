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
import org.moeaframework.problem.Problem;
import org.moeaframework.problem.DTLZ.DTLZ2;

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

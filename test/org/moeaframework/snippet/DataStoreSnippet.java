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
package org.moeaframework.snippet;

import java.io.IOException;
import java.net.URI;

import org.junit.Test;
import org.moeaframework.TempFiles.File;
import org.moeaframework.algorithm.NSGAII;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.core.PRNG;
import org.moeaframework.problem.CEC2009.UF1;
import org.moeaframework.problem.Problem;

public class DataStoreSnippet {

	@Test
	public void basicSnippet() throws IOException {
		// begin-example: datastore-create
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		// end-example: datastore-create
		
		// begin-example: datastore-container
		Reference reference = Reference.of("populationSize", 100);
		Container container = dataStore.getContainer(reference);
		// end-example: datastore-container
		
		// begin-example: datastore-blob
		Blob blob = container.getBlob("greeting");
		blob.storeText("Hello world!");
		
		System.out.println(blob.extractText());
		// end-example: datastore-blob
	}
	
	@Test
	public void layoutSnippet() throws IOException {
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		
		// begin-example: datastore-layout
		Reference reference = Reference.of("populationSize", 200);
		Container container = dataStore.getContainer(reference);
		// end-example: datastore-layout
		
		Blob blob = container.getBlob("greeting");
		blob.storeText("Hello world from the second container!");
	}
	
	@Test
	public void algorithmSnippet() throws IOException {
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		
		// begin-example: datastore-algorithm
		Problem problem = new UF1();
		
		NSGAII algorithm = new NSGAII(problem);
		algorithm.run(10000);
		
		Reference reference = Reference.of(algorithm.getConfiguration());
		Container container = dataStore.getContainer(reference);
		
		Blob blob = container.getBlob("result");
		blob.storePopulation(algorithm.getResult());
		// end-example: datastore-algorithm
		
		// begin-example: datastore-exists
		if (!blob.exists()) {
			algorithm.run(10000);
			blob.storePopulation(algorithm.getResult());
		}
		// end-example: datastore-exists
	}
	
	@Test
	public void seedsSnippet() throws IOException {
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		Problem problem = new UF1();
		
		// begin-example: datastore-seeds
		for (int seed = 0; seed < 10; seed++) {
			PRNG.setSeed(seed);
			
			NSGAII algorithm = new NSGAII(problem);
			
			Reference reference = Reference.of(algorithm.getConfiguration()).with("seed", seed);
			Container container = dataStore.getContainer(reference);
			Blob blob = container.getBlob("result");
			
			if (!blob.exists()) {
				algorithm.run(10000);
				blob.storePopulation(algorithm.getResult());
			}
		}
		// end-example: datastore-seeds
	}
	
	@Test
	public void uriSnippet() throws IOException {
		DataStore dataStore = new FileSystemDataStore(new File("results"));
		
		Reference reference = Reference.of("populationSize", 200);
		Container container = dataStore.getContainer(reference);
		Blob blob = container.getBlob("greeting");
		
		// begin-example: datastore-geturi
		URI uri = blob.getURI();
		// end-example: datastore-geturi
		
		// begin-example: datastore-resolveuri
		DataStoreFactory.getInstance().resolveBlob(uri);
		// end-example: datastore-resolveuri
	}
	
}

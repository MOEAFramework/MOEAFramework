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
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.DataStoreFactory;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;
import org.moeaframework.analysis.store.schema.Field;
import org.moeaframework.analysis.store.schema.Schema;
import org.moeaframework.core.PropertyNotFoundException;

public class DataStoreSnippet {

	@Test
	public void basicSnippet() throws IOException {
		// begin-example: datastore-create
		DataStore dataStore = new FileSystemDataStore(new File("sample"));
		// end-example: datastore-create
		
		// begin-example: datastore-container
		Reference reference = Reference.of("populationSize", 100);
		Container container = dataStore.getContainer(reference);
		// end-example: datastore-container
		
		// begin-example: datastore-container
		Blob blob = container.getBlob("hello");
		blob.storeText("Hello world!");
		
		System.out.println(blob.extractText());
		// end-example: datastore-container
		
		// begin-example: datastore-uri
		URI uri = blob.getURI();
		DataStoreFactory.getInstance().resolveBlob(uri);
		// end-example: datastore-uri
	}
	
	@Test(expected = PropertyNotFoundException.class)
	public void schemaSnippet() throws IOException {
		// begin-example: datastore-schema
		Schema schema = Schema.of(
				Field.named("populationSize").asInt());
		
		DataStore dataStore = new FileSystemDataStore(new File("sample-schema"), schema);
		
		Reference reference = Reference.of("sbx.rate", 1.0);
		Container container = dataStore.getContainer(reference);
		
		Blob blob = container.getBlob("hello");
		blob.storeText("Hello world!");
		// end-example: datastore-schema
	}
	
}

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

import java.io.File;

import org.junit.Test;
import org.moeaframework.analysis.store.Blob;
import org.moeaframework.analysis.store.Container;
import org.moeaframework.analysis.store.DataStore;
import org.moeaframework.analysis.store.Reference;
import org.moeaframework.analysis.store.fs.FileSystemDataStore;

public class DataStoreSnippet {

	@Test
	public void snippet() {
		// begin-example: datastore
		DataStore dataStore = new FileSystemDataStore(new File("result"));
		
		Reference reference = Reference.of("case", "Test1");
		Container container = dataStore.getContainer(reference);
		
		Blob blob = container.getBlob("hello");
		blob.storeText("Hello world!");
		
		System.out.println(blob.extractText());
		
		
		// end-example: datastore
	}
	
}

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
package org.moeaframework.analysis.tools;

import java.io.File;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.Capture;
import org.moeaframework.Capture.CaptureResult;
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.BlobNotFoundException;

public class DataStoreToolTest {
	
	@Test
	public void test() throws Exception {
		File tempDirectory = TempFiles.createDirectory();
		File outputFile = TempFiles.createFile();
		File inputFile = TempFiles.createFile().withContent("foo");
		
		CaptureResult result = Capture.output(DataStoreTool.class, "list", tempDirectory.toURI().toString());
		result.assertSuccessful();
		Assert.assertLineCount(0, result.toString());
		
		result = Capture.output(DataStoreTool.class, "set", "--input", inputFile.toString(),
				tempDirectory.toURI().toString() + "?a=b#foo");
		result.assertSuccessful();
		
		result = Capture.output(DataStoreTool.class, "get", "--output", outputFile.toString(),
				tempDirectory.toURI().toString() + "?a=b#foo");
		result.assertSuccessful();
		Assert.assertFileWithContent("foo", outputFile);
		
		result = Capture.output(DataStoreTool.class, "list", tempDirectory.toURI().toString() + "?a=b");
		result.assertSuccessful();
		Assert.assertLineCount(1, result.toString());
		
		result = Capture.output(DataStoreTool.class, "delete", "--yes", tempDirectory.toURI().toString() + "?a=b#foo");
		result.assertSuccessful();
		
		result = Capture.output(DataStoreTool.class, "get", tempDirectory.toURI().toString() + "?a=b#foo");
		result.assertThrows(BlobNotFoundException.class);
		
		result = Capture.output(DataStoreTool.class, "type", tempDirectory.toURI().toString());
		result.assertSuccessful();
		result.assertContains("datastore");
		
		result = Capture.output(DataStoreTool.class, "type", tempDirectory.toURI().toString() + "?a=b");
		result.assertSuccessful();
		result.assertContains("container");
		
		result = Capture.output(DataStoreTool.class, "type", tempDirectory.toURI().toString() + "?a=b#foo");
		result.assertSuccessful();
		result.assertContains("blob");
	}

}

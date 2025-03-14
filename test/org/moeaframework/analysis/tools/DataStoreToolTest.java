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
import org.moeaframework.TempFiles;
import org.moeaframework.analysis.store.BlobNotFoundException;

public class DataStoreToolTest {
	
	@Test
	public void test() throws Exception {
		File tempDirectory = TempFiles.createDirectory();
		File outputFile = TempFiles.createFile();
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString(),
				"--list",
				"--output", outputFile.toString() });
		
		Assert.assertLineCount(0, outputFile);
		
		File inputFile = TempFiles.createFile().withContent("foo");
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b#foo",
				"--set",
				"--input", inputFile.toString() });
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b#foo",
				"--get",
				"--output", outputFile.toString() });
		
		Assert.assertFileWithContent("foo", outputFile);
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b",
				"--list",
				"--output", outputFile.toString() });
		
		Assert.assertLineCount(1, outputFile);
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b#foo",
				"--delete",
				"--yes" });
		
		Assert.assertThrows(BlobNotFoundException.class, () -> DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b#foo",
				"--get",
				"--output", outputFile.toString() }));
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString(),
				"--type",
				"--output", outputFile.toString() });
		
		Assert.assertFileWithContent("datastore" + System.lineSeparator(), outputFile);
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b",
				"--type",
				"--output", outputFile.toString() });
		
		Assert.assertFileWithContent("container" + System.lineSeparator(), outputFile);
		
		DataStoreTool.main(new String[] {
				"--uri", tempDirectory.toURI().toString() + "?a=b#foo",
				"--type",
				"--output", outputFile.toString() });
		
		Assert.assertFileWithContent("blob" + System.lineSeparator(), outputFile);
	}

}

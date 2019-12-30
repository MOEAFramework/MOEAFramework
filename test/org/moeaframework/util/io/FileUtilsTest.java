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
package org.moeaframework.util.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;

public class FileUtilsTest {
	
	@Test
	public void testMove() throws IOException {
		File from = TestUtils.createTempFile("foobar");
		File to = TestUtils.createTempFile();
		
		FileUtils.move(from, to);
		
		Assert.assertFalse(from.exists());
		Assert.assertTrue(to.exists());
		Assert.assertEquals(1, TestUtils.lineCount(to));
		TestUtils.assertLinePattern(to, "foobar");
	}
	
	@Test
	public void testMoveSame() throws IOException {
		File file = TestUtils.createTempFile("foobar");
		
		FileUtils.move(file, file);
		
		Assert.assertTrue(file.exists());
		Assert.assertEquals(1, TestUtils.lineCount(file));
		TestUtils.assertLinePattern(file, "foobar");
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testMoveNonexistentFile() throws IOException {
		File from = TestUtils.createTempFile("foobar");
		File to = TestUtils.createTempFile();
		
		from.delete();
		
		FileUtils.move(from, to);	
	}
	
	@Test
	public void testDelete() throws IOException {
		File file = TestUtils.createTempFile();
		
		FileUtils.delete(file);
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void testDeleteNonexistentFile() throws IOException {
		File file = TestUtils.createTempFile();
		file.delete();
		
		FileUtils.delete(file);
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void testMkdir() throws IOException {
		FileUtils.mkdir(getTempFolder());
	}
	
	@Test
	public void testMkdirAlreadyExists() throws IOException {
		File directory = getTempFolder();
		
		FileUtils.mkdir(directory);
		FileUtils.mkdir(directory);
	}
	
	@Test(expected = IOException.class)
	public void testMkdirButFileExistsWithName() throws IOException {
		File file = TestUtils.createTempFile();
		
		FileUtils.mkdir(file);
	}
	
	private File getTempFolder() throws IOException {
		File file = File.createTempFile("test", null);
		file.delete();
		
		File directory = new File(file.getParent(), file.getName());
		directory.deleteOnExit();
		
		return directory;
	}

}

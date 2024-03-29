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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;

public class OutputWriterTest {

	@Test
	public void testReplaceDestinationNotExists() throws IOException {
		File source = TestUtils.createTempFile("foo");
		
		File destination = TestUtils.createTempFile();
		destination.delete();
		
		Assert.assertTrue(OutputWriter.replace(source, destination));
		
		Assert.assertFalse(source.exists());
		Assert.assertTrue(destination.exists());
		Assert.assertEquals("foo", TestUtils.loadText(destination));
	}
	
	@Test
	public void testReplaceDestinationIsIdentical() throws IOException {
		File source = TestUtils.createTempFile("foo");
		File destination = TestUtils.createTempFile("foo");
		long lastModified = destination.lastModified();
		
		Assert.assertFalse(OutputWriter.replace(source, destination));
		
		Assert.assertFalse(source.exists());
		Assert.assertTrue(destination.exists());
		Assert.assertEquals("foo", TestUtils.loadText(destination));
		Assert.assertEquals(lastModified, destination.lastModified());
	}
	
	@Test
	public void testReplaceDestinationIsDifferent() throws IOException {
		File source = TestUtils.createTempFile("foo");
		File destination = TestUtils.createTempFile("bar");
		
		Assert.assertTrue(OutputWriter.replace(source, destination));
		
		Assert.assertFalse(source.exists());
		Assert.assertTrue(destination.exists());
		Assert.assertEquals("foo", TestUtils.loadText(destination));
	}
	
	@Test(expected = IOException.class)
	public void testReplaceSourceNotFound() throws IOException {
		File source = TestUtils.createTempFile();
		File destination = TestUtils.createTempFile("bar");
		source.delete();
		
		OutputWriter.replace(source, destination);
	}

}

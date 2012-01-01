/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.RealVariable;

/**
 * Tests the {@link CoreUtils} class.
 */
public class CoreUtilsTest {
	
	@Test
	public void testAssertNotNullWithObject() {
		CoreUtils.assertNotNull("test", 0.0);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testAssertNotNullWithNull() {
		CoreUtils.assertNotNull("test", null);
	}
	
	@Test
	public void testCastVariablesToDoubleArray() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.25, 0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.75, 0.0, 1.0));
		Assert.assertArrayEquals(new double[] { 0.25, 0.75 }, 
				CoreUtils.castVariablesToDoubleArray(solution), Settings.EPS);
	}
	
	@Test
	public void testCastVariablesToDoubleArrayEmpty() {
		Solution solution = new Solution(0, 0);
		Assert.assertArrayEquals(new double[] { }, 
				CoreUtils.castVariablesToDoubleArray(solution), Settings.EPS);
	}
	
	@Test(expected = ClassCastException.class)
	public void testCastVariablesToDoubleInvalidType() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.25, 0.0, 1.0));
		solution.setVariable(1, new BinaryVariable(3));
		CoreUtils.castVariablesToDoubleArray(solution);
	}
	
	@Test
	public void testFillVariablesFromDoubleArray() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new RealVariable(0.0, 1.0));
		CoreUtils.fillVariablesFromDoubleArray(solution, 
				new double[] { 0.25, 0.75 });
		
		Assert.assertEquals(0.25, ((RealVariable)solution.getVariable(0))
				.getValue(), Settings.EPS);
		Assert.assertEquals(0.75, ((RealVariable)solution.getVariable(1))
				.getValue(), Settings.EPS);
	}
	
	@Test
	public void testFillVariablesFromDoubleArrayEmpty() {
		Solution solution = new Solution(0, 0);
		CoreUtils.fillVariablesFromDoubleArray(solution, 
				new double[] {  });
		Assert.assertEquals(0, solution.getNumberOfVariables());
	}
	
	@Test(expected = ClassCastException.class)
	public void testFillVariablesFromDoubleArrayInvalidType() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		solution.setVariable(1, new BinaryVariable(3));
		CoreUtils.fillVariablesFromDoubleArray(solution, 
				new double[] { 0.25, 0.75 });
	}
	
	@Test(expected = NullPointerException.class)
	public void testFillVariablesFromDoubleArrayNullPointer() {
		Solution solution = new Solution(2, 0);
		solution.setVariable(0, new RealVariable(0.0, 1.0));
		CoreUtils.fillVariablesFromDoubleArray(solution, 
				new double[] { 0.25, 0.75 });
	}
	
	@Test
	public void testMerge1() {
		Solution s1 = new Solution(0, 0);
		Solution s2 = new Solution(0, 0);
		Solution s3 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1, s2, s3 },
				CoreUtils.merge(s1, new Solution[] { s2, s3 }));
	}
	
	@Test
	public void testMerge1Empty() {
		Solution s1 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1 },
				CoreUtils.merge(s1, new Solution[] { }));
	}
	
	@Test(expected = NullPointerException.class)
	public void testMerge1Null() {
		Solution s1 = new Solution(0, 0);
		CoreUtils.merge(s1, null);
	}
	
	@Test
	public void testMerge2() {
		Solution s1 = new Solution(0, 0);
		Solution s2 = new Solution(0, 0);
		Solution s3 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1, s2, s3 },
				CoreUtils.merge(new Solution[] { s1 }, 
						new Solution[] { s2, s3 }));
	}
	
	@Test
	public void testMerge2Empty1() {
		Solution s1 = new Solution(0, 0);
		
		Assert.assertArrayEquals(new Solution[] { s1 },
				CoreUtils.merge(new Solution[] { }, new Solution[] { s1 }));
	}
	
	@Test
	public void testMerge2Empty2() {
		Assert.assertArrayEquals(new Solution[] { },
				CoreUtils.merge(new Solution[] { }, new Solution[] { }));
	}
	
	@Test(expected = NullPointerException.class)
	public void testMerge2Null() {
		Solution s1 = new Solution(0, 0);
		CoreUtils.merge((Solution[])null, new Solution[] { s1 });
	}
	
	@Test
	public void testMove() throws IOException {
		File from = TestUtils.createTempFile("foobar");
		File to = TestUtils.createTempFile();
		
		CoreUtils.move(from, to);
		
		Assert.assertFalse(from.exists());
		Assert.assertTrue(to.exists());
		Assert.assertEquals(1, TestUtils.lineCount(to));
		TestUtils.assertLinePattern(to, "foobar");
	}
	
	@Test
	public void testMoveSame() throws IOException {
		File file = TestUtils.createTempFile("foobar");
		
		CoreUtils.move(file, file);
		
		Assert.assertTrue(file.exists());
		Assert.assertEquals(1, TestUtils.lineCount(file));
		TestUtils.assertLinePattern(file, "foobar");
	}
	
	@Test(expected = FileNotFoundException.class)
	public void testMoveNonexistentFile() throws IOException {
		File from = TestUtils.createTempFile("foobar");
		File to = TestUtils.createTempFile();
		
		from.delete();
		
		CoreUtils.move(from, to);	
	}
	
	@Test
	public void testDelete() throws IOException {
		File file = TestUtils.createTempFile();
		
		CoreUtils.delete(file);
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void testDeleteNonexistentFile() throws IOException {
		File file = TestUtils.createTempFile();
		file.delete();
		
		CoreUtils.delete(file);
		
		Assert.assertFalse(file.exists());
	}
	
	@Test
	public void testMkdir() throws IOException {
		CoreUtils.mkdir(getTempFolder());
	}
	
	@Test
	public void testMkdirAlreadyExists() throws IOException {
		File directory = getTempFolder();
		
		CoreUtils.mkdir(directory);
		CoreUtils.mkdir(directory);
	}
	
	@Test(expected = IOException.class)
	public void testMkdirButFileExistsWithName() throws IOException {
		File file = TestUtils.createTempFile();
		
		CoreUtils.mkdir(file);
	}
	
	private File getTempFolder() throws IOException {
		File file = File.createTempFile("test", null);
		file.delete();
		
		File directory = new File(file.getParent(), file.getName());
		directory.deleteOnExit();
		
		return directory;
	}

}

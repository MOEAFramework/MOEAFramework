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

import java.io.IOException;
import java.io.StringReader;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.Settings;

public class MatrixReaderTest {

	public static final String FIXED = """
			0.0 0.1 -0.1
			# commented line
			0 10 100
			""";

	public static final String FIXED_WHITESPACE = """
			  0.0 0.1    -0.1  
			# commented line
			\t\t0 \t 10 100\t\t
			""";

	public static final String VARIABLE = """
			0.0 0.1 -0.1
			-0.1 -0.2
			0 10 100
			""";

	public static final String UNPARSEABLE = """
			0.0 0.1 -0.1
			0.0 0.1 -0.1foo
			0 10 100
			""";
	
	@Test
	public void testFixed1() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED))) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testFixed2() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED), 3)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testFixed3() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED_WHITESPACE), 3)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testFixed4() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED), 2)) {
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testReadAll() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED))) {
			double[][] data = reader.readAll();
			
			Assert.assertFalse(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, data[0], Settings.EPS);
			Assert.assertArrayEquals(new double[] { 0, 10, 100 }, data[1], Settings.EPS);
		}
	}
	
	@Test
	public void testVariable1() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(VARIABLE))) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { -0.1, -0.2 }, reader.next(), Settings.EPS);
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testVariable2() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(VARIABLE), 3)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testVariable3() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(VARIABLE), 2)) {
			Assert.assertFalse(reader.hasNext());
		}
	}

	@Test(expected = FrameworkException.class)
	public void testUnparseable1() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(UNPARSEABLE))) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test(expected = FrameworkException.class)
	public void testUnparseable2() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(UNPARSEABLE), 3)) {
			Assert.assertTrue(reader.hasNext());
			Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(), Settings.EPS);
			Assert.assertFalse(reader.hasNext());
		}
	}
	
	@Test
	public void testSuppressExceptions() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(UNPARSEABLE))) {
			reader.setSuppressExceptions(true);
			
			reader.next();
			Assert.assertFalse(reader.hasNext());
			Assert.assertTrue(reader.isError());
		}
	}
	
	@Test(expected = NoSuchElementException.class)
	public void testNextAfterEndOfFile() throws IOException {
		try (MatrixReader reader = new MatrixReader(new StringReader(FIXED))) {
			reader.next();
			reader.next();
			Assert.assertFalse(reader.hasNext());
			reader.next();
		}
	}

}

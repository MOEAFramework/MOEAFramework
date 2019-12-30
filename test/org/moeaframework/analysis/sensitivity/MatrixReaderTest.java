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
package org.moeaframework.analysis.sensitivity;

import java.io.StringReader;

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.core.Settings;

/**
 * Tests the {@link MatrixReader} class.
 */
public class MatrixReaderTest {

	public static final String FIXED =
			"0.0 0.1 -0.1\n" +
			"# commented line\n" +
			"0 10 100\n";
	
	public static final String FIXED_WHITESPACE =
			"  0.0 0.1    -0.1  \n" +
			"# commented line\n" +
			"\t\t0 \t 10 100\t\t\n";

	public static final String VARIABLE =
			"0.0 0.1 -0.1\n" +
			"-0.1 -0.2\n" +
			"0 10 100\n";

	public static final String UNPARSEABLE =
			"0.0 0.1 -0.1\n" +
			"0.0 0.1 -0.1foo\n" + // unparseable data
			"0 10 100\n";
	
	@Test
	public void testFixed1() {
		MatrixReader reader = new MatrixReader(new StringReader(FIXED));
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), 
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test
	public void testFixed2() {
		MatrixReader reader = new MatrixReader(new StringReader(FIXED), 3);
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), 
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test
	public void testFixed3() {
		MatrixReader reader = new MatrixReader(new StringReader(
				FIXED_WHITESPACE), 3);
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), 
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test(expected = Exception.class)
	public void testFixed4() {
		MatrixReader reader = new MatrixReader(new StringReader(FIXED), 2);
		
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test
	public void testVariable1() {
		MatrixReader reader = new MatrixReader(new StringReader(VARIABLE));
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { -0.1, -0.2 }, reader.next(), 
				Settings.EPS);
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0, 10, 100 }, reader.next(), 
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test(expected = Exception.class)
	public void testVariable2() {
		MatrixReader reader = new MatrixReader(new StringReader(VARIABLE), 3);
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test(expected = Exception.class)
	public void testVariable3() {
		MatrixReader reader = new MatrixReader(new StringReader(VARIABLE), 2);
		
		Assert.assertFalse(reader.hasNext());
	}

	@Test(expected = Exception.class)
	public void testUnparseable1() {
		MatrixReader reader = new MatrixReader(new StringReader(UNPARSEABLE));
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}
	
	@Test(expected = Exception.class)
	public void testUnparseable2() {
		MatrixReader reader = new MatrixReader(new StringReader(UNPARSEABLE), 
				3);
		
		Assert.assertTrue(reader.hasNext());
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, reader.next(),
				Settings.EPS);
		Assert.assertFalse(reader.hasNext());
	}

}

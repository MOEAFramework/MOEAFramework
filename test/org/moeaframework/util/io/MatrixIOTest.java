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
package org.moeaframework.util.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.TestThresholds;

public class MatrixIOTest {
		
	@Test
	public void testLoad() throws IOException {
		double[][] data = MatrixIO.load(new StringReader(MatrixReaderTest.FIXED));
		
		Assert.assertNotNull(data);
		Assert.assertEquals(2, data.length);
		Assert.assertArrayEquals(new double[] { 0.0, 0.1, -0.1 }, data[0], TestThresholds.HIGH_PRECISION);
		Assert.assertArrayEquals(new double[] { 0, 10, 100 }, data[1], TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testLoadColumn() throws IOException {
		double[] data = MatrixIO.loadColumn(new StringReader(MatrixReaderTest.FIXED), 2);
			
		Assert.assertNotNull(data);
		Assert.assertEquals(2, data.length);
		Assert.assertEquals(-0.1, data[0], TestThresholds.HIGH_PRECISION);
		Assert.assertEquals(100.0, data[1], TestThresholds.HIGH_PRECISION);
	}
	
	@Test
	public void testSave() throws IOException {
		double[][] data = new double[][] {{ 0.0, 1.0, -1.0 }, { 0.0, 1e9, 1e-9 }};
		
		try (StringWriter writer = new StringWriter()) {
			MatrixIO.save(writer, data);
			Assert.assertEqualsNormalized("0.0 1.0 -1.0\n0.0 1.0E9 1.0E-9", writer.toString());
		}
		
	}
	
}

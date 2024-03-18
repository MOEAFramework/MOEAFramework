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

import org.junit.Assert;
import org.junit.Test;
import org.moeaframework.TestUtils;

public class ParameterTest {

	@Test
	public void testScale() {
		Parameter parameter = new Parameter("foo", 100, 1000);
		
		TestUtils.assertEquals(100, parameter.scale(0.0));
		TestUtils.assertEquals(1000, parameter.scale(1.0));
		TestUtils.assertEquals(550, parameter.scale(0.5));
	}
	
	@Test
	public void testScaleBoundsCheck() {
		Parameter parameter = new Parameter("foo", 100, 1000);
		
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.scale(-0.001));
		Assert.assertThrows(IllegalArgumentException.class, () -> parameter.scale(1.001));
	}

}

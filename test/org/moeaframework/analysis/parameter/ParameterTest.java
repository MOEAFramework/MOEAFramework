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
package org.moeaframework.analysis.parameter;

import org.junit.Test;
import org.moeaframework.Assert;

public class ParameterTest {
	
	@Test
	public void testDecode() {
		Assert.assertInstanceOf(Constant.class, Parameter.decode("foo const 1"));
		Assert.assertInstanceOf(Enumeration.class, Parameter.decode("foo enum 1 2 3"));
		Assert.assertInstanceOf(IntegerRange.class, Parameter.decode("foo int 1 2"));
		Assert.assertInstanceOf(LongRange.class, Parameter.decode("foo long 1 2"));
		Assert.assertInstanceOf(DecimalRange.class, Parameter.decode("foo decimal 1.0 2.0"));
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testDecodeInvalidType() {
		Parameter.decode("foo bar 1");
	}
	
	@Test(expected = InvalidParameterException.class)
	public void testMissingType() {
		Parameter.decode("foo");
	}

}

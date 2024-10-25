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
import org.moeaframework.util.io.Tokenizer;

public class ParameterTest {
	
	@Test
	public void testDecode() {
		Tokenizer tokenizer = new Tokenizer();
		Assert.assertInstanceOf(Constant.class, Parameter.decode(tokenizer, "foo const 1"));
		Assert.assertInstanceOf(Enumeration.class, Parameter.decode(tokenizer, "foo enum 1 2 3"));
		Assert.assertInstanceOf(IntegerRange.class, Parameter.decode(tokenizer, "foo int 1 2"));
		Assert.assertInstanceOf(LongRange.class, Parameter.decode(tokenizer, "foo long 1 2"));
		Assert.assertInstanceOf(DecimalRange.class, Parameter.decode(tokenizer, "foo decimal 1.0 2.0"));
	}

}

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
package org.moeaframework.analysis.diagnostics;

import java.awt.Paint;
import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.moeaframework.Assert;

public class PaintHelperTest {
	
	@Test
	public void test() {
		PaintHelper paintHelper = new PaintHelper();
		
		ResultKey key1 = new ResultKey("foo", "bar");
		ResultKey key2 = new ResultKey("foo", "baz");
		ResultKey key3 = new ResultKey("bar", "foo");
		
		Paint paint1 = paintHelper.get(key1);
		Paint paint2 = paintHelper.get(key2);
		Paint paint3 = paintHelper.get(key3);
		
		// each key returns a unique paint
		Set<Paint> paints = new HashSet<>();
		Assert.assertTrue(paints.add(paint1));
		Assert.assertTrue(paints.add(paint2));
		Assert.assertTrue(paints.add(paint3));
		
		// the same key returns the same paint
		Assert.assertEquals(paint1, paintHelper.get(key1));
		
		// clearing the paints resets the assignment
		paintHelper.clear();
		Assert.assertEquals(paint1, paintHelper.get(key2));
	}

}

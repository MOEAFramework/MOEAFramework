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
package org.moeaframework.core.variable;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.util.tree.Rules;

public class ProgramTest {

	private Program program;

	@Before
	public void setUp() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		
		program = new Program(rules);
	}

	@After
	public void tearDown() {
		program = null;
	}
	
	// TODO: Make this test better.  Is there a better way to validate?
	@Test
	public void randomize() {
		program.randomize();
	}

	@Test
	public void testCopy() {
		Program copy = program.copy();
		Assert.assertNotSame(copy, program);
	}
	
	// TODO: Encode works but stores a null value, which fails to decode
	@Test(expected = NullPointerException.class)
	public void testEncodeDecodeNullProgram() {
		String encoding = program.encode();
		program.decode(encoding);
	}
	
	// TODO: Programs are currently not Serializable, so encoding does does not work!
	@Test(expected = FrameworkException.class)
	public void testEncodeDecode() {
		program.randomize();
		String encoding = program.encode();
		program.decode(encoding);
	}

}

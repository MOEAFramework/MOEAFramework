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
import org.junit.Before;
import org.junit.Test;
import org.moeaframework.Assert;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Get;
import org.moeaframework.util.tree.Rules;

public class ProgramTest {

	private Rules rules;
	private Program program;

	@Before
	public void setUp() {
		rules = new Rules();
		rules.populateWithDefaults();
		rules.add(new Get(Number.class, "x"));
		rules.setReturnType(Number.class);
		rules.setMaxVariationDepth(10);
		
		program = new Program(rules);
		program.randomize();
	}

	@After
	public void tearDown() {
		program = null;
	}
	
	@Test
	public void testVoidReturnType() {
		rules.setReturnType(Void.class);
		program.randomize();
		Assert.assertNull(testEvaluate(program, true));
	}
	
	@Test
	public void testEvaluate() {
		testEvaluate(program, false);
	}
	
	@Test
	public void testCopy() {
		Program copy = program.copy();
		Assert.assertNotSame(copy, program);
		Assert.assertNotSame(copy.getArgument(0), program.getArgument(0));
		Assert.assertEquals(copy.getNumberOfArguments(), program.getNumberOfArguments());
		Assert.assertEquals(copy.getArgument(0).toString(), program.getArgument(0).toString());
	}
	
	@Test
	public void testEncodeDecode() {
		String encoding = program.encode();
		
		Program copy = program.copy();
		copy.decode(encoding);
		
		Assert.assertEquals(copy.getNumberOfArguments(), program.getNumberOfArguments());
		Assert.assertEquals(copy.getArgument(0).toString(), program.getArgument(0).toString());
	}
	
	@Test
	public void testEncodeDecodeEmptyProgram() {
		Program program = new Program(rules);
		String encoding = program.encode();
		
		Program copy = program.copy();
		copy.decode(encoding);
		
		Assert.assertEquals(copy.getNumberOfArguments(), program.getNumberOfArguments());
		Assert.assertEquals(copy.getArgument(0).toString(), program.getArgument(0).toString());
	}
	
	private Object testEvaluate(Program program, boolean allowNull) {
		Environment environment = new Environment();
		environment.set("x", 5);
		
		Object result = program.evaluate(environment);
		
		if (!allowNull) {
			Assert.assertNotNull(result);
		}
		
		return result;
	}

}

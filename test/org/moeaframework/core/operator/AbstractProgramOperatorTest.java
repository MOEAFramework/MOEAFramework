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
package org.moeaframework.core.operator;

import org.moeaframework.core.Variation;
import org.moeaframework.core.variable.Program;
import org.moeaframework.util.tree.Rules;

public abstract class AbstractProgramOperatorTest<T extends Variation> extends AbstractOperatorTest<T, Program> {
	
	@Override
	public Program createTestVariable() {
		Rules rules = new Rules();
		rules.populateWithDefaults();
		rules.setReturnType(Number.class);
		rules.setMaxVariationDepth(5);

		Program program = new Program(rules);
		program.randomize();
		return program;
	}

}

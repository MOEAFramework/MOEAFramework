/* Copyright 2009-2012 David Hadka
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
package org.moeaframework.util.tree.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.IfElse;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Sequence;

public class AntProblem extends AbstractProblem {

	private final Rules rules;
	
	private final World world;

	public AntProblem(File file, int maxMoves) throws FileNotFoundException,
	IOException {
		super(1, 1);
		
		rules = new Rules();
		rules.add(new TurnLeft());
		rules.add(new TurnRight());
		rules.add(new MoveForward());
		rules.add(new IsFoodAhead());
		rules.add(new IfElse(Void.class));
		rules.add(new Sequence(Void.class, Void.class));
		
		world = new World(file, maxMoves);
	}

	@Override
	public synchronized void evaluate(Solution solution) {
		Program program = (Program)solution.getVariable(0);
		
		world.reset();
		
		while ((world.getRemainingMoves() > 0) &&
				(world.getRemainingFood() > 0)) {
			Environment environment = new Environment();
			environment.set("world", world);
			program.evaluate(environment);
		}
		
		solution.setObjective(0, world.getRemainingFood());
	}
	
	public void displayLastEvaluation() {
		System.out.println("Moves: " + world.getNumberOfMoves());
		System.out.println("Food: " + world.getFoodEaten());
		world.display();
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules, Void.class));
		return solution;
	}

}

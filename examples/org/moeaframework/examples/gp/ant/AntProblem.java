/* Copyright 2009-2016 David Hadka
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
package org.moeaframework.examples.gp.ant;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.moeaframework.core.Solution;
import org.moeaframework.core.variable.Program;
import org.moeaframework.problem.AbstractProblem;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.IfElse;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Sequence;

/**
 * The ant trail problem.  A program for controlling an ant must be discovered
 * to traverse a world and maximize the amount of food eaten.  The ant should
 * also minimize the number of steps required to do so.
 * <p>
 * References:
 * <ol>
 *   <li>Koza, J.R.  "Genetic Programming: On the Programming of Computers by
 *       Means of Natural Selection."  MIT Press, Cambridge, MA, USA, 1992.
 * </ol>
 */
public class AntProblem extends AbstractProblem {

	/**
	 * The rules for building the ant trail program.
	 */
	private final Rules rules;
	
	/**
	 * The world that the ant occupies.
	 */
	private final World world;

	/**
	 * Constructs a new ant trail problem using the ant trail defined in the
	 * specified file.
	 * 
	 * @param file the file containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws FileNotFoundException if the file was not found
	 * @throws IOException if an I/O error occurred
	 */
	public AntProblem(File file, int maxMoves) throws FileNotFoundException,
	IOException {
		this(new FileReader(file), maxMoves);
	}
	
	/**
	 * Constructs a new ant trail problem using the ant trail defined in the
	 * specified input stream.
	 * 
	 * @param inputStream the input stream containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws IOException if an I/O error occurred
	 */
	public AntProblem(InputStream inputStream, int maxMoves) throws
	IOException {
		this(new InputStreamReader(inputStream), maxMoves);
	}
	
	/**
	 * Constructs a new ant trail problem using the ant trail defined in the
	 * specified reader.
	 * 
	 * @param reader the reader containing the ant trail
	 * @param maxMoves the maximum number of moves the ant can expend to find
	 *        food
	 * @throws IOException if an I/O error occurred
	 */
	public AntProblem(Reader reader, int maxMoves) throws IOException {
		super(1, 1);
		
		rules = new Rules();
		rules.add(new TurnLeft());
		rules.add(new TurnRight());
		rules.add(new MoveForward());
		rules.add(new IsFoodAhead());
		rules.add(new IfElse(Void.class));
		rules.add(new Sequence(Void.class, Void.class));
		rules.setReturnType(Void.class);
		
		world = new World(reader, maxMoves);
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
		
		solution.setObjective(0, world.getRemainingFood() + 
				(world.getNumberOfMoves() / (world.getMaxMoves() + 1.0)));
	}
	
	/**
	 * Prints a visual representation of the last evaluated solution to this
	 * problem.
	 */
	public void displayLastEvaluation() {
		System.out.println("Moves: " + world.getNumberOfMoves() + " / " +
				world.getMaxMoves());
		System.out.println("Food: " + world.getFoodEaten() + " / " + 
				world.getTotalFood());
		world.display();
	}

	@Override
	public Solution newSolution() {
		Solution solution = new Solution(1, 1);
		solution.setVariable(0, new Program(rules));
		return solution;
	}

}

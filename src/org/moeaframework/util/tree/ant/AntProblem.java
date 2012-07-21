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

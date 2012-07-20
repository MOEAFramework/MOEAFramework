package org.moeaframework.util.tree.ant;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

public class TurnRight extends Node {
	
	public TurnRight() {
		super();
	}

	@Override
	public TurnRight copyNode() {
		return new TurnRight();
	}

	@Override
	public Void evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		world.turnRight();
		return null;
	}

}

package org.moeaframework.util.tree.ant;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

public class TurnLeft extends Node {
	
	public TurnLeft() {
		super();
	}

	@Override
	public TurnLeft copyNode() {
		return new TurnLeft();
	}

	@Override
	public Void evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		world.turnLeft();
		return null;
	}

}

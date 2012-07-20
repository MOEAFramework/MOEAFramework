package org.moeaframework.util.tree.ant;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

public class MoveForward extends Node {
	
	public MoveForward() {
		super();
	}

	@Override
	public MoveForward copyNode() {
		return new MoveForward();
	}

	@Override
	public Void evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		world.moveForward();
		return null;
	}

}

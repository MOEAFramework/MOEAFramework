package org.moeaframework.util.tree.ant;

import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;

public class IsFoodAhead extends Node {
	
	public IsFoodAhead() {
		super(Boolean.class);
	}

	@Override
	public IsFoodAhead copyNode() {
		return new IsFoodAhead();
	}

	@Override
	public Boolean evaluate(Environment environment) {
		World world = environment.get(World.class, "world");
		return world.isFoodAhead();
	}

}

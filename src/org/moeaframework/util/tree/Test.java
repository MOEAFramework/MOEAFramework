package org.moeaframework.util.tree;


public class Test {
	
	public static void main(String[] args) {
		Rules nodeSet = new Rules();
		nodeSet.populateWithDefaults();
		
		Define function = new Define("eval", Number.class, "x", Number.class, "y", Number.class);
		
		//ensure the function and its arguments can be used as nodes
		nodeSet.add(new Get(Number.class, "x"));
		nodeSet.add(new Get(Number.class, "y"));
		nodeSet.add(new Call(function));
		
		Node base = new Sequence(Number.class)
				.setArgument(0, function);

		Node node = nodeSet.buildTreeFull(base, 5);
		System.out.println(node);
		System.out.println(node.isValid());
		System.out.println(node.evaluate(new Environment()));
	}

}

package org.moeaframework.util.tree;


public class TestFactorial {
	
	public static void main(String[] args) {
		/*
		 * fact(n):
		 *   if (n <= 1) return 1;
		 *   else return n * fact(n-1);
		 */
		Node factorial = new Define("factorial", Number.class, "n", Number.class).setArgument(0, 
				new IfElse(Number.class)
						.setArgument(0, new LessThanOrEqual()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Constant(1)))
						.setArgument(1, new Constant(1))
						.setArgument(2, new Multiply()
								.setArgument(0, new Get(Number.class, "n"))
								.setArgument(1, new Call("factorial", Number.class, "n", Number.class)
										.setArgument(0, new Subtract()
												.setArgument(0, new Get(Number.class, "n"))
												.setArgument(1, new Constant(1))))));
		
		Node program = new Sequence()
				.setArgument(0, factorial)
				.setArgument(1, new Call("factorial", Number.class, "n", Number.class)
						.setArgument(0, new Constant(5)));
		
		Environment environment = new Environment();
		System.out.println(program.isValid());
		System.out.println(program.evaluate(environment));
	}

}

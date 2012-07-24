package org.moeaframework.examples.gp.regression;

import org.moeaframework.util.tree.Add;
import org.moeaframework.util.tree.Cos;
import org.moeaframework.util.tree.Divide;
import org.moeaframework.util.tree.Exp;
import org.moeaframework.util.tree.Get;
import org.moeaframework.util.tree.Log;
import org.moeaframework.util.tree.Multiply;
import org.moeaframework.util.tree.Rules;
import org.moeaframework.util.tree.Sin;
import org.moeaframework.util.tree.Subtract;

public class Quintic extends SymbolicRegression {

	public Quintic() {
		super(getRules(), "x", -1.0, 1.0, 100);
	}
	
	public static Rules getRules() {
		Rules rules = new Rules();
		rules.add(new Add());
		rules.add(new Multiply());
		rules.add(new Subtract());
		rules.add(new Divide());
		rules.add(new Sin());
		rules.add(new Cos());
		rules.add(new Exp());
		rules.add(new Log());
		rules.add(new Get(Number.class, "x"));
		rules.setReturnType(Number.class);
		rules.setMaxVariationDepth(10);
		return rules;
	}

	@Override
	public double evaluate(double x) {
		return x*x*x*x*x* - 2.0*x*x*x + x;
	}
	
	public static void main(String[] args) {
		new Quintic().runDemo(1000);
	}
	
}

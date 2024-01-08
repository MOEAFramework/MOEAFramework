package org.moeaframework.problem.ZCAT;

import static org.moeaframework.problem.ZCAT.PFShapeFunction.valueBetween;

public class ZCAT20 extends ZCAT {
	
	public ZCAT20(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT20(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F20,
				complicatedPS ? PSShapeFunction.G3 : PSShapeFunction.G0);
	}
	
	@Override
	public int getDimension(double[] y) {
		return valueBetween(y[0], 0.1, 0.4) || valueBetween(y[0], 0.6, 0.9) ? 1 : numberOfObjectives - 1;
	}

}
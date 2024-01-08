package org.moeaframework.problem.ZCAT;

import static org.moeaframework.problem.ZCAT.PFShapeFunction.allValuesBetween;

public class ZCAT19 extends ZCAT {
	
	public ZCAT19(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT19(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F19,
				complicatedPS ? PSShapeFunction.G6 : PSShapeFunction.G0);
	}
	
	@Override
	public int getDimension(double[] y) {
		return allValuesBetween(y, 1, 0.0, 0.2) || allValuesBetween(y, 1, 0.4, 0.6) ? 1 : numberOfObjectives - 1;
	}

}

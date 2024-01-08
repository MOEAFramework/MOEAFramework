package org.moeaframework.problem.ZCAT;

public class ZCAT15 extends ZCAT {
	
	public ZCAT15(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT15(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F15,
				complicatedPS ? PSShapeFunction.G8 : PSShapeFunction.G0);
	}
	
	@Override
	public int getDimension(double[] y) {
		return 1;
	}

}

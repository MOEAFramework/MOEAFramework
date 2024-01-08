package org.moeaframework.problem.ZCAT;

public class ZCAT16 extends ZCAT {
	
	public ZCAT16(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT16(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F16,
				complicatedPS ? PSShapeFunction.G10 : PSShapeFunction.G0);
	}
	
	@Override
	public int getDimension(double[] y) {
		return 1;
	}

}

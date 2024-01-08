package org.moeaframework.problem.ZCAT;

public class ZCAT9 extends ZCAT {
	
	public ZCAT9(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT9(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F9,
				complicatedPS ? PSShapeFunction.G7 : PSShapeFunction.G0);
	}

}

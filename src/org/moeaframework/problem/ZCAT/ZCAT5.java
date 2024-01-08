package org.moeaframework.problem.ZCAT;

public class ZCAT5 extends ZCAT {
	
	public ZCAT5(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT5(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F5,
				complicatedPS ? PSShapeFunction.G9 : PSShapeFunction.G0);
	}

}

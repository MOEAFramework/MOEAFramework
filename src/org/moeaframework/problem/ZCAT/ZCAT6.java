package org.moeaframework.problem.ZCAT;

public class ZCAT6 extends ZCAT {
	
	public ZCAT6(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT6(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F6,
				complicatedPS ? PSShapeFunction.G4 : PSShapeFunction.G0);
	}

}

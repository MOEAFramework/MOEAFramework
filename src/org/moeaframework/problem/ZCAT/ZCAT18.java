package org.moeaframework.problem.ZCAT;

public class ZCAT18 extends ZCAT {
	
	public ZCAT18(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT18(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F18,
				complicatedPS ? PSShapeFunction.G8 : PSShapeFunction.G0);
	}

}

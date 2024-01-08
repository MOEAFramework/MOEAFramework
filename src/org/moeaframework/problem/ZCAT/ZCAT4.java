package org.moeaframework.problem.ZCAT;

public class ZCAT4 extends ZCAT {
	
	public ZCAT4(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT4(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F4,
				complicatedPS ? PSShapeFunction.G7 : PSShapeFunction.G0);
	}

}

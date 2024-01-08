package org.moeaframework.problem.ZCAT;

public class ZCAT2 extends ZCAT {
	
	public ZCAT2(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT2(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F2,
				complicatedPS ? PSShapeFunction.G5 : PSShapeFunction.G0);
	}

}

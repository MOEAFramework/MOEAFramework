package org.moeaframework.problem.ZCAT;

public class ZCAT11 extends ZCAT {
	
	public ZCAT11(int numberOfObjectives) {
		this(numberOfObjectives, true);
	}
	
	public ZCAT11(int numberOfObjectives, boolean complicatedPS) {
		super(numberOfObjectives, 1, false, false, PFShapeFunction.F11,
				complicatedPS ? PSShapeFunction.G3 : PSShapeFunction.G0);
	}

}

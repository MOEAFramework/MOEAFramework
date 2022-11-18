package org.moeaframework.problem.CDTLZ;

import java.util.Locale;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Problem;
import org.moeaframework.core.spi.ProblemProvider;

/**
 * Problem provider for the constrained DTLZ test problems.
 */
public class CDTLZProblemProvider extends ProblemProvider {

	/**
	 * Constructs and registers the DTLZ problems.
	 */
	public CDTLZProblemProvider() {
		super();
	}
	
	@Override
	public Problem getProblem(String name) {
		name = name.toUpperCase(Locale.ROOT);
		
		try {
			if (name.startsWith("C1_DTLZ1_")) {
				return new C1_DTLZ1(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C1_DTLZ3_")) {
				return new C1_DTLZ3(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C2_DTLZ2_")) {
				return new C2_DTLZ2(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C3_DTLZ1_")) {
				return new C3_DTLZ1(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("C3_DTLZ4_")) {
				return new C3_DTLZ4(Integer.parseInt(name.substring(9)));
			} else if (name.startsWith("CONVEX_C2_DTLZ2_")) {
				return new ConvexC2_DTLZ2(Integer.parseInt(name.substring(15)));
			}
		} catch (NumberFormatException e) {
			return null;
		}
		
		return null;
	}

	@Override
	public NondominatedPopulation getReferenceSet(String name) {
		return null;
	}
	
}

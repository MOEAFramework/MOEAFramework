package org.moeaframework.util.format;

import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryVariable;
import org.moeaframework.core.variable.Permutation;
import org.moeaframework.core.variable.RealVariable;
import org.moeaframework.core.variable.Subset;

/**
 * Formatter for {@link Variable}s.
 */
public class VariableFormatter implements Formatter<Variable> {
	
	private NumberFormatter numberFormatter;

	public VariableFormatter() {
		super();
		numberFormatter = new NumberFormatter();
	}
	
	@Override
	public Class<Variable> getType() {
		return Variable.class;
	}

	@Override
	public String format(Object value) {
		StringBuilder sb = new StringBuilder();

		if (value instanceof RealVariable) {
			RealVariable real = (RealVariable)value;
			sb.append(numberFormatter.format(real.getValue()));
		} else if (value instanceof BinaryVariable) {
			BinaryVariable binary = (BinaryVariable)value;
			
			for (int i = 0; i < binary.getNumberOfBits(); i++) {
				sb.append(binary.get(i) ? "1" : "0");
			}
		} else if (value instanceof Permutation) {
			int[] permutation = ((Permutation)value).toArray();

			for (int i = 0; i < permutation.length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				
				sb.append(permutation[i]);
			}
		} else if (value instanceof Subset) {
			int[] subset = ((Subset)value).toArray();
			
			for (int i = 0; i < subset.length; i++) {
				if (i > 0) {
					sb.append(',');
				}
				
				sb.append(subset[i]);
			}
		} else {
			sb.append("-");
		}
		
		return sb.toString();
	}

}

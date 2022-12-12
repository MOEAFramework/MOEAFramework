package org.moeaframework.util.format;

import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.BinaryIntegerVariable;
import org.moeaframework.core.variable.RealVariable;

/**
 * Formatter for {@link Variable}s.  Primarily, this uses the registered formatter,
 * if any, for numeric types.
 */
public class VariableFormatter implements Formatter<Variable> {
	
	private TabularData<?> data;

	/**
	 * Constructs a new variable formatter.
	 */
	public VariableFormatter(TabularData<?> data) {
		super();
		this.data = data;
	}
	
	@Override
	public Class<Variable> getType() {
		return Variable.class;
	}

	@Override
	public String format(Object variable) {
		if (variable instanceof RealVariable) {
			return data.formatValue(((RealVariable)variable).getValue());
		} else if (variable instanceof BinaryIntegerVariable) {
			return data.formatValue(((BinaryIntegerVariable)variable).getValue());
		} else {
			return variable.toString();
		}
	}

}

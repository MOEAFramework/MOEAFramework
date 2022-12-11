package org.moeaframework.util.format;

import org.moeaframework.core.Variable;
import org.moeaframework.core.variable.RealVariable;

/**
 * Formatter for {@link Variable}s.
 */
public class VariableFormatter implements Formatter<Variable> {
	
	private NumberFormatter numberFormatter;

	/**
	 * Constructs a new variable formatter.
	 */
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
		if (value instanceof RealVariable) {
			RealVariable real = (RealVariable)value;
			return numberFormatter.format(real.getValue());
		} else {
			return ((Variable)value).toString();
		}
	}

}

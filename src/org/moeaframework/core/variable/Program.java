/* Copyright 2009-2024 David Hadka
 *
 * This file is part of the MOEA Framework.
 *
 * The MOEA Framework is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your
 * option) any later version.
 *
 * The MOEA Framework is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with the MOEA Framework.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.moeaframework.core.variable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Base64;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * A decision variable for programs.  The program is represented as a strongly-typed expression tree.
 * <p>
 * <b>Note: Although {@code Program} extends {@link Node}, the {@code Program} object must never be altered by the
 * optimization algorithm.</b>  Only its arguments can undergo variation.
 */
public class Program extends AbstractVariable {

	private static final long serialVersionUID = -2621361322042428290L;

	/**
	 * The rules defining the program syntax.
	 */
	private final Rules rules;
	
	/**
	 * The root of the program tree.
	 */
	private Root root;

	/**
	 * Constructs a new program variable with the specified syntax rules.
	 * 
	 * @param rules the rules defining the program syntax
	 */
	public Program(Rules rules) {
		super();
		this.rules = rules;
		this.root = new Root();
	}
	
	/**
	 * Returns the rules defining the program syntax.
	 * 
	 * @return the rules defining the program syntax
	 */
	public Rules getRules() {
		return rules;
	}
	
	/**
	 * Returns the root node of this program, primarily intended for testing.
	 * 
	 * @return the root
	 */
	Root getRoot() {
		return root;
	}
	
	/**
	 * Returns the body of this program.
	 * 
	 * @return the top-level node
	 */
	public Node getBody() {
		return root.getBody();
	}
	
	/**
	 * Sets the body of this program.
	 * 
	 * @param node the top-level node
	 */
	public void setBody(Node node) {
		root.setBody(node);
	}
	
	@Override
	public Program copy() {
		Program program = new Program(rules);
		
		if (getBody() != null) {
			program.setBody(getBody().copyTree());
		}
		
		return program;
	}
	
	/**
	 * Evaluates this program using the given environment.
	 * 
	 * @param environment the environment
	 * @return the result
	 */
	public Object evaluate(Environment environment) {
		return root.evaluate(environment);
	}

	/**
	 * Initializes the program tree using ramped half-and-half initialization.
	 */
	@Override
	public void randomize() {
		Rules rules = getRules();
		int depth = PRNG.nextInt(2, rules.getMaxInitializationDepth());
		boolean isFull = PRNG.nextBoolean();
		Node body = null;
		
		if (isFull) {
			if (rules.getScaffolding() == null) {
				body = rules.buildTreeFull(rules.getReturnType(), depth);
			} else {
				body = rules.buildTreeFull(rules.getScaffolding(), depth);
			}
		} else {
			if (rules.getScaffolding() == null) {
				body = rules.buildTreeGrow(rules.getReturnType(), depth);
			} else {
				body = rules.buildTreeGrow(rules.getScaffolding(), depth);
			}
		}
		
		setBody(body);
	}
	
	@Override
	public String getDefinition() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String encode() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(root);
			return "Program(" + Base64.getEncoder().encodeToString(baos.toByteArray()) + ")";
		} catch (IOException e) {
			throw new FrameworkException("failed to encode program", e);
		}
	}
	
	@Override
	public void decode(String value) {
		if (!value.startsWith("Program(") || !value.endsWith(")")) {
			throw new FrameworkException("failed to decode program, missing 'Program(' ... ')'");
		}
		
		byte[] encoding = Base64.getDecoder().decode(value.substring(8, value.length()-1));
		
		try (ByteArrayInputStream baos = new ByteArrayInputStream(encoding);
				ObjectInputStream ois = new ObjectInputStream(baos)) {
			root = (Root)ois.readObject();
		} catch (IOException | ClassNotFoundException e) {
			throw new FrameworkException("failed to decode program", e);
		}
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.appendSuper(super.hashCode())
				.append(rules)
				.append(root)
				.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if ((obj == null) || (obj.getClass() != getClass())) {
			return false;
		} else {
			Program rhs = (Program)obj;
			
			return new EqualsBuilder()
					.appendSuper(super.equals(obj))
					.append(rules, rhs.rules)
					.append(root, rhs.root)
					.isEquals();
		}
	}
	
	/**
	 * Node representing the root of a program.  This is useful to ensure the body has a non-null parent, which is
	 * required by some variation operators to swap or change the top-level nodes in a program.
	 */
	class Root extends Node {
		
		private static final long serialVersionUID = -7045431952776749584L;
				
		public Root() {
			super(getRules().getReturnType(), getRules().getReturnType());
		}
		
		public Node getBody() {
			return getArgument(0);
		}
		
		public void setBody(Node node) {
			setArgument(0, node);
		}

		@Override
		public Root copyNode() {
			return new Root();
		}

		@Override
		public Object evaluate(Environment environment) {
			return getBody().evaluate(environment);
		}

	}

}

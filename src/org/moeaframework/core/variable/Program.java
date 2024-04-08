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

import org.moeaframework.core.FrameworkException;
import org.moeaframework.core.PRNG;
import org.moeaframework.core.Variable;
import org.moeaframework.util.tree.Environment;
import org.moeaframework.util.tree.NOP;
import org.moeaframework.util.tree.Node;
import org.moeaframework.util.tree.Rules;

/**
 * A decision variable for programs.  The program is represented as a strongly-typed expression tree.
 * <p>
 * <b>Note: Although {@code Program} extends {@link Node}, the {@code Program} object must never be altered by the
 * optimization algorithm.</b>  Only its arguments can undergo variation.
 */
public class Program extends Node implements Variable {

	private static final long serialVersionUID = -2621361322042428290L;

	/**
	 * The rules defining the program syntax.
	 */
	private final Rules rules;

	/**
	 * Constructs a new program variable with the specified syntax rules.
	 * 
	 * @param rules the rules defining the program syntax
	 */
	public Program(Rules rules) {
		super(rules.getReturnType(), rules.getReturnType());
		this.rules = rules;
		
		setArgument(0, new NOP());
	}
	
	/**
	 * Returns the rules defining the program syntax.
	 * 
	 * @return the rules defining the program syntax
	 */
	public Rules getRules() {
		return rules;
	}
	
	@Override
	public Program copy() {
		return (Program)copyTree();
	}

	@Override
	public Program copyNode() {
		return new Program(rules);
	}

	@Override
	public Object evaluate(Environment environment) {
		return getArgument(0).evaluate(environment);
	}

	/**
	 * Initializes the program tree using ramped half-and-half initialization.
	 */
	@Override
	public void randomize() {
		Rules rules = getRules();
		int depth = PRNG.nextInt(2, rules.getMaxInitializationDepth());
		boolean isFull = PRNG.nextBoolean();
		Node root = null;
		
		if (isFull) {
			if (rules.getScaffolding() == null) {
				root = rules.buildTreeFull(rules.getReturnType(), depth);
			} else {
				root = rules.buildTreeFull(rules.getScaffolding(), depth);
			}
		} else {
			if (rules.getScaffolding() == null) {
				root = rules.buildTreeGrow(rules.getReturnType(), depth);
			} else {
				root = rules.buildTreeGrow(rules.getScaffolding(), depth);
			}
		}
		
		setArgument(0, root);
	}
	
	@Override
	public String encode() {
		try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
				ObjectOutputStream oos = new ObjectOutputStream(baos)) {
			oos.writeObject(getArgument(0));
			return "Program(" + Base64.getEncoder().encodeToString(baos.toByteArray()) + ")";
		} catch (IOException e) {
			throw new FrameworkException("failed to encode program", e);
		}
	}
	
	@Override
	public void decode(String value) {
		if (!value.startsWith("Program(") || !value.endsWith(")")) {
			throw new IllegalArgumentException("invalid variable encoding, missing Program(...)");
		}
		
		byte[] encoding = Base64.getDecoder().decode(value.substring(8, value.length()-1));
		
		try (ByteArrayInputStream baos = new ByteArrayInputStream(encoding);
				ObjectInputStream ois = new ObjectInputStream(baos)) {
			setArgument(0, (Node)ois.readObject());
		} catch (IOException | ClassNotFoundException e) {
			throw new FrameworkException("failed to decode program", e);
		}
	}

}

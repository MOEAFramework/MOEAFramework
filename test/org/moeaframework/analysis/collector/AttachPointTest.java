/* Copyright 2009-2019 David Hadka
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
package org.moeaframework.analysis.collector;

import java.util.Stack;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests the {@link AttachPoint} class.
 */
public class AttachPointTest {
	
	public static class TypeA {
		
	}
	
	public static class TypeB {
		
	}
	
	public static class TypeC extends TypeB {
		
	}
	
	private AttachPoint FALSE;
	
	private AttachPoint TRUE;
	
	@Before
	public void setUp() {
		FALSE = new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				return false;
			}
			
		};
		
		TRUE = new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				return true;
			}
			
		};
	}
	
	@Test
	public void testAnd() {
		Assert.assertFalse(FALSE.and(FALSE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertFalse(FALSE.and(TRUE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertFalse(TRUE.and(FALSE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertTrue(TRUE.and(TRUE).matches(new Stack<Object>(), 
				new TypeA()));
	}
	
	@Test
	public void testOr() {
		Assert.assertFalse(FALSE.or(FALSE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertTrue(FALSE.or(TRUE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertTrue(TRUE.or(FALSE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertTrue(TRUE.or(TRUE).matches(new Stack<Object>(), 
				new TypeA()));
	}
	
	@Test
	public void testNot() {
		Assert.assertTrue(AttachPoint.not(FALSE).matches(new Stack<Object>(), 
				new TypeA()));
		Assert.assertFalse(AttachPoint.not(TRUE).matches(new Stack<Object>(), 
				new TypeA()));
	}
	
	@Test
	public void testIsClass() {
		Assert.assertFalse(AttachPoint.isClass(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
		Assert.assertTrue(AttachPoint.isClass(TypeB.class).matches(
				new Stack<Object>(), new TypeB()));
		Assert.assertFalse(AttachPoint.isClass(TypeB.class).matches(
				new Stack<Object>(), new TypeC()));
		
		//test empty parents
		Assert.assertFalse(AttachPoint.isClass(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
	}
	
	@Test
	public void testIsSubclass() {
		Assert.assertFalse(AttachPoint.isSubclass(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
		Assert.assertTrue(AttachPoint.isSubclass(TypeB.class).matches(
				new Stack<Object>(), new TypeB()));
		Assert.assertTrue(AttachPoint.isSubclass(TypeB.class).matches(
				new Stack<Object>(), new TypeC()));
		
		//test empty parents
		Assert.assertFalse(AttachPoint.isSubclass(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
	}
	
	@Test
	public void testIsDeclaredIn() {
		Stack<Object> parents = new Stack<Object>();
		parents.add(new TypeB());
		
		Assert.assertFalse(AttachPoint.isDeclaredIn(TypeA.class).matches(
				parents, new TypeA()));
		Assert.assertTrue(AttachPoint.isDeclaredIn(TypeB.class).matches(
				parents, new TypeA()));
		Assert.assertFalse(AttachPoint.isDeclaredIn(TypeC.class).matches(
				parents, new TypeA()));
		
		//test empty parents
		Assert.assertFalse(AttachPoint.isDeclaredIn(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
	}
	
	@Test
	public void testIsNestedIn() {
		Stack<Object> parents = new Stack<Object>();
		parents.add(new TypeC());
		parents.add(new TypeB());
		
		Assert.assertFalse(AttachPoint.isNestedIn(TypeA.class).matches(
				parents, new TypeA()));
		Assert.assertTrue(AttachPoint.isNestedIn(TypeB.class).matches(
				parents, new TypeA()));
		Assert.assertTrue(AttachPoint.isNestedIn(TypeC.class).matches(
				parents, new TypeA()));
		
		//test empty parents
		Assert.assertFalse(AttachPoint.isNestedIn(TypeB.class).matches(
				new Stack<Object>(), new TypeA()));
	}

}

/* Copyright 2009-2018 David Hadka
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

/**
 * Identifies objects in an object graph (all public and private fields
 * contained within an object and those it references).  For instance,
 * <pre>
 *   AttachPoint.isClass(TypeA.class).and(AttachPoint.isNestedIn(TypeB.class)
 * </pre>
 * will match any objects of type {@code TypeA} that are referenced (or 
 * accessible through) an object of type {@code TypeB}.
 */
public abstract class AttachPoint {
	
	/**
	 * Protected constructor to prevent instantiation, but allow subclassing.
	 */
	protected AttachPoint() {
		super();
	}
	
	/**
	 * Returns an attach point which performs the logical AND between two 
	 * attach points, matching an object only if both attach points match the
	 * object.
	 * 
	 * @param that the second attach point
	 * @return an attach point which performs the logical AND between two 
	 *         attach points
	 */
	public AttachPoint and(final AttachPoint that) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				//note that AttachPoint.this points to the parent class, and
				//not this anonymous class (i.e., A in A.and(B))
				return AttachPoint.this.matches(parents, object) && 
						that.matches(parents, object);
			}
			
		};
	}
	
	/**
	 * Returns an attach point which performs the logical OR between two 
	 * attach points, matching an object if either or both of the attach points
	 * match the object.
	 * 
	 * @param that the second attach point
	 * @return an attach point which performs the logical OR between two 
	 *         attach points
	 */
	public AttachPoint or(final AttachPoint that) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				//note that AttachPoint.this points to the parent class, and
				//not this anonymous class (i.e., A in A.or(B))
				return AttachPoint.this.matches(parents, object) || 
						that.matches(parents, object);
			}
			
		};
	}
	
	/**
	 * Returns an attach point which performs the logical NOT on the given
	 * attach point, matching an object only if the attach point does not match
	 * the object.
	 * 
	 * @param that the original attach point
	 * @return an attach point which performs the logical NOT on the given
	 *         attach point
	 */
	public static AttachPoint not(final AttachPoint that) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				return !that.matches(parents, object);
			}
			
		};
	}
	
	/**
	 * Returns an attach point that matches an object if its type is equal to
	 * the specified type.  Note that subclasses of the type will not match.
	 * 
	 * @param type the required type of the object
	 * @return an attach point that matches an object if its type is equal to
	 *         the specified type
	 */
	public static AttachPoint isClass(final Class<?> type) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				return object.getClass().equals(type);
			}
			
		};
	}
	
	/**
	 * Returns an attach point that matches an object if it is an instance of
	 * the specified type.  The specified type and all subclasses will match.
	 * 
	 * @param type the required type/supertype of the object
	 * @return an attach point that matches an object if it is an instance of
	 *         the specified type
	 */
	public static AttachPoint isSubclass(final Class<?> type) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				return type.isInstance(object);
			}
			
		};
	}
	
	/**
	 * Returns an attach point that matches an object if it is a declared field
	 * of the specified type.
	 * 
	 * @param parentType the parent type
	 * @return an attach point that matches an object if it is a declared field
	 *         of the specified type
	 */
	public static AttachPoint isDeclaredIn(final Class<?> parentType) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				if (parents.isEmpty()) {
					return false;
				} else {
					return parentType.isInstance(parents.peek());
				}
			}
			
		};
	}
	
	/**
	 * Returns an attach point that matches an object if it is nested inside a
	 * class of the specified type.  This includes both declared fields of the 
	 * specified type and all fields accessible by recursively following all
	 * references.
	 * 
	 * @param ancestorType the ancestor type
	 * @return an attach point that matches an object if it is nested inside a
	 *         class of the specified type
	 */
	public static AttachPoint isNestedIn(final Class<?> ancestorType) {
		return new AttachPoint() {

			@Override
			public boolean matches(Stack<Object> parents, Object object) {
				for (Object parent : parents) {
					if (ancestorType.isInstance(parent)) {
						return true;
					}
				}
				
				return false;
			}
			
		};
	}
	
	/**
	 * Returns {@code true} if the specified object in the object graph matches
	 * some criteria; and {@code false} otherwise.
	 * 
	 * @param parents the path from the root (starting) object to the current
	 *        object
	 * @param object the current object
	 * @return {@code true} if the specified object in the object graph matches
	 *         some criteria; and {@code false} otherwise
	 */
	public abstract boolean matches(Stack<Object> parents, Object object);

}

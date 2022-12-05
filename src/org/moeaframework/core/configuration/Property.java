/* Copyright 2009-2022 David Hadka
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
package org.moeaframework.core.configuration;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation added to setter methods that identify auto-configurable properties.  The methods must follow
 * JavaBean conventions.  For a property named {@code foo}, then we expected to see the methods:
 * <pre>
 *    public void setFoo(T value) { ... }
 *    public T getFoo() { ... }
 * </pre>
 * The following types are supported:
 * <ul>
 *   <li>Any Java primitive type (int, double, boolean, etc.)
 *   <li>{@code String}
 *   <li>Any enumeration - the properties are automatically converted to strings
 *   <li>{@code Variation} - the instance is created using {@code OperatorFactory.getInstance().getVariation(value)}
 *   <li>{@code Mutation} - same as {@code Variation} except only supports one parent
 * </ul>
 * If the type is {@code boolean}, then the getter can alternatively be named {@code isFoo}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Property {
	
	/**
	 * The name of this property.  If unset, the name is derived from the method name.  To help disambiguate
	 * property names, especially for variation operators, consider adding the {@link Prefix} annotation.
	 * 
	 * @return the name of this property
	 */
	String value() default "";
	
	/**
	 * One or more alternate names used by this property.
	 * 
	 * @return the alternate names for this property
	 */
	String[] alias() default {};

}

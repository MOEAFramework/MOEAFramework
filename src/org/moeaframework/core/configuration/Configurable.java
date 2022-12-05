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

import org.moeaframework.util.TypedProperties;

/**
 * Interface for classes that can be configured either explicitly by overriding the methods defined by this
 * interface or auto-configurable by using the annotations available in this package.
 * 
 * With auto-configuration, in addition to processing annotated setters, it will also check each getter method
 * and recursively process any that also implement this interface.  However, it will not scan any collection
 * types, such as {@code Iterable}, {@code Collection}, {@code List}, etc., even if they contain
 * {@code Configurable} types.
 */
public interface Configurable {
	
	/**
	 * Applies the properties to this instance.  It is strongly recommended to apply a configuration immediately
	 * after creating the instance, as some properties can not be changed after the class is used.  Exceptions
	 * may be thrown if attempting to set such properties.
	 * 
	 * After calling this method, we encourage users to call {@link TypedProperties#warnIfUnaccessedProperties()}
	 * to verify all properties were processed.  This can identify simple mistakes like typos.
	 * 
	 * If overriding this method, properties should only be updated if a new value is provided.  Additionally, if
	 * updating any {@code Configurable} objects inside this object, they should be updated before calling
	 * {@code super.applyConfiguration(properties)}.
	 * 
	 * @param properties the user-defined properties
	 */
	public default void applyConfiguration(TypedProperties properties) {
		ConfigurationUtils.applyConfiguration(properties, this);
	}
	
	/**
	 * Gets the current configuration of this instance.  In theory, these properties should be able to create a
	 * duplicate instance.  Note however, they are unlikely to behave identically due to random numbers and other
	 * transient fields.
	 * 
	 * @return the properties defining this instance
	 */
	public default TypedProperties getConfiguration() {
		return ConfigurationUtils.getConfiguration(this);
	}

}

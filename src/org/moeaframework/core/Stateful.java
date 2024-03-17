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
package org.moeaframework.core;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Interface for objects that can save and load their state.
 */
public interface Stateful {
	
	/**
	 * Writes the state of this object to the stream.  The order that objects are written to the stream
	 * is important.  We recommend first calling {@code super.saveState(stream)} followed by writing each field.
	 * 
	 * @param stream the stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void saveState(ObjectOutputStream stream) throws IOException {
		throw new NotSerializableException(getClass().getSimpleName());
	}

	/**
	 * Loads the state of this object from the stream.  The order for reading objects from the stream must match
	 * the order they are written to the stream in {@link #saveState(ObjectOutputStream)}.
	 * 
	 * @param stream the stream
	 * @throws IOException if an I/O error occurred
	 * @throws ClassNotFoundException if the stream referenced a class that is not defined
	 */
	public default void loadState(ObjectInputStream stream) throws IOException, ClassNotFoundException {
		throw new NotSerializableException(getClass().getSimpleName());
	}
	
	/**
	 * Writes a field into the object stream that can be validated using
	 * {@link #checkTypeSafety(ObjectInputStream, Object)}.
	 * 
	 * @param stream the stream
	 * @param object the stateful object
	 * @throws IOException if an I/O error occurred
	 */
	public static void writeTypeSafety(ObjectOutputStream stream, Object object) throws IOException {
		stream.writeObject(object.getClass().getCanonicalName());
	}

	/**
	 * Validates the type safety information embedded in the object stream.
	 * 
	 * @param stream the stream
	 * @param object the stateful object
	 * @throws IOException if an I/O error occurred or the type safety check failed
	 * @throws ClassNotFoundException if the stream referenced a class that is not defined
	 */
	public static void checkTypeSafety(ObjectInputStream stream, Object object) throws IOException, ClassNotFoundException {
		String expectedClassName = (String)stream.readObject();
		
		if (expectedClassName != null && !expectedClassName.equals(object.getClass().getCanonicalName())) {
			throw new IOException("failed to load state created from " + expectedClassName + " into " +
					object.getClass().getCanonicalName());
		}
	}

}

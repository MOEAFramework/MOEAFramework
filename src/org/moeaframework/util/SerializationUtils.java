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
package org.moeaframework.util;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility methods for serialization, primarily to assist in serializing collections in a type-safe manner and avoiding
 * unchecked casts.
 */
public class SerializationUtils {

	private SerializationUtils() {
		super();
	}

	/**
	 * Converts the given list into a serializable version, if required.  While most (if not all) collections that are
	 * provided by the JDK are serializable, since the {@link List} interface does not specify it is serializable, it's
	 * possible some implementations are not.
	 * 
	 * @param <T> the type of the list
	 * @param list the list to serialize
	 * @return a serializable version of the list
	 */
	public static final <T extends Serializable> Serializable serializable(List<T> list) {
		if (list instanceof Serializable serializableList) {
			return serializableList;
		}
		
		return new ArrayList<T>(list);
	}
	
	/**
	 * Casts a list with the wildcard {@code <?>} type to a typed list, ensuring each element is if the correct type.
	 * 
	 * @param <T> the type of the list
	 * @param type the expected type of each element in the list
	 * @param list the original list of unknown type
	 * @return the typed list
	 * @throws ClassCastException if the object is not a list, or any element is not the required type
	 */
	public static final <T extends Serializable> List<T> castList(Class<T> type, List<?> list) {
		List<T> result = new ArrayList<T>();
		
		for (Object obj : list) {
			result.add(type.cast(obj));
		}
		
		return result;
	}
	
	/**
	 * Casts an object, typically produced via deserialization, to a typed list.
	 * 
	 * @param <T> the type of the list
	 * @param type the expected type of each element in the list
	 * @param object the object, which is expected to be a list
	 * @return the typed list
	 * @throws ClassCastException if the object is not a list, or any element is not the required type
	 */
	public static final <T extends Serializable> List<T> castList(Class<T> type, Object object) {
		return castList(type, (List<?>)object);
	}
	
	/**
	 * Writes the given list to the object stream.
	 * 
	 * @param <T> the type of the list
	 * @param list the list to serialize
	 * @param stream the object stream
	 * @throws IOException if an error occurred while writing the object
	 */
	public static final <T extends Serializable> void writeList(List<T> list, ObjectOutputStream stream)
			throws IOException {
		stream.writeObject(serializable(list));
	}
	
	/**
	 * Reads a list from the object stream.
	 * 
	 * @param <T> the type of the list
	 * @param type the expected type of each element in the list
	 * @param stream the object stream
	 * @return the typed list
	 * @throws IOException if an error occurred while writing the object
	 * @throws ClassNotFoundException if any type being serialized could not be found
	 */
	public static final <T extends Serializable> List<T> readList(Class<T> type, ObjectInputStream stream)
			throws IOException, ClassNotFoundException {
		return castList(type, stream.readObject());
	}

}
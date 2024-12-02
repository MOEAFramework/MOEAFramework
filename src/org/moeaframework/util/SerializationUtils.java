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
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;

/**
 * Utility methods for serialization, primarily to assist in serializing collections in a type-safe manner and avoiding
 * unchecked casts.
 */
public class SerializationUtils {

	private SerializationUtils() {
		super();
	}
	
	/**
	 * Casts an object, typically produced via deserialization, to a typed list.
	 * 
	 * @param <V> the type of the values
	 * @param <T> the return type
	 * @param type the expected type of each element in the list
	 * @param generator a supplier responsible for creating an empty list
	 * @param object the object, which is expected to be a list
	 * @return the typed list
	 * @throws ClassCastException if the object is not a list, or any element is not the required type
	 */
	public static final <V extends Serializable, T extends List<V>> T castList(Class<V> type, Supplier<T> generator,
			Object object) {
		T result = generator.get();
		
		for (Object obj : ((List<?>)object)) {
			result.add(type.cast(obj));
		}
		
		return result;
	}
	
	/**
	 * Writes the given list to the object stream.
	 * 
	 * @param <V> the type of the values
	 * @param list the list to serialize
	 * @param stream the object stream
	 * @throws IOException if an error occurred while writing the object
	 */
	public static final <V extends Serializable> void writeList(List<V> list, ObjectOutputStream stream)
			throws IOException {
		stream.writeObject(list);
	}
	
	/**
	 * Reads a list from the object stream.
	 * 
	 * @param <V> the type of the values
	 * @param <T> the return type
	 * @param type the expected type of each element in the list
	 * @param generator a supplier responsible for creating an empty list
	 * @param stream the object stream
	 * @return the typed list
	 * @throws IOException if an error occurred while writing the object
	 * @throws ClassNotFoundException if any type being serialized could not be found
	 */
	public static final <V extends Serializable, T extends List<V>> T readList(Class<V> type,
			Supplier<T> generator, ObjectInputStream stream) throws IOException, ClassNotFoundException {
		return castList(type, generator, stream.readObject());
	}
	
	/**
	 * Casts a map with the wildcard {@code <?, ?>} type to a typed map, ensuring each element is if the correct type.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param <T> the return type
	 * @param keyType the expected type of each key
	 * @param valueType the expected type of each value
	 * @param generator a supplier responsible for creating an empty map
	 * @param object the object, which is expected to be a map
	 * @return the typed map
	 * @throws ClassCastException if the object is not a map, or any element is not the required type
	 */
	public static final <K extends Serializable, V extends Serializable, T extends Map<K, V>> T castMap(
			Class<K> keyType, Class<V> valueType, Supplier<T> generator, Object object) {
		T result = generator.get();
		
		for (Entry<?, ?> entry : ((Map<?, ?>)object).entrySet()) {
			result.put(keyType.cast(entry.getKey()), valueType.cast(entry.getValue()));
		}
		
		return result;
	}
	
	/**
	 * Writes the given map to the object stream.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param map the map to serialize
	 * @param stream the object stream
	 * @throws IOException if an error occurred while writing the object
	 */
	public static final <K extends Serializable, V extends Serializable> void writeMap(Map<K, V> map,
			ObjectOutputStream stream) throws IOException {
		stream.writeObject(map);
	}
	
	/**
	 * Reads a map from the object stream.
	 * 
	 * @param <K> the type of the keys
	 * @param <V> the type of the values
	 * @param <T> the return type
	 * @param keyType the expected type of each key
	 * @param valueType the expected type of each value
	 * @param generator a supplier responsible for creating an empty map
	 * @param stream the object stream
	 * @return the typed map
	 * @throws IOException if an error occurred while writing the object
	 * @throws ClassNotFoundException if any type being deserialized could not be found
	 */
	public static final <K extends Serializable, V extends Serializable, T extends Map<K, V>> T readMap(
			Class<K> keyType, Class<V> valueType, Supplier<T> generator, ObjectInputStream stream) throws IOException,
			ClassNotFoundException {
		return castMap(keyType, valueType, generator, stream.readObject());
	}

}

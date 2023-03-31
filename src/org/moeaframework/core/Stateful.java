package org.moeaframework.core;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Interface for objects that can save and load their state.
 */
public interface Stateful {
	
	/**
	 * Writes the state of this object to the stream.  The order that objects are written to the stream
	 * is important.  We recommend first calling {@code super.saveState(stream)} followed by writing each
	 * field.
	 * 
	 * @param stream the stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void saveState(ObjectOutputStream stream) throws IOException {
		stream.writeObject(getState());
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
		setState(stream.readObject());
	}
	
	/**
	 * Returns a {@code Serializable} object representing the internal state of
	 * this algorithm.
	 * 
	 * @return a {@code Serializable} object representing the internal state of this algorithm
	 * @throws NotSerializableException if this algorithm does not support serialization
	 * @deprecated use {@link #loadState(ObjectInputStream)} instead
	 */
	@Deprecated
	public default Serializable getState() throws NotSerializableException {
		throw new NotSerializableException(getClass().getSimpleName());
	}

	/**
	 * Sets the internal state of of this algorithm.
	 * 
	 * @param state the internal state of this algorithm
	 * @throws NotSerializableException if this algorithm does not support serialization
	 * @deprecated use {@link #saveState(ObjectOutputStream)} instead
	 */
	@Deprecated
	public default void setState(Object state) throws NotSerializableException {
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

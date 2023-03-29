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
	 * Writes the state of this object to the stream.
	 * 
	 * @param stream the stream
	 * @throws IOException if an I/O error occurred
	 */
	public default void saveState(ObjectOutputStream stream) throws IOException {
		stream.writeObject(getState());
	}

	/**
	 * Loads the state of this object from the stream.
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
	 * @throws AlgorithmException if this algorithm has not yet been initialized
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
	 * @throws AlgorithmException if this algorithm has already been initialized
	 * @deprecated use {@link #saveState(ObjectOutputStream) instead
	 */
	@Deprecated
	public default void setState(Object state) throws NotSerializableException {
		throw new NotSerializableException(getClass().getSimpleName());
	}


}

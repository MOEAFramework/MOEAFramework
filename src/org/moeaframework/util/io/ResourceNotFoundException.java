package org.moeaframework.util.io;

import java.io.IOException;

/**
 * Similar to a {@link java.io.FileNotFoundException}, indicates that the resource was not found.
 */
public class ResourceNotFoundException extends IOException {

	private static final long serialVersionUID = -695410973076840905L;

	/**
	 * Constructs an exception indicating not resource was found with the given path.
	 * 
	 * @param owner the class attempting to load the resource
	 * @param resource the resource
	 */
	public ResourceNotFoundException(Class<?> owner, String resource) {
		super("Resource not found: " + resource + " (requestor: " + owner + ", resolved path: " +
				Resources.resolvePath(resource) + ")");
	}
	
}
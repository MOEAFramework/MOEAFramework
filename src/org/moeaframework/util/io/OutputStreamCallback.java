package org.moeaframework.util.io;

import java.io.OutputStream;

/**
 * Callback function accepting an {@link OutputStream}.
 */
@FunctionalInterface
public interface OutputStreamCallback extends IOCallback<OutputStream> {
	
}
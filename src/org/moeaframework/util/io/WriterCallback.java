package org.moeaframework.util.io;

import java.io.Writer;

/**
 * Callback function accepting a {@link Writer}.
 */
@FunctionalInterface
public interface WriterCallback extends IOCallback<Writer> {
	
}
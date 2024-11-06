package org.moeaframework.util.io;

import java.io.PrintStream;

/**
 * Callback function accepting a {@link PrintStream}.
 */
@FunctionalInterface
public interface PrintStreamCallback extends IOCallback<PrintStream> {
	
}
package org.moeaframework.util.io;

import java.io.Reader;

/**
 * Callback function accepting a {@link Reader}.
 */
@FunctionalInterface
public interface ReaderCallback extends IOCallback<Reader> {
	
}
package org.moeaframework.mock;

import java.io.IOException;
import java.io.StringWriter;

public class MockWriter extends StringWriter {

	private boolean isClosed;
	
	public MockWriter() {
		super();
	}

	@Override
	public void close() throws IOException {
		isClosed = true;
		super.close();
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
}

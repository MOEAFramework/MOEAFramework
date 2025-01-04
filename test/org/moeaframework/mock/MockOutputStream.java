package org.moeaframework.mock;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MockOutputStream extends ByteArrayOutputStream {

	private boolean isClosed;
	
	public MockOutputStream() {
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

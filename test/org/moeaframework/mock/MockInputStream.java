package org.moeaframework.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public class MockInputStream extends ByteArrayInputStream {

	private boolean isClosed;
	
	public MockInputStream(byte[] content) {
		super(content);
	}
	
	public MockInputStream(String content) {
		this(content.getBytes());
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

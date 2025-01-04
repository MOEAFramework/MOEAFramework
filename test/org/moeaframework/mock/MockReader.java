package org.moeaframework.mock;

import java.io.StringReader;

public class MockReader extends StringReader {

	private boolean isClosed;
	
	public MockReader(String content) {
		super(content);
	}

	@Override
	public void close() {
		isClosed = true;
		super.close();
	}
	
	public boolean isClosed() {
		return isClosed;
	}
	
}

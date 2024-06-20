package org.moeaframework.experiment;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;

import org.moeaframework.util.TypedProperties;
import org.moeaframework.util.io.CommentedLineReader;

public class Sample extends TypedProperties {
	
	public Sample() {
		super(TypedProperties.DEFAULT_SEPARATOR, true);
	}
	
	public Sample copy() {
		Sample copy = new Sample();
		copy.addAll(this);
		return copy;
	}
	
	@Override
	public void store(Writer writer) throws IOException {
		try (StringWriter stringBuffer = new StringWriter()) {
			super.store(stringBuffer);
			
			try (CommentedLineReader reader = CommentedLineReader.wrap(new StringReader(stringBuffer.toString()))) {
				reader.transferTo(writer);
			}
		}
	}

}

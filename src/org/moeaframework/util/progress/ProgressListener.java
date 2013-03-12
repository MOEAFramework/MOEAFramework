package org.moeaframework.util.progress;

import java.util.EventListener;

public interface ProgressListener extends EventListener {
	
	public void progressUpdate(ProgressEvent event);

}

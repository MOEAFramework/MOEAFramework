package org.moeaframework.util.progress;

import java.util.EventListener;

/**
 * Interface used to listen for progress reports provided by
 * {@link ProgressHelper}.
 */
public interface ProgressListener extends EventListener {
	
	/**
	 * Called when a new progress report is generated.
	 * 
	 * @param event the progress report
	 */
	public void progressUpdate(ProgressEvent event);

}

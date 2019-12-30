/* The following source code is copied from the Coco Framework available at
 * <https://github.com/numbbo/coco> under the 3-clause BSD license. The
 * original code is copyright 2013 by the NumBBO/CoCO team.  See the AUTHORS
 * file located in the Coco Framework repository for more details.
 */
package org.moeaframework.problem.BBOB2016;

public class Observer {
	
	private long pointer; // Pointer to the coco_observer_t object
	private String name;

	/** 
	 * Constructs the observer from observerName and observerOptions.
	 * TODO: Copy explanation of options from the C code. 
	 * @param observerName
	 * @param observerOptions
	 * @throws Exception
	 */
	public Observer(String observerName, String observerOptions) throws Exception {

		super();
		try {
			this.pointer = CocoJNI.cocoGetObserver(observerName, observerOptions);
			this.name = observerName;
		} catch (Exception e) {
			throw new Exception("Observer constructor failed.\n" + e.toString());
		}
	}

	/**
	 * Finalizes the observer.
	 * @throws Exception 
	 */
	public void finalizeObserver() throws Exception {
		try {
			CocoJNI.cocoFinalizeObserver(this.pointer);
		} catch (Exception e) {
			throw new Exception("Observer finalization failed.\n" + e.toString());
		}
	}

	public long getPointer() {
		return this.pointer;
	}
	
	public String getName() {
		return this.name;
	}

	/* toString method */
	@Override
	public String toString() {
		return getName();
	}
}
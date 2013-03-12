package org.moeaframework.util.progress;

import java.util.EventObject;

public class ProgressEvent extends EventObject {
	
	private static final long serialVersionUID = -1133068166971961110L;

	private final int currentSeed;
	
	private final int totalSeeds;
	
	private final int currentNFE;
	
	private final int maxNFE;
	
	private final double percentComplete;
	
	private final double elapsedTime;
	
	private final double remainingTime;

	public ProgressEvent(Object source, int currentSeed, int totalSeeds,
			int currentNFE, int maxNFE, double percentComplete, 
			double elapsedTime, double remainingTime) {
		super(source);
		this.currentSeed = currentSeed;
		this.totalSeeds = totalSeeds;
		this.currentNFE = currentNFE;
		this.maxNFE = maxNFE;
		this.percentComplete = percentComplete;
		this.elapsedTime = elapsedTime;
		this.remainingTime = remainingTime;
	}

	public int getCurrentSeed() {
		return currentSeed;
	}

	public int getTotalSeeds() {
		return totalSeeds;
	}

	public int getCurrentNFE() {
		return currentNFE;
	}

	public int getMaxNFE() {
		return maxNFE;
	}

	public double getPercentComplete() {
		return percentComplete;
	}

	public double getElapsedTime() {
		return elapsedTime;
	}

	public double getRemainingTime() {
		return remainingTime;
	}

}

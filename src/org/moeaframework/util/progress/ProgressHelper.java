package org.moeaframework.util.progress;

import org.apache.commons.lang3.event.EventListenerSupport;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class ProgressHelper {
	
	private final EventListenerSupport<ProgressListener> listeners;
	
	private final DescriptiveStatistics statistics;
	
	private final Object source;
	
	private int currentSeed;
	
	private int totalSeeds;
	
	private int currentNFE;
	
	private int maxNFE;
	
	private long startTime;
	
	private long lastTime;
	
	private int lastSeed;
	
	private int lastNFE;
	
	public ProgressHelper(Object source) {
		super();
		this.source = source;
		
		statistics = new DescriptiveStatistics(25);
		listeners = EventListenerSupport.create(ProgressListener.class);
	}
	
	public void addProgressListener(ProgressListener listener) {
		listeners.addListener(listener);
	}
	
	public void removeProgressListener(ProgressListener listener) {
		listeners.removeListener(listener);
	}
	
	private void updateStatistics() {
		long currentTime = System.currentTimeMillis();
		
		// update moving average
		double diffTime = currentTime - lastTime;
		double diffSeed = currentSeed - lastSeed;
		double diffNFE = currentNFE - lastNFE;
		double percentChange = (diffSeed + (diffNFE / maxNFE)) / totalSeeds;
		
		statistics.addValue(diffTime / percentChange);
		
		// finally, update the last values
		lastTime = currentTime;
		lastSeed = currentSeed;
		lastNFE = currentNFE;
	}
	
	private void sendProgressEvent() {
		long currentTime = System.currentTimeMillis();
		double remainingSeeds = totalSeeds - currentSeed;
		double remainingNFE = maxNFE - currentNFE;
		double percentRemaining = (remainingSeeds + (remainingNFE / maxNFE)) /
				totalSeeds;
		
		ProgressEvent event = new ProgressEvent(
				source,
				currentSeed,
				totalSeeds,
				currentNFE,
				maxNFE,
				Math.max(1.0 - percentRemaining, 0.0),
				(currentTime - startTime) / 1000.0,
				(statistics.getMean() * percentRemaining) / 1000.0);
		
		listeners.fire().progressUpdate(event);
	}
	
	public void setCurrentNFE(int currentNFE) {
		this.currentNFE = currentNFE;
		
		updateStatistics();
		sendProgressEvent();
	}
	
	public void setCurrentSeed(int currentSeed) {
		this.currentSeed = currentSeed;

		updateStatistics();
		sendProgressEvent();
	}
	
	public void nextSeed() {
		currentSeed++;
		currentNFE = 0;
	}
	
	public void start(int totalSeeds, int maxNFE) {
		this.totalSeeds = totalSeeds;
		this.maxNFE = maxNFE;
		
		// reset all internal parameters
		lastTime = startTime;
		lastSeed = 1;
		lastNFE = 0;
		currentSeed = 1;
		currentNFE = 0;
		statistics.clear();
		startTime = System.currentTimeMillis();
	}
	
	public void stop() {
		
	}
	
	public static void main(String[] args) {
		int totalSeeds = 10;
		int maxNFE = 100000;
		ProgressHelper helper = new ProgressHelper(ProgressHelper.class);
		
		helper.addProgressListener(new ProgressListener() {

			@Override
			public void progressUpdate(ProgressEvent event) {
				System.out.println(event.getRemainingTime());
			}
			
		});
		
		helper.start(totalSeeds, maxNFE);
		
		for (int i = 0; i < totalSeeds; i++) {
			for (int j = 0; j <= maxNFE-10000; j += 10000) {
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					//do nothing
				}
				
				helper.setCurrentNFE(j+10000);
			}
			
			helper.nextSeed();
		}
	}

}

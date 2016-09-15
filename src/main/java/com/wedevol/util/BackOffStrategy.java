package com.wedevol.util;

import java.util.Random;

/**
 * Util class for back off strategy
 */

public class BackOffStrategy {

	public static final int DEFAULT_RETRIES = 3;
	public static final long DEFAULT_WAIT_TIME_IN_MILLI = 1000;

	private int numberOfRetries;
	private int numberOfTriesLeft;
	private long defaultTimeToWait;
	private long timeToWait;
	private Random random = new Random();

	public BackOffStrategy() {
		this(DEFAULT_RETRIES, DEFAULT_WAIT_TIME_IN_MILLI);
	}

	public BackOffStrategy(int numberOfRetries, long defaultTimeToWait) {
		this.numberOfRetries = numberOfRetries;
		this.numberOfTriesLeft = numberOfRetries;
		this.defaultTimeToWait = defaultTimeToWait;
		this.timeToWait = defaultTimeToWait;
	}

	/**
	 * @return true if there are tries left
	 */
	public boolean shouldRetry() {
		return numberOfTriesLeft > 0;
	}

	public void errorOccured() throws Exception {
		numberOfTriesLeft--;
		if (!shouldRetry()) {
			throw new Exception("Retry Failed: Total of attempts: " + numberOfRetries + ". Total waited time: "
					+ timeToWait + "ms.");
		}
		waitUntilNextTry();
		timeToWait *= 2;
		// we add a random time (recommendation from google)
		timeToWait += random.nextInt(500);
	}

	private void waitUntilNextTry() {
		try {
			Thread.sleep(timeToWait);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public long getTimeToWait() {
		return this.timeToWait;
	}

	/**
	 * Reset back off state. Call this method after successful attempts if you
	 * want to reuse the class.
	 */
	public void reset() {
		this.numberOfTriesLeft = numberOfRetries;
		this.timeToWait = defaultTimeToWait;
	}

}

package org.bug.soundnotification;

/**
 * This is where the magic happens
 */
public class DelayCalculator {
	private int increment;
	private int limit;
	private int totalRepeats;
	private int totalDelays;

	private int next;
	private int previous;

	public void init(int start, int increment, int limit) {
		this.increment = increment;
		this.limit = limit;

		next = start;
		previous = 0;
		totalRepeats = 1;
		totalDelays = next;
	}

	public int nextDelay() {
		int delay = next;

		if (limit == 0 || (limit > 0 && totalDelays <= limit) || (limit < 0 && totalRepeats <= Math.abs(limit))) {
			switch (increment) {
				case -1: // double the interval
					next *= 2;
					break;
				case -2: // Fibonacci progression
					int tmp = next;
					next += previous;
					previous = tmp;
					break;
				default: // plain linear progression
					next += increment;
			}
			if (limit > 0) {
				totalDelays += next;
			}
			if (limit < 0) {
				totalRepeats++;
			}
		} else {
			delay = 0;
		}

		return delay;
	}
}

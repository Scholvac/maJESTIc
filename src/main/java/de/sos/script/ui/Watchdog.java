package de.sos.script.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Generalization of <code>ExecuteWatchdog</code>
 * 
 * @see org.apache.commons.exec.ExecuteWatchdog
 */
public class Watchdog implements Runnable {

	/**
	 * Interface for classes that want to be notified by Watchdog.
	 * 
	 * @see org.apache.commons.exec.Watchdog
	 */
	public interface WatchdogObserver {

		/**
		 * Called when the watchdow times out.
		 * 
		 * @param w
		 *            the watchdog that timed out.
		 */
		void timeoutOccured(Watchdog w);
	}
	
	private List<WatchdogObserver> observers = new ArrayList<>();

	private final long mTimeout;

	private boolean stopped = false;

	private long mUntil;
	
	

	public Watchdog(final long timeout, WatchdogObserver...observers) {
		if (timeout < 1) {
			throw new IllegalArgumentException("timeout must not be less than 1.");
		}
		this.mTimeout = timeout;
		if (observers != null && observers.length > 0) {
			for (WatchdogObserver ob : observers)
				addTimeoutObserver(ob);
		}
	}

	public void addTimeoutObserver(final WatchdogObserver wdo) {
		observers.add(wdo);
	}

	public void removeTimeoutObserver(final WatchdogObserver wdo) {
		observers.remove(wdo);
	}

	protected final void fireTimeoutOccured() {
		for (WatchdogObserver wdo : observers) {
			wdo.timeoutOccured(this);
		}
	}

	public synchronized void start() {
		stopped = false;
		Thread t = new Thread(this, "WATCHDOG");
		t.setDaemon(true);
		t.start();
	}

	public synchronized void stop() {
		stopped = true;
		notifyAll();
	}
	
	public void refresh() {
		refresh(mTimeout);
	}

	public void refresh(long newTimeout) {
		mUntil = System.currentTimeMillis() + newTimeout;
	}
	
	public synchronized void run() {
		mUntil = System.currentTimeMillis() + mTimeout;
		long now;
		while (!stopped && mUntil > (now = System.currentTimeMillis())) {
			try {
				wait(mUntil - now);
			} catch (InterruptedException e) {
			}
		}
		if (!stopped) {
			fireTimeoutOccured();
		}
	}
}



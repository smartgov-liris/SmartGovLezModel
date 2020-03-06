package org.liris.smartgov.lez.core.simulation;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.simulator.core.simulation.time.Clock;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;

public class ExtendedClock extends Clock {
	
	private final Logger logger = LogManager.getLogger(ExtendedClock.class);

	/**
	 * Increments this clock with the given amount of seconds,
	 * and triggers delayed actions that should occur during
	 * this time.
	 * 
	 * @param seconds seconds to add to the clock
	 */
	public void increment(double seconds) {
		super._increment(seconds);
		while(actions.size() > 0 && this.compareTo(actions.firstKey()) >= 0) {
			for(DelayedActionHandler handler : actions.pollFirstEntry().getValue()) {
				handler.getAction().trigger();
			}
		}
	}
	
	public void addDelayedAction(DelayedActionHandler action) {
		if(!actions.containsKey(action.getDate())) {
			actions.put(action.getDate(), new ArrayList<>());
		} 
		actions.get(action.getDate()).add(action);
	}
}

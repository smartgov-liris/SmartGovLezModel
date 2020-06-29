package org.liris.smartgov.lez.core.simulation;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.core.Agent;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.simulation.SimulationRuntime;
import org.liris.smartgov.simulator.core.simulation.time.Clock;

/**
 * Customized SimulationRuntime
 * @author alban
 *
 */
public class ExtendedSimulationRuntime extends SimulationRuntime {
	private final Logger logger = LogManager.getLogger(ExtendedSimulationRuntime.class);
	public ExtendedSimulationRuntime (SmartGovContext context) {
		super(context);
		clock = new Clock();
	}
	
	/**
	 * Allows to restart the temporal parts of the simulation.
	 * @param context current context
	 */
	public void restart(SmartGovContext context) {
		if (isRunning()) {
			throw new IllegalStateException("A Simulation is already running in this SmartGovRuntime.");
		}
		this.context = context;
		clock = new Clock();
		run = true;
		pause = false;
		tickCount = 0;
		simulationThread.resumeSimulation();
		triggerSimulationStartedListeners();
	}
	
	/**
	 * Stop a running simulation.
	 * 
	 * @throws IllegalStateException if no simulation is running.
	 */
	@Override
	public void stop() {
		if(!run) {
			throw new IllegalStateException("No simulation running.");
		}
		logger.info("Stop simulation after " + tickCount + " ticks.");
		pause = false;
		run = false;
		//clock.reset();
		triggerSimulationStoppedListeners();
	}
	
}
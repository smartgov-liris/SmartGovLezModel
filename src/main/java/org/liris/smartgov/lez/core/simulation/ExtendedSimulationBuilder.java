package org.liris.smartgov.lez.core.simulation;

import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.simulation.SimulationBuilder;
import org.liris.smartgov.lez.core.simulation.scenario.PollutionScenario;

public class ExtendedSimulationBuilder extends SimulationBuilder {
	public ExtendedSimulationBuilder(SmartGovContext context) {
		super(context);
	}
	
	@Override
	public void rebuild() {
		long beginTime = System.currentTimeMillis();

		((PollutionScenario)getContext().getScenario()).reloadWorld(getContext());
	}
}

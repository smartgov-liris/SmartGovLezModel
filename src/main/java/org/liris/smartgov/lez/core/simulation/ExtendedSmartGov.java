package org.liris.smartgov.lez.core.simulation;

import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.simulation.SimulationBuilder;
import org.liris.smartgov.simulator.core.simulation.SimulationRuntime;

public class ExtendedSmartGov extends SmartGov {
	public ExtendedSmartGov(SmartGovContext context) {
		super(context);
	}
	
	public ExtendedSmartGov(SmartGovContext context, SimulationRuntime smartGovRuntime) {
		super(context, smartGovRuntime);
	}
	
	public ExtendedSmartGov(SmartGovContext context, SimulationRuntime smartGovRuntime, SimulationBuilder simulationBuilder) {
		super(context, smartGovRuntime, simulationBuilder);
	}
	
	public void restart(SmartGovContext context) {
		this.context = context;
		((ExtendedSimulationRuntime)this.smartGovRuntime).restart(context);
		((ExtendedSimulationBuilder)this.simulationBuilder).rebuild();
	}
	
	
}

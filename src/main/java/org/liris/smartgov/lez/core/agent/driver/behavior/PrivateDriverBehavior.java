package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.Random;

import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

public abstract class PrivateDriverBehavior extends DriverBehavior {
	
	
	
	protected MoverAction nextAction;
	protected Random random;
	
	public PrivateDriverBehavior(
			DriverBody agentBody,
			Round round,
			SmartGovContext context,
			Random random
			) {
		super(agentBody, round, context);
		this.random = random;
	}

	@Override
	public MoverAction provideAction() {
		return nextAction;
	}

	@Override
	protected void triggerRoundDepartureListeners(RoundDeparture event) {
		for(EventHandler<RoundDeparture> listener : roundDepartureListeners) {
			listener.handle(event);
		}		
	}

	@Override
	protected void triggerRoundEndListeners(RoundEnd event) {
		for(EventHandler<RoundEnd> listener : roundEndListeners) {
			listener.handle(event);
		}		
	}	
	
}

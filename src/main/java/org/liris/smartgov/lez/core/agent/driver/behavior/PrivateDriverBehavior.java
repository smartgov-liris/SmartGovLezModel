package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.Random;

import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.driver.personality.Personality;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

/**
 * Generic class of behavior for private agents.
 * @author alban
 *
 */
public abstract class PrivateDriverBehavior extends DriverBehavior {
	
	protected MoverAction nextAction;
	protected Random random;
	
	/**
	 * 
	 * @param agentBody body of the agent
	 * @param round round to be performed
	 * @param personality personality associated to the agent
	 * @param context current context
	 * @param random an instantiated random
	 */
	public PrivateDriverBehavior(
			DriverBody agentBody,
			Round round,
			Personality personality,
			SmartGovContext context,
			Random random
			) {
		super(agentBody, round, personality,context);
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

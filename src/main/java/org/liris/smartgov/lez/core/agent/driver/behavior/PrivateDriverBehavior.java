package org.liris.smartgov.lez.core.agent.driver.behavior;

import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Lez;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;

public class PrivateDriverBehavior extends LezBehavior {
	
	private Round round;
	
	private MoverAction nextAction;
	
	public PrivateDriverBehavior(
			DriverBody agentBody,
			Round round,
			SmartGovContext context
			) {
		super(agentBody, round.getOrigin().getClosestOsmNode(), round.getEstablishments().get(0).getClosestOsmNode(), context, Lez.none());
		this.round = round;
	}

	@Override
	public MoverAction provideAction() {
		return nextAction;
	}
	
	public Round getRound() {
		return round;
	}

	@Override
	public void setUpListeners() {
		throw new UnsupportedOperationException("Not implmented yet");
		
	}
	
	
}

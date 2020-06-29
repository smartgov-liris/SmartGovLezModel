package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.ArrayList;
import java.util.Collection;

import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.driver.personality.Personality;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.events.EventHandler;
/**
 * The generic class of driver behaviors.
 * @author alban
 *
 */
public abstract class DriverBehavior extends LezBehavior {
	
	protected Round round;
	protected Collection<EventHandler<RoundDeparture>> roundDepartureListeners;
	protected Collection<EventHandler<RoundEnd>> roundEndListeners;
	protected Personality personality;
	
	/**
	 * DeliveryDriverBehavior constructor.
	 *
	 * @param agentBody associated body
	 * @param round round to perform
	 * @param personality personality associated to the agent
	 * @param context currentContext
	 */
	public DriverBehavior(DriverBody agentBody, Round round, Personality personality, SmartGovContext context) {
		super(
				agentBody,
				round.getOrigin().getClosestOsmNode(),
				round.getEstablishments().get(0).getClosestOsmNode(),
				context,
				Neighborhood.none());
		this.round = round;
		this.personality = personality;
		roundDepartureListeners = new ArrayList<>();
		roundEndListeners = new ArrayList<>();
	}
	
	/**
	 * Adds a round departure event handler, triggered when the departure
	 * date has been reached.
	 *
	 * @param listener round departure listener
	 */
	public void addRoundDepartureListener(EventHandler<RoundDeparture> listener) {
		this.roundDepartureListeners.add(listener);
	}
	
	/**
	 * Adds a round end event handler, triggered when the agent come back
	 * to the origin establishment.
	 *
	 * <p>
	 * Triggered when the agent reaches the destination, but does not
	 * guarantee that the agent as re-entered the establishment (what will
	 * be its next action.
	 * </p>
	 *
	 * @param listener round end listener
	 */
	public void addRoundEndListener(EventHandler<RoundEnd> listener) {
		this.roundEndListeners.add(listener);
	}
	
	/**
	 * return the round to be performed
	 * @return agent's round
	 */
	public Round getRound() {
		return round;
	}
	
	protected abstract void triggerRoundDepartureListeners(RoundDeparture event);
	protected abstract void triggerRoundEndListeners(RoundEnd event);
	public abstract void setUpListeners();
}

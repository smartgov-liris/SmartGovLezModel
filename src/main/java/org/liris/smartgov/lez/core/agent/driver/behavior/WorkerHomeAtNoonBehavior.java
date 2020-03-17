package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.Random;

import org.liris.smartgov.lez.cli.tools.Run;
import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

public class WorkerHomeAtNoonBehavior extends PrivateDriverBehavior {
	private int position;
	
	public WorkerHomeAtNoonBehavior(
			DriverBody agentBody,
			Round round,
			SmartGovContext context,
			Random random
			) {
	
		super(agentBody,
				round,
				context,
				random);
		
		if (round.getEstablishments() == null || round.getEstablishments().size() == 0) {
			throw new IllegalArgumentException("This behavior needs one establishment in its round");
		}
		
		position = 0;
		this.nextAction = MoverAction.ENTER(round.getOrigin());
	}

	@Override
	public void setUpListeners() {
		// After the agents leave the parking, it moves until it finished the round
		((DriverBody) getAgentBody()).addOnParkingLeftListener((event) ->
			nextAction = MoverAction.MOVE()
			);
		
		// When the agent enter the parking, it waits
		((DriverBody) getAgentBody()).addOnParkingEnteredListener((event) ->
			nextAction = MoverAction.WAIT()
			);
		
		//goes to work between 7h and 8h59
		Date departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 7, random.nextInt(60));
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " leaves his home "
								);

						nextAction = MoverAction.LEAVE(round.getOrigin());
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves work for noon between 11h and 11h29
		departure = new Date(0, WeekDay.MONDAY, 11, random.nextInt(30));
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " left work to eat "
								);
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(0));
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//goes back to work between 13h30 and 13h59
		departure = new Date(0, WeekDay.MONDAY, 13, random.nextInt(30) + 30);
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " goes back to work "
								);
						if ( position != 2 ) {
							//for now, we give him a new place to go to even if he didn't reach the last one
							position++;
							refresh(round.getOrigin().getClosestOsmNode(),
									round.getEstablishments().get(0).getClosestOsmNode());
						}
						nextAction = MoverAction.LEAVE(round.getOrigin());
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves work between 17h and 18h59
		departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 17, random.nextInt(60));
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " left work "
								);
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(0));
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		((DriverBody) getAgentBody()).addOnDestinationReachedListener((event) -> {
			if (position == 0) {
				//he arrived at work in the morning
				Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " arrived at work "
						);
				refresh(round.getEstablishments().get(0).getClosestOsmNode(),
						round.getOrigin().getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(0));
				position += 1;
			} else if (position == 1) {
				//he is home at noon
				Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " is home to eat "
						);
				refresh(round.getOrigin().getClosestOsmNode(),
						round.getEstablishments().get(0).getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getOrigin());
				position += 1;
			} else if (position == 2) {
				//he is back at work
				Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " arrived at work for the afternoon "
						);
				refresh(round.getEstablishments().get(0).getClosestOsmNode(),
						round.getOrigin().getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(0));
				position += 1;
			} else {
				//he is home, he ended his day
				Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " is back home "
						);
				refresh(round.getOrigin().getClosestOsmNode(),
						round.getEstablishments().get(0).getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getOrigin());
				position += 1;
			}
		});
		
	}
}

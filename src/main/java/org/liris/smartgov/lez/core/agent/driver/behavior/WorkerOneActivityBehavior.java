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

public class WorkerOneActivityBehavior extends PrivateDriverBehavior {
	private int position;
	
	public WorkerOneActivityBehavior(
			DriverBody agentBody,
			Round round,
			SmartGovContext context,
			Random random
			) {
	
		super(agentBody,
				round,
				context,
				random);
		
		if (round.getEstablishments().size() < 2) {
			throw new IllegalArgumentException("This behavior needs two establishments in his round");
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
		
		//the departure is between 7h and 8h59
		Date departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 7, random.nextInt(60));
		
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
							Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
									+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
									+ "Agent " + getAgentBody().getAgent().getId()
									+ " leaves his home "
									);
						}
						nextAction = MoverAction.LEAVE(round.getOrigin());
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves work between 16h and 17h59
		departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 16, random.nextInt(60));
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						
						if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
							Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
									+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
									+ "Agent " + getAgentBody().getAgent().getId()
									+ " left work "
									);
						}
						if ( position != 1 ) {
							throw new IllegalStateException("Agent received a new place to go but he did not reach the previous one");
						}
						
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(0));
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves his activity between 19h and 20h59
		departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 19, random.nextInt(60));
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						
						if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
							Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
									+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
									+ "Agent " + getAgentBody().getAgent().getId()
									+ " left work "
									);
						}
						if ( position != 2 ) {
							//we could implement another behavior in this case instead of throwing an exception
							throw new IllegalStateException("Agent received a new place to go but he did not reach the previous one");
						}
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(1));
						triggerRoundDepartureListeners(new RoundDeparture());
						
					}
					)
			);
		
		
		((DriverBody) getAgentBody()).addOnDestinationReachedListener((event) -> {
			if ( position == 0 ) {
				//he arrives to work
				if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
					Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
							+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
							+ "Agent " + getAgentBody().getAgent().getId()
							+ " arrived at work "
							);
				}
				refresh(round.getEstablishments().get(0).getClosestOsmNode(),
						round.getEstablishments().get(1).getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(0));
				position += 1;
			} else  if (position == 1){
				//he just arrived at his activity
				if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
					Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
							+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
							+ "Agent " + getAgentBody().getAgent().getId()
							+ " arrived at his activity "
							);
				}
				
				refresh(round.getEstablishments().get(1).getClosestOsmNode(),
						round.getOrigin().getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(1));
				position += 1;
				
			}
			else {
				//he is back home
				if (Integer.parseInt(getAgentBody().getAgent().getId()) <= 100) {
					Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
							+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
							+ "Agent " + getAgentBody().getAgent().getId()
							+ " is back home "
							);
				}
				nextAction = MoverAction.ENTER(round.getOrigin());
				position += 1;
				triggerRoundEndListeners(new RoundEnd());
			}
		});
		
	}
}

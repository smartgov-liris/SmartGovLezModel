package org.liris.smartgov.lez.core.agent.driver.behavior;

import java.util.Random;

import org.liris.smartgov.lez.cli.tools.Run;
import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.driver.personality.Personality;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.simulation.ExtendedDate;
import org.liris.smartgov.lez.core.simulation.files.FilePath;
import org.liris.smartgov.lez.core.simulation.files.FilesManagement;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.agent.moving.behavior.MoverAction;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.DelayedActionHandler;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

/**
 * Behavior of worker home at noon private agent.
 * His behavior is : 
 * <ul>
 * 	<li> Leaves his origin establishment between 7am and 9am and to go to work.</li>
 * 	<li> Leaves his work between 11am and 11:30 to go home.</li>
 * 	<li> Leaves his origin establishment between 1:30pm and 2pm to go to work.</li>
 * 	<li> Leaves his work between 5pm and 7 pm.
 * </ul> 
 * @author alban
 *
 */
public class WorkerHomeAtNoonBehavior extends PrivateDriverBehavior {
	private int position;
	private int journeyTime;
	private Date[] departures;
	
	/**
	 * WorkerHomeAtNoonBehavior constructor.
	 *
	 * @param agentBody associated body
	 * @param round round to perform
	 * @param personality personality associated to the agent
	 * @param context currentContext
	 * @param random an instantiated random
	 */
	public WorkerHomeAtNoonBehavior(
			DriverBody agentBody,
			Round round,
			Personality personality,
			SmartGovContext context,
			Random random
			) {
	
		super(agentBody,
				round,
				personality,
				context,
				random);
		
		departures = new Date[4];
		journeyTime = 0;
		
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
		departures[0] = departure;
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " leaves his home "
								);*/

						nextAction = MoverAction.LEAVE(round.getOrigin());
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves work for noon between 11h and 11h29
		departure = new Date(0, WeekDay.MONDAY, 11, random.nextInt(30));
		departures[1] = departure;
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " left work to eat "
								);*/
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(0));
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//goes back to work between 13h30 and 13h59
		departure = new Date(0, WeekDay.MONDAY, 13, random.nextInt(30) + 30);
		departures[2] = departure;
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " goes back to work "
								);*/
						if ( position != 2 ) {
							//for now we just throw an exception, but we could think about another behavior
							throw new IllegalStateException ("Agent " + getAgentBody().getAgent().getId() + " received a new place to go "
									+ "but he did not reach the last one");
						}
						nextAction = MoverAction.LEAVE(round.getOrigin());
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		//leaves work between 17h and 18h59
		departure = new Date(0, WeekDay.MONDAY, random.nextInt(2) + 17, random.nextInt(60));
		departures[3] = departure;
		SmartGov
		.getRuntime()
		.getClock()
		.addDelayedAction(
			new DelayedActionHandler(
					departure,
					() -> {
						/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
								+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
								+ "Agent " + getAgentBody().getAgent().getId()
								+ " left work "
								);*/
						nextAction = MoverAction.LEAVE(round.getEstablishments().get(0));
						triggerRoundDepartureListeners(new RoundDeparture());
					}
					)
			);
		
		((DriverBody) getAgentBody()).addOnDestinationReachedListener((event) -> {
			if (position == 0) {
				//he arrived at work in the morning
				/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " arrived at work "
						);*/
				refresh(round.getEstablishments().get(0).getClosestOsmNode(),
						round.getOrigin().getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(0));
				position += 1;
				
			} else if (position == 1) {
				//he is home at noon
				/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " is home to eat "
						);*/
				refresh(round.getOrigin().getClosestOsmNode(),
						round.getEstablishments().get(0).getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getOrigin());
				position += 1;
			} else if (position == 2) {
				//he is back at work
				/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " arrived at work for the afternoon "
						);*/
				refresh(round.getEstablishments().get(0).getClosestOsmNode(),
						round.getOrigin().getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getEstablishments().get(0));
				position += 1;
			} else {
				//he is home, he ended his day
				/*Run.logger.info("[" + SmartGov.getRuntime().getClock().getHour()
						+ ":" + SmartGov.getRuntime().getClock().getMinutes() + "]"
						+ "Agent " + getAgentBody().getAgent().getId()
						+ " is back home "
						);*/
				refresh(round.getOrigin().getClosestOsmNode(),
						round.getEstablishments().get(0).getClosestOsmNode());
				nextAction = MoverAction.ENTER(round.getOrigin());
				position += 1;
			}
			journeyTime += ExtendedDate.getTimeBetween(departures[position - 1], SmartGov.getRuntime().getClock().time());
			if (position == 4) {
				personality.giveTime(journeyTime);
				personality.computeSatisfactionOfAgent();
				personality.giveSatisfactionToNeighborhoods();
				triggerRoundEndListeners(new RoundEnd());
			}
		});
		
	}
}

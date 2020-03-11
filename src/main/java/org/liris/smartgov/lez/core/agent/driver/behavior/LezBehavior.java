package org.liris.smartgov.lez.core.agent.driver.behavior;


import org.liris.smartgov.lez.core.agent.driver.DriverBody;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.simulator.core.environment.graph.Node;
import org.liris.smartgov.simulator.urban.geo.agent.behavior.GeoMovingBehavior;
import org.liris.smartgov.simulator.core.events.EventHandler;

/**
 * Abstract behavior that describes the behavior of an agent
 * moving in an urban environment including a Low Emission
 * Zone.
 */
public abstract class LezBehavior extends GeoMovingBehavior {
	
	private Round round;

	/**
	 * LezBehavior constructor.
	 * <p>
	 * The costs associated to arcs for this behavior are
	 * retrieved from the {@link org.liris.smartgov.lez.core.environment.lez.Neighborhood#costs}
	 * function, applied to the delivery driver's current vehicle.
	 * </p>
	 *
	 * @param agentBody delivery driver body
	 * @param origin initial origin
	 * @param destination initial destination
	 * @param context current context
	 * @param neighborhood current lez
	 */
	public LezBehavior(
			DriverBody agentBody,
			Node origin,
			Node destination,
			SmartGovContext context,
			Neighborhood neighborhood
			) {
		super(agentBody, origin, destination, context, neighborhood.costs(agentBody.getVehicle()));
	}

}

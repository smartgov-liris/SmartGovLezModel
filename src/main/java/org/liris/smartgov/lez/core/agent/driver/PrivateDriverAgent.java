package org.liris.smartgov.lez.core.agent.driver;

import org.liris.smartgov.lez.core.agent.driver.behavior.PrivateDriverBehavior;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgent;

/**
 * An OSM agent associated to an establishment, that can perform journeys
 * with a DriverBody through a PrivateDriverBehavior.
 * @author alban
 *
 */
public class PrivateDriverAgent extends OsmAgent {
	
	//establishment where the driver goes to work
	private Establishment establishment;
	
	/**
	 * 
	 * @param id agent id
	 * @param body agent body
	 * @param privateDriverBehavior agent behavior
	 */
	public PrivateDriverAgent(
			String id,
			DriverBody body,
			PrivateDriverBehavior privateDriverBehavior) {
		super(id, body, privateDriverBehavior);
		this.establishment = privateDriverBehavior.getRound().getOrigin();
		establishment.addAgent(this);
	}
	
	/**
	 * Returns this agent's establishment, initialized from the specified
	 * PrivateDriverBehavior.
	 *
	 * @return agent's establishment
	 */
	public Establishment getEstablishment() {
		return establishment;
	}
}

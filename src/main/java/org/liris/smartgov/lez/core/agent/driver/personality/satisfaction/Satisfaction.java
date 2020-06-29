package org.liris.smartgov.lez.core.agent.driver.personality.satisfaction;

/**
 * Generic class of satisfaction.
 * @author alban
 *
 */
public abstract class Satisfaction {
	public Satisfaction () {
		
	}
	
	public abstract double getSatisfaction (boolean changedMobility, boolean hasFrauded, boolean changedVehicle, int time, int price);
}

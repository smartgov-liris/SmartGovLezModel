package org.liris.smartgov.lez.core.environment.lez.criteria;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

/**
 * A LezCriteria implementation that allows all the vehicles.
 *
 */
public class AllAllowedCriteria implements LezCriteria {

	/**
	 * Always returns true.
	 * 
	 * @param vehicle vehicle
	 * @return true
	 */
	@Override
	public boolean isAllowed(Vehicle vehicle) {
		return true;
	}

}

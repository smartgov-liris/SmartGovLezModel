package org.liris.smartgov.lez.core.environment.lez.criteria;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

/**
 * A LezCriteria implementation that forbids all the vehicles.
 *
 */
public class NothingAllowedCriteria implements LezCriteria {

	/**
	 * Always returns false.
	 * 
	 * @param vehicle vehicle
	 * @return false
	 */
	@Override
	public boolean isAllowed(Vehicle vehicle) {
		return false;
	}

}

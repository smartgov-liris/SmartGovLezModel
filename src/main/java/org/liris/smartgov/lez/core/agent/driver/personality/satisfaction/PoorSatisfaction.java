package org.liris.smartgov.lez.core.agent.driver.personality.satisfaction;

public class PoorSatisfaction extends Satisfaction {

	@Override
	public double getSatisfaction(boolean changedMobility, boolean hasFrauded, boolean changedVehicle, int time,
			int price) {
		int vehicle = changedVehicle ? -5 : 0;
		int mobility = changedMobility ? -3 : 0;
		int fraud = hasFrauded ? -1 : 0;
		return mobility + vehicle + fraud - ((double)(time/3600)) - price * 2;
	}
}

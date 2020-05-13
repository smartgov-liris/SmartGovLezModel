package org.liris.smartgov.lez.core.agent.driver.personality.satisfaction;

public class RichSatisfaction extends Satisfaction {

	@Override
	public double getSatisfaction(boolean changedMobility, boolean hasFrauded, boolean changedVehicle, int time,
			int price) {
		int mobility = changedMobility ? -3 : 0;
		int vehicle = changedVehicle ? -1 : 0;
		int fraud = hasFrauded ? -1 : 0;
		return mobility + vehicle + fraud - ((double)(time/900)) - (price/3);
	}

}

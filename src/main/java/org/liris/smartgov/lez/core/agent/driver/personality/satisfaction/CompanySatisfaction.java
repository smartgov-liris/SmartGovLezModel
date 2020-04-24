package org.liris.smartgov.lez.core.agent.driver.personality.satisfaction;

public class CompanySatisfaction extends Satisfaction {

	@Override
	public double getSatisfaction(boolean changedMobility, boolean hasFrauded, boolean changedVehicle, int time,
			int price) {
		int vehicle = changedVehicle ? -5 : 0;
		int fraud = hasFrauded ? -1 : 0;
		return vehicle + fraud - ((double)(time/1800)) - price;
	}

}

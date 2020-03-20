package org.liris.smartgov.lez.core.agent.driver.personality;

public class Satisfaction {
	public Satisfaction () {
		
	}
	
	public int getSatisfaction (boolean changedMobility, boolean hasFrauded, boolean changedVehicle, int time) {
		int mobility = changedMobility ? 3 : 0;
		int vehicle = changedVehicle ? 2 : 0;
		int fraud = hasFrauded ? 1 : 0;
		return mobility + vehicle + fraud + (time/3600);
	}
}

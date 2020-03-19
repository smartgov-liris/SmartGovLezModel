package org.liris.smartgov.lez.core.agent.establishment.personality;

public class Satisfaction {
	public Satisfaction () {
		
	}
	
	public int getSatisfaction (int changedMobility, int nbFraud, int changedVehicle, int time) {
		return time + 2 * changedMobility + nbFraud + 3*changedVehicle;
	}
}

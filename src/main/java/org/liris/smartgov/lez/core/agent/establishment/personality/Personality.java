package org.liris.smartgov.lez.core.agent.establishment.personality;

import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.Choice;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.CompanyChoice;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.PrivateChoice;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class Personality {
	private Choice choice;
	private Satisfaction satisfaction;
	private int changedVehicle;
	private int nbFraud;
	private int changedMobility;
	private int totalTimeLost;
	
	public Personality (ST8 activity) {
		if (activity != ST8.PRIVATE_HABITATION) {
			choice = new PrivateChoice();
		}
		else {
			choice = new CompanyChoice();
		}
		changedVehicle = 0;
		nbFraud = 0;
		changedMobility = 0;
	}
	
	public Decision getDecision(Surveillance surveillance, int placesVehicleForbidden) {
		return choice.getDecision(surveillance, placesVehicleForbidden);
	}
	
	public void increaseVehicleChanged() {
		changedVehicle++;
	}
	
	public void increaseMobilityChanged() {
		changedMobility ++;
	}
	
	public void increaseNbFraud() {
		nbFraud++;
	}
	
	public void increaseTimeLost(int timeLost) {
		totalTimeLost += timeLost;
	}
	
	public int getSatisfactionOfEstablishment() {
		return satisfaction.getSatisfaction(changedMobility, nbFraud, changedVehicle, totalTimeLost);
	}
}

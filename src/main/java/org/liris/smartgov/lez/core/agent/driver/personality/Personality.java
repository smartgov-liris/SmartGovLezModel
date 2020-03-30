package org.liris.smartgov.lez.core.agent.driver.personality;

import org.liris.smartgov.lez.core.agent.driver.personality.choice.Choice;
import org.liris.smartgov.lez.core.agent.driver.personality.choice.CompanyChoice;
import org.liris.smartgov.lez.core.agent.driver.personality.choice.PrivateChoice;
import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class Personality {
	private Choice choice;
	private Satisfaction satisfaction;
	private boolean changedVehicle;
	private boolean hasFrauded;
	private boolean changedMobility;
	private int price;
	private int initialTime;
	private int timeLost;
	private String vehicleId;
	
	public Personality (ST8 activity, String vehicleId) {
		if (activity != ST8.PRIVATE_HABITATION) {
			choice = new PrivateChoice();
		}
		else {
			choice = new CompanyChoice();
		}
		changedVehicle = false;
		hasFrauded = false;
		changedMobility = false;
		this.vehicleId = vehicleId;
		initialTime = 0;
		timeLost = 0;
		price = 0;
	}
	
	public Decision getDecision(Surveillance surveillance, int placesVehicleForbidden) {
		Decision decision = choice.getDecision(surveillance, placesVehicleForbidden);
		if (decision != Decision.CHANGE_MOBILITY) {
			if (surveillance == Surveillance.CHEAP_TOLL) {
				price = 1;
			}
			if (surveillance == Surveillance.MEDIUM_TOLL) {
				price = 2;
			}
			if (surveillance == Surveillance.EXPENSIVE_TOLL) {
				price = 3;
			}
		}
		return decision;
	}
	
	public String getVehicleId() {
		return vehicleId;
	}
	
	public void changeVehicle() {
		changedVehicle = true;
	}
	
	public void changeMobility() {
		changedMobility = true;
	}
	
	public void fraud() {
		hasFrauded = true;
	}
	
	public void giveTime(int time) {
		if (initialTime == 0) {
			//if he has nothing to compare his time
			initialTime = time;
		}
		else {
			timeLost = initialTime - time;
		}
	}
	
	public int getSatisfactionOfAgent() {
		return satisfaction.getSatisfaction(changedMobility, hasFrauded, changedVehicle, timeLost, price);
	}
}

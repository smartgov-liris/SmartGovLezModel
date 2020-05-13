package org.liris.smartgov.lez.core.agent.driver.personality;

import java.util.List;

import org.liris.smartgov.lez.core.agent.driver.personality.choice.Choice;
import org.liris.smartgov.lez.core.agent.driver.personality.choice.CompanyChoice;
import org.liris.smartgov.lez.core.agent.driver.personality.choice.PrivateChoice;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.CompanySatisfaction;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.MediumSatisfaction;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.PoorSatisfaction;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.PrivateSatisfaction;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.RichSatisfaction;
import org.liris.smartgov.lez.core.agent.driver.personality.satisfaction.Satisfaction;
import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.CasesManager;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
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
	private double satisfactionScore;
	private String vehicleId;
	private List<Neighborhood> causeNeighborhoods;
	private Cases cases;
	ST8 activity;
	
	
	public Personality (ST8 activity, String vehicleId, PersonalityType type) {
		this.activity = activity;
		if (activity == ST8.PRIVATE_HABITATION) {
			choice = new PrivateChoice();
			/*if (type == PersonalityType.POOR) {
				satisfaction = new PoorSatisfaction();
			}
			else if (type == PersonalityType.MEDIUM) {
				satisfaction = new MediumSatisfaction();
			}
			else {
				satisfaction = new RichSatisfaction();
			}*/
			satisfaction = new PrivateSatisfaction();
		}
		else {
			choice = new CompanyChoice();
			satisfaction = new CompanySatisfaction();
		}
		changedVehicle = false;
		hasFrauded = false;
		changedMobility = false;
		this.vehicleId = vehicleId;
		initialTime = 0;
		timeLost = 0;
		price = 0;
	}
	
	public void resetPersonality() {
		hasFrauded = false;
		changedMobility = false;
		changedVehicle = false;
		timeLost = 0;
		price = 0;
		satisfactionScore = 0;
	}
	
	public void setCase(Cases c) {
		cases = c;
	}
	
	public Cases getCase() {
		return cases;
	}
	
	public Decision getDecision() {
		int[] counters = CasesManager.getCounters(activity, cases);
		CasesManager.addCase(cases, true, activity);
		double proportion = ((double)counters[1]) / ((double)counters[0]);
		
		Decision decision = choice.getDecision(cases, proportion);
		if (decision != Decision.CHANGE_MOBILITY) {
			if (cases == Cases.ALLOWED_CHEAP_TOLL || cases == Cases.FORBIDDEN_CHEAP_TOLL) {
				price = 1;
			}
			if (cases == Cases.ALLOWED_EXPENSIVE_TOLL || cases == Cases.FORBIDDEN_EXPENSIVE_TOLL) {
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
	
	public void setCauseNeighborhoods ( List<Neighborhood> causeNeighborhoods ) {
		this.causeNeighborhoods = causeNeighborhoods;
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
	
	public void computeSatisfactionOfAgent() {
		satisfactionScore = satisfaction.getSatisfaction(changedMobility, hasFrauded, changedVehicle, timeLost, price);
	}
	
	public void giveSatisfactionToNeighborhoods() {
		for ( Neighborhood neighborhood : causeNeighborhoods) {
			neighborhood.giveSatisfaction( (double) satisfactionScore / causeNeighborhoods.size(), changedVehicle, changedMobility, hasFrauded );
		}
	}
	
	
}

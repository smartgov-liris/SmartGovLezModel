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

/**
 * Personality of agents. Two main parts in this personality :
 * The choice that will be made considering the policies, and the satisfaction
 * of the agent.
 * @author alban
 *
 */
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
	
	/**
	 * Constructor of Personality
	 * @param activity activity of the agent, delivery or private.
	 * @param vehicleId id of the vehicle
	 */
	public Personality (ST8 activity, String vehicleId) {
		this.activity = activity;
		if (activity == ST8.PRIVATE_HABITATION) {
			choice = new PrivateChoice();
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
	
	/**
	 * Reset all the variables that compute satisfaction.
	 */
	public void resetPersonality() {
		hasFrauded = false;
		changedMobility = false;
		changedVehicle = false;
		timeLost = 0;
		price = 0;
		satisfactionScore = 0;
	}
	
	/**
	 * Gives the agent's perception of the policy.
	 * @param c case perceived by the agent to make his choice.
	 */
	public void setCase(Cases c) {
		cases = c;
	}
	
	/**
	 * Returns the case perceived by the agent of the policy.
	 * @return case.
	 */
	public Cases getCase() {
		return cases;
	}
	
	/**
	 * Gives the decision of the agent, made by his choice object.
	 * @return the decision.
	 */
	public Decision getDecision() {
		int[] counters = CasesManager.getCounter(activity, cases);
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
	
	/**
	 * Returns the vehicle id.
	 * @return vehicle id.
	 */
	public String getVehicleId() {
		return vehicleId;
	}
	
	/**
	 * Inform the personality that the agent changed his vehicle.
	 */
	public void changeVehicle() {
		changedVehicle = true;
	}
	
	/**
	 * Inform the personality that the agent changed mobility.
	 */
	public void changeMobility() {
		changedMobility = true;
	}
	
	/**
	 * Inform the personality that the agent frauded.
	 */
	public void fraud() {
		hasFrauded = true;
	}
	
	/**
	 * Set the neighborhoods that caused the decision, and then will receive the satisfaction.
	 */
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
	
	/**
	 * Compute satisfaction considering the choices made.
	 */
	public void computeSatisfactionOfAgent() {
		satisfactionScore = satisfaction.getSatisfaction(changedMobility, hasFrauded, changedVehicle, timeLost, price);
	}
	
	/**
	 * Give the satisfaction to the neighborhoods that caused the decision.
	 */
	public void giveSatisfactionToNeighborhoods() {
		for ( Neighborhood neighborhood : causeNeighborhoods) {
			neighborhood.giveSatisfaction( (double) satisfactionScore / causeNeighborhoods.size(), changedVehicle, changedMobility, hasFrauded );
		}
	}
	
	
}

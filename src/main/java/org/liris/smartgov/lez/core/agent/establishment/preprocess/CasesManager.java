package org.liris.smartgov.lez.core.agent.establishment.preprocess;

import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

/**
 * Counts all the agents in all cases in order to create proportions.
 * @author alban
 *
 */
public class CasesManager {
	static Map<Cases, Map<String, int[]>> casesCounter = new HashMap<>();
	
	/**
	 * Initialise the counters.
	 */
	public static void init() {
		for (Cases c : Cases.values()) {
			Map<String, int[]> type = new HashMap<>();
			type.put("Private", new int[2]);
			type.put("Deliverie", new int[2]);
			casesCounter.put(c, type);
		}
	}
	
	/**
	 * Returns the counter for a case and an activity.
	 * @param activity agent activity.
	 * @param c considered case.
	 * @return corresponding counter.
	 */
	public static int[] getCounter(ST8 activity, Cases c) {
		if (activity == ST8.PRIVATE_HABITATION) {
			return casesCounter.get(c).get("Private");
		}
		else {
			return casesCounter.get(c).get("Deliverie");
		}
	}
	
	/**
	 * Add case to the counters, defining if the agent made his choice, or not yet.
	 * @param c considered case.
	 * @param hasChosen says if the agent has made his choice.
	 * @param activity activity of the agent.
	 */
	public static void addCase (Cases c, boolean hasChosen, ST8 activity) {
		int caseToIncrease;
		if (! hasChosen) {
			caseToIncrease = 0;
		}
		else {
			caseToIncrease = 1;
		}
		String type;
		if (activity == ST8.PRIVATE_HABITATION) {
			type = "Private";
		}
		else {
			type = "Deliverie";
		}
		
		casesCounter.get(c).get(type)[caseToIncrease] += 1;
	}
	
	/**
	 * Add case to the counters, defining if the agent made his choice, or not yet.
	 * 
	 * @param surveillance allows to compute the case.
	 * @param forbidden allows to compute the case.
	 * @param hasChosen says if the agent has made his choice.
	 * @param activity activity of the agent.
	 */
	public static void addCase (Surveillance surveillance, boolean forbidden, boolean hasChosen, ST8 activity) {
		addCase(getCase(surveillance, forbidden), hasChosen, activity);
	}
	
	/**
	 * Returns the case considering a surveillance, and the fact that agent's vehicle is forbidden or not.
	 * @param surveillance the surveillance.
	 * @param forbidden whether or not the vehicle is forbiddden.
	 * @return corresponding case.
	 */
	public static Cases getCase(Surveillance surveillance, boolean forbidden) {
		if (forbidden) {
			switch (surveillance) {
			case NO_SURVEILLANCE:
				return Cases.FORBIDDEN_NO_SURVEILLANCE;
			case PATROL:
				return Cases.FORBIDDEN_PATROL;
			case CAMERA:
				return Cases.FORBIDDEN_CAMERA;
			case BARRIER:
				return Cases.FORBIDDEN_BARRIER;
			case CHEAP_TOLL:
				return Cases.FORBIDDEN_CHEAP_TOLL;
			case EXPENSIVE_TOLL:
				return Cases.FORBIDDEN_EXPENSIVE_TOLL;
			default :
				//will not happen
				return Cases.FORBIDDEN_NO_SURVEILLANCE;
			}
		}
		else {
			switch (surveillance) {
			case CHEAP_TOLL:
				return Cases.ALLOWED_CHEAP_TOLL;
			case EXPENSIVE_TOLL:
				return Cases.ALLOWED_EXPENSIVE_TOLL;
			default:
				return Cases.ALLOWED_NO_TOLL;
			}
		}
	}
}

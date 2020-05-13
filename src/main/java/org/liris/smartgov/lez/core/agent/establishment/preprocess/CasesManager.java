package org.liris.smartgov.lez.core.agent.establishment.preprocess;

import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class CasesManager {
	static Map<Cases, Map<String, int[]>> casesCounter = new HashMap<>();
	
	public static void init() {
		for (Cases c : Cases.values()) {
			Map<String, int[]> type = new HashMap<>();
			type.put("Private", new int[2]);
			type.put("Deliverie", new int[2]);
			casesCounter.put(c, type);
		}
	}
	
	public static int[] getCounters(ST8 activity, Cases c) {
		if (activity == ST8.PRIVATE_HABITATION) {
			return casesCounter.get(c).get("Private");
		}
		else {
			return casesCounter.get(c).get("Deliverie");
		}
	}
	
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
	
	public static void addCase (Surveillance surveillance, boolean forbidden, boolean hasChosen, ST8 activity) {
		addCase(getCase(surveillance, forbidden), hasChosen, activity);
	}
	
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

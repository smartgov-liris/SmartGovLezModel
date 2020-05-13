package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class PrivateChoice extends Choice {
	
	/*protected boolean wantToChangeMobility(Surveillance surveillance, boolean hasToChange) {
		if (hasToChange) {
			//if he does not want to change vehicle, he changes mobility
			if (Math.random() < 0.2) {
				return true;
			}
		}
		
		//we then check if he accepts to pay tolls
		if (surveillance == Surveillance.CHEAP_TOLL)
			return Math.random() < 0.33;
		else if (surveillance == Surveillance.EXPENSIVE_TOLL){
			return Math.random() < 0.66;
		}
		//if there is no toll he does not need to change mobility
		return false;
	}
	
	protected boolean wantToFraud(Surveillance surveillance) {
		if (surveillance == Surveillance.NO_SURVEILLANCE) {
			return Math.random() < 0.5;
		}
		else if (surveillance == Surveillance.PATROL) {
			return Math.random() < 0.3;
		}
		else if (surveillance == Surveillance.CAMERA) {
			return Math.random() < 0.1;
		}
		//there are barriers, he cannot fraud
		return false;
	}*/
	
	public Decision getDecision(Cases c, double proportion) {
		switch (c) {
		case ALLOWED_NO_TOLL:
			return Decision.DO_NOTHING;
		case ALLOWED_CHEAP_TOLL:
			if (proportion < 0.66) {
				return Decision.DO_NOTHING;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case ALLOWED_EXPENSIVE_TOLL:
			if (proportion < 0.5) {
				return Decision.DO_NOTHING;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_NO_SURVEILLANCE:
			if (proportion < 0.5) {
				return Decision.DO_NOTHING;
			}
			else if (proportion < 0.9) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_PATROL:
			if (proportion < 0.3) {
				return Decision.DO_NOTHING;
			}
			else if (proportion < 0.86) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_CAMERA:
			if (proportion < 0.1) {
				return Decision.DO_NOTHING;
			}
			else if (proportion < 0.82) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_BARRIER:
			if (proportion < 0.8) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_CHEAP_TOLL:
			if (proportion < 0.47) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		case FORBIDDEN_EXPENSIVE_TOLL:
			if (proportion < 0.14) {
				return Decision.CHANGE_VEHICLE;
			}
			else {
				return Decision.CHANGE_MOBILITY;
			}
		}
		//will not happen
		return Decision.DO_NOTHING;
	}
	
}

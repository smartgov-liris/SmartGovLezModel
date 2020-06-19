package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class CompanyChoice extends Choice {
	
	
	/*protected boolean wantToChangeMobility(Surveillance surveillance, boolean hasToChange) {
		//they cannot change mobility
		return false;
	}

	protected boolean wantToFraud(Surveillance surveillance) {
		if (surveillance == Surveillance.NO_SURVEILLANCE) {
			return Math.random() < 0.6;
		}
		else if (surveillance == Surveillance.PATROL) {
			return Math.random() < 0.4;
		}
		else if (surveillance == Surveillance.CAMERA) {
			return Math.random() < 0.2;
		}
		//there are barriers, he cannot fraud
		return false;
	}*/
	
	public Decision getDecision(Cases c, double proportion) {
		switch (c) {
		case ALLOWED_NO_TOLL:
			return Decision.DO_NOTHING;
		case ALLOWED_CHEAP_TOLL:
			return Decision.DO_NOTHING;
		case ALLOWED_EXPENSIVE_TOLL:
			return Decision.DO_NOTHING;
		case FORBIDDEN_NO_SURVEILLANCE:
			if (proportion < 0.6) {
				return Decision.DO_NOTHING;
			}
			else {
				return Decision.CHANGE_VEHICLE;
			}
		case FORBIDDEN_PATROL:
			if (proportion < 0.4) {
				return Decision.DO_NOTHING;
			}
			else {
				return Decision.CHANGE_VEHICLE;
			}
		case FORBIDDEN_CAMERA:
			if (proportion < 0.2) {
				return Decision.DO_NOTHING;
			}
			else {
				return Decision.CHANGE_VEHICLE;
			}
		}
		
		return Decision.CHANGE_VEHICLE;
	}
	
}

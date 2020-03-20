package org.liris.smartgov.lez.core.agent.establishment.personality.choice;

import org.liris.smartgov.lez.core.agent.establishment.personality.Decision;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class PrivateChoice extends Choice {
	
	protected boolean wantToChangeMobility(Surveillance surveillance, boolean hasToChange) {
		if (hasToChange) {
			//if he does not want to change vehicle, he changes mobility
			if (Math.random() < 0.2) {
				return true;
			}
		}
		
		//we then check if he accepts to pay tolls
		if (surveillance == Surveillance.CHEAP_TOLL)
			return Math.random() < 0.25;
		else if (surveillance == Surveillance.MEDIUM_TOLL) {
			return Math.random() < 0.5;
		}
		else if (surveillance == Surveillance.EXPENSIVE_TOLL){
			return Math.random() < 0.75;
		}
		//if there is no toll, he accepts to pay nothing
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
	}
	
}

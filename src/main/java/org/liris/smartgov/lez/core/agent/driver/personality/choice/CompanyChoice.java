package org.liris.smartgov.lez.core.agent.establishment.personality.choice;

import org.liris.smartgov.lez.core.agent.establishment.personality.Decision;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class CompanyChoice extends Choice {
	
	
	protected boolean wantToChangeMobility(Surveillance surveillance, boolean hasToChange) {
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
	}
	
}

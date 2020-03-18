package org.liris.smartgov.lez.core.agent.establishment.personality.choice;

import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class CompanyChoice extends Choice {

	@Override
	protected boolean acceptToPay(Surveillance surveillance) {
		//as they cannot change mobility, cannot refuse to pay
		return true;
	}

	@Override
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

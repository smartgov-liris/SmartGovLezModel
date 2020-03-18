package org.liris.smartgov.lez.core.agent.establishment.personality.choice;

import org.liris.smartgov.lez.core.agent.establishment.personality.Decision;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public abstract class Choice {
	public Decision getDecision(Surveillance surveillance, int nbPlacesForbidden) {
		if (nbPlacesForbidden == 0) {
			//if his vehicle does not need to be changed
			if ( acceptToPay(surveillance)) {
				return Decision.DO_NOTHING;
			}
			return Decision.CHANGE_MOBILITY;
		}
		else {
			//his vehicle is not allowed
			boolean pay = acceptToPay(surveillance);
			boolean fraud = wantToFraud(surveillance);
			if (pay && !fraud) {
				return Decision.CHANGE_VEHICLE;
			} else if (pay && fraud) {
				return Decision.DO_NOTHING;
			}
			else {
				// he refuses to pay anyway
				return Decision.CHANGE_MOBILITY;
			}
			
		}
	}
	
	protected abstract boolean acceptToPay(Surveillance surveillance);
	protected abstract boolean wantToFraud(Surveillance surveillance);
}

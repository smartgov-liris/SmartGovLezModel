package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public abstract class Choice {
	/*public Decision getDecision(Surveillance surveillance, int nbPlacesForbidden) {
		if (nbPlacesForbidden == 0) {
			//if his vehicle does not need to be changed
			if ( wantToChangeMobility(surveillance, false)) {
				return Decision.CHANGE_MOBILITY;
			}
			return Decision.DO_NOTHING;
		}
		else {
			//his vehicle is not allowed
			if (wantToFraud(surveillance)) {
				return Decision.DO_NOTHING;
			}
			if (wantToChangeMobility(surveillance, true)) {
				return Decision.CHANGE_MOBILITY;
			}
			return Decision.CHANGE_VEHICLE;
			
		}
	}
	
	protected abstract boolean wantToChangeMobility(Surveillance surveillance, boolean hasToChange);
	protected abstract boolean wantToFraud(Surveillance surveillance);*/
	public abstract Decision getDecision(Cases c, double proportion);
}

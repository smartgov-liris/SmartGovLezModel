package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

/**
 * Compute the choices of delivery drivers considering the deployed policies.
 * @author alban
 *
 */
public class CompanyChoice extends Choice {
	
	/**
	 * Returns the decision considering a case and proportion
	 * @param c case to be considered
	 * @param proportion proportion of agents in the same case who already have made their choice.
	 * It is used to stabilise the choices made, and not to use random.
	 * @return the decision
	 */
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

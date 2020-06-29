package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

/**
 * Compute the choices of private drivers considering the deployed policies.
 * @author alban
 *
 */
public class PrivateChoice extends Choice {
	
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

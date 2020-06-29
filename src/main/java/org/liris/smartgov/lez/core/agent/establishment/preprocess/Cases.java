package org.liris.smartgov.lez.core.agent.establishment.preprocess;

/**
 * Enumerates the cases that represent the agent's perception of deployed policies.
 * @author alban
 *
 */
public enum Cases {
	ALLOWED_NO_TOLL,
	ALLOWED_CHEAP_TOLL,
	ALLOWED_EXPENSIVE_TOLL,
	FORBIDDEN_NO_SURVEILLANCE,
	FORBIDDEN_PATROL,
	FORBIDDEN_CAMERA,
	FORBIDDEN_BARRIER,
	FORBIDDEN_CHEAP_TOLL,
	FORBIDDEN_EXPENSIVE_TOLL
}

package org.liris.smartgov.lez.core.agent.driver.personality.choice;

import org.liris.smartgov.lez.core.agent.driver.personality.Decision;
import org.liris.smartgov.lez.core.agent.establishment.preprocess.Cases;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

/**
 * Abstract choice class.
 * @author alban
 *
 */
public abstract class Choice {
	public abstract Decision getDecision(Cases c, double proportion);
}

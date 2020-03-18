package org.liris.smartgov.lez.core.agent.establishment.personality;

import org.liris.smartgov.lez.core.agent.establishment.ST8;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.Choice;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.CompanyChoice;
import org.liris.smartgov.lez.core.agent.establishment.personality.choice.PrivateChoice;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class Personality {
	private ST8 activity;
	private Choice choice;
	
	public Personality (ST8 activity) {
		this.activity = activity;
		if (activity != ST8.PRIVATE_HABITATION) {
			choice = new PrivateChoice();
		}
		else {
			choice = new CompanyChoice();
		}
	}
	
	public Decision getDecision(Surveillance surveillance, int placesVehicleForbidden) {
		return choice.getDecision(surveillance, placesVehicleForbidden);
	}
}

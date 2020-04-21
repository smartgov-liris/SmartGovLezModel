package org.liris.smartgov.lez.core.environment.lez.criteria;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

public class CritAirCriteria implements LezCriteria {
	
	CritAir allowed;
	
	public CritAirCriteria(CritAir allowed) {
		this.allowed = allowed;
	}

	@Override
	public boolean isAllowed(Vehicle vehicle) {
		return vehicle.getCritAir().ordinal() <= allowed.ordinal();
	}
	
	public void increaseCriteria () {
		if ( allowed != CritAir.CRITAIR_1) {
			allowed = CritAir.values()[allowed.ordinal() - 1];
		}
	}
	
	public void decreaseCriteria () {
		if ( allowed != CritAir.NONE ) {
			allowed = CritAir.values()[allowed.ordinal() + 1];
		}
	}
	
	public CritAir getCritAir() {
		return allowed;
	}

}

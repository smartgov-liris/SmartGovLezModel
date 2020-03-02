package org.liris.smartgov.lez.core.agent.establishment;

import org.liris.smartgov.simulator.core.simulation.time.Date;

public class Appointment {
	Establishment establishment;
	Date departureTime;


	public Appointment(Establishment establishment, Date departureTime) {
		super();
		this.establishment = establishment;
		this.departureTime = departureTime;
	}


	public Establishment getEstablishment() {
		return establishment;
	}


	public void setEstablishment(Establishment establishment) {
		this.establishment = establishment;
	}


	public Date getDepartureTime() {
		return departureTime;
	}


	public void setDepartureTime(Date departureTime) {
		this.departureTime = departureTime;
	}
	
}

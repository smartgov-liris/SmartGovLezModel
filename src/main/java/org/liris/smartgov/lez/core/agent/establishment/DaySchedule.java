package org.liris.smartgov.lez.core.agent.establishment;

import java.util.ArrayList;
import java.util.List;

public class DaySchedule {
	
	private List<Appointment> schedule;
	private Establishment origin;
	
	public DaySchedule (ArrayList<Appointment> schedule, Establishment origin) {
		this.schedule = schedule;
		this.origin = origin;
	}
	
	public DaySchedule (Establishment origin) {
		this.schedule = new ArrayList<Appointment>();
		this.origin = origin;
	}
	
	public void addAppointment(Appointment appointment) {
		schedule.add(appointment);
	}
	
	public Establishment getOrigin() {
		return origin;
	}
}

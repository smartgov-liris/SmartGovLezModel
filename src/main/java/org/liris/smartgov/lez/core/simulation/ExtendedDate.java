package org.liris.smartgov.lez.core.simulation;

import org.liris.smartgov.simulator.core.simulation.time.Date;
import org.liris.smartgov.simulator.core.simulation.time.WeekDay;

/**
 * Allows to compute time between two dates
 * @author alban
 *
 */
public class ExtendedDate extends Date {

	public ExtendedDate(int day, WeekDay weekDay, int hour, int minutes, double seconds) {
		super(day, weekDay, hour, minutes, seconds);
	}
	
	/**
	 * returns the number of seconds from date 1 to date 2
	 * @param date1
	 * @param date2
	 * @return
	 */
	public static int getTimeBetween(Date date1, Date date2) {
		return Math.abs ( ( (date2.getDay() - date1.getDay()) * 86400 ) +
				( (date2.getHour() - date1.getHour()) * 3600 ) +
				( (date2.getMinutes() - date1.getMinutes() ) * 60 ) + 
				(int)( date2.getSeconds() - date1.getSeconds() )) ;
	}
}

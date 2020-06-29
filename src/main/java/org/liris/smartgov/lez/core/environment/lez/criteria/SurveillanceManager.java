package org.liris.smartgov.lez.core.environment.lez.criteria;

/**
 * Allows to manage surveillance.
 * @author alban
 *
 */
public class SurveillanceManager {
	private Surveillance surveillance;
	
	/**
	 * SurveillanceManager constructor.
	 * @param surveillance current surveillance 
	 */
	public SurveillanceManager (Surveillance surveillance) {
		this.surveillance = surveillance;
	}
	
	/**
	 * Returns current surveillance
	 * @return surveillance
	 */
	public Surveillance getSurveillance() {
		return surveillance;
	}
	
	/**
	 * Set a new surveillance
	 * @param surveillance surveillance to be set
	 */
	public void setSurveillance(Surveillance surveillance) {
		this.surveillance = surveillance;
	}
	
	/**
	 * Increase surveillance of one level if it is possible
	 */
	public void increaseSurveillance() {
		if ( surveillance != Surveillance.EXPENSIVE_TOLL ) {
			surveillance = Surveillance.values()[surveillance.ordinal() + 1];
		}
	}
	
	/**
	 * Decrease surveillance of one level if it is possible
	 */
	public void decreaseSurveillance() {
		if ( surveillance != Surveillance.NO_SURVEILLANCE ) {
			surveillance = Surveillance.values()[surveillance.ordinal() - 1];
		}
	}
}

package org.liris.smartgov.lez.core.environment.lez.criteria;

public class SurveillanceManager {
	private Surveillance surveillance;
	
	public SurveillanceManager (Surveillance surveillance) {
		this.surveillance = surveillance;
	}
	
	public Surveillance getSurveillance() {
		return surveillance;
	}
	
	public void setSurveillance(Surveillance surveillance) {
		this.surveillance = surveillance;
	}
	
	public void increaseSurveillance() {
		if ( surveillance != Surveillance.EXPENSIVE_TOLL ) {
			surveillance = Surveillance.values()[surveillance.ordinal() + 1];
		}
	}
	
	public void decreaseSurveillance() {
		if ( surveillance != Surveillance.NO_SURVEILLANCE ) {
			surveillance = Surveillance.values()[surveillance.ordinal() - 1];
		}
	}
}

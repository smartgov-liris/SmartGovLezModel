package org.liris.smartgov.lez.core.copert.fields;

import java.util.regex.Pattern;

public enum PassengerCarsSegment implements VehicleSegment {
	MINI("Mini"),
	SMALL("Small"),
	MEDIUM("Medium"),
	LARGE_SUV_EXECUTIVE("Large-SUV-Executive"),
	_2_STROKE("2-Stroke"),
	RANDOM("Random");
	
	
	private final String matcher;
	
	private PassengerCarsSegment(String matcher) {
		this.matcher = matcher;
	}

	@Override
	public String matcher() {
		return matcher;
	}
	
	public static PassengerCarsSegment getValue(String string) {
		for(PassengerCarsSegment value : values()) {
			if (Pattern.matches(value.matcher, string)) {
				return value;
			}
		}
		return null;
	}
	
	public static PassengerCarsSegment randomSelector() {
		return RANDOM;
	}

}

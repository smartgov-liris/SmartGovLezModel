package org.liris.smartgov.lez.core.agent.driver.vehicle;

import org.liris.smartgov.lez.core.copert.Copert;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Fuel;
import org.liris.smartgov.lez.core.copert.fields.Technology;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;
import org.liris.smartgov.lez.core.copert.fields.VehicleSegment;

public class PassengerVehicle extends Vehicle {
	
	public PassengerVehicle(String id, VehicleCategory category, Fuel fuel, VehicleSegment segment,  EuroNorm euroNorm, Technology technology, Copert copert) {
		super(id, category, fuel, segment, euroNorm, technology, copert);
	}
}

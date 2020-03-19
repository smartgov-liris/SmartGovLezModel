package org.liris.smartgov.lez.core.agent.establishment.preprocess;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.driver.behavior.DriverBehavior;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.agent.establishment.personality.Decision;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Fuel;
import org.liris.smartgov.lez.core.copert.fields.Technology;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;
import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;
import org.liris.smartgov.lez.core.copert.tableParser.CopertParser;
import org.liris.smartgov.lez.core.copert.tableParser.CopertSelector;
import org.liris.smartgov.lez.core.environment.lez.Environment;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;
import org.liris.smartgov.simulator.urban.osm.agent.OsmAgent;

public class LezPreprocessor {
	
	private Environment environment;
	private CopertParser parser;
	
	public LezPreprocessor(Environment environment, CopertParser parser) {
		this.environment = environment;
		this.parser = parser;
	}

	public Map<String, Integer> preprocess(Establishment establishment) {
		
		int replacedVehicles = 0;
		int mobilityChanged = 0;
		int nbFrauds = 0;
		
		Map<String, Integer> indicators = new HashMap<>();


		for(Vehicle vehicle : establishment.getFleet().values()) {
			Round round = establishment.getRounds().get(vehicle.getId());
			
			//counts the number of places in the journey where the vehicle is forbidden
			int placesVehicleForbidden = 0;
			Surveillance surveillance = Surveillance.NO_SURVEILLANCE;
			
			if(! environment.getNeighborhood(establishment.getClosestOsmNode()).isAllowed(vehicle)) {
				//if the origin establishment does not allow the vehicle
				placesVehicleForbidden ++;
				surveillance = environment.getNeighborhood(establishment.getClosestOsmNode()).getSurveillance();
			}
			
			int i = 0;
			while( i < round.getEstablishments().size()) {
				Neighborhood neighborhood = environment.getNeighborhood(round.getEstablishments().get(i).getClosestOsmNode());
				if (! neighborhood.isAllowed(vehicle) ) {
					//if the establishments of the round do not allow the vehicle
					placesVehicleForbidden ++;
					if (neighborhood.getSurveillance().ordinal() > surveillance.ordinal()) {
						surveillance = neighborhood.getSurveillance();
					}
				}
				i++;
			}
			
			Decision decision = establishment.getPersonality().getDecision(surveillance, placesVehicleForbidden);
			if(decision == Decision.CHANGE_VEHICLE) {
				CopertSelector selector = new CopertSelector();
				selector.put(CopertHeader.CATEGORY, vehicle.getCategory());
				if (vehicle.getCategory() != VehicleCategory.HEAVY_DUTY_TRUCK) {
					//only Diesel is available for heavy duty trucks
					selector.put(CopertHeader.FUEL, Fuel.DIESEL);
				} else {
					selector.put(CopertHeader.FUEL, Fuel.PETROL);
				}
				
				selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
				
				Vehicle newVehicle =
					DeliveryVehicleFactory.generateVehicle(
						selector,
						parser,
						vehicle.getId()
						);
				
				establishment.replaceVehicle(newVehicle.getId(), newVehicle);
				establishment.getPersonality().increaseVehicleChanged();
				replacedVehicles++;
			}
			else if (decision == Decision.CHANGE_MOBILITY) {
				establishment.replaceVehicle(vehicle.getId(), null);
				establishment.getPersonality().increaseMobilityChanged();
				mobilityChanged ++;
			}
			else if (decision == Decision.DO_NOTHING && placesVehicleForbidden > 0) {
				establishment.getPersonality().increaseNbFraud();
				nbFrauds++;
			}
		}
		indicators.put("Replaced", replacedVehicles);
		indicators.put("Mobility", mobilityChanged);
		indicators.put("Fraud", nbFrauds);
		return indicators;
		
	}
}

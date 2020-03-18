package org.liris.smartgov.lez.core.agent.establishment.preprocess;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

import java.util.ArrayList;

import org.liris.smartgov.lez.core.agent.driver.behavior.DriverBehavior;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.agent.establishment.personality.Decision;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Technology;
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

	public int preprocess(Establishment establishment) {
		
		int replacedVehicles = 0;
		


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
				selector.put(CopertHeader.FUEL, vehicle.getFuel());
				selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
				
				Vehicle newVehicle =
					DeliveryVehicleFactory.generateVehicle(
						selector,
						parser,
						vehicle.getId()
						);
				
				establishment.replaceVehicle(newVehicle.getId(), newVehicle);
				replacedVehicles++;
			}
			else if (decision == Decision.CHANGE_MOBILITY) {
				//TODO delete this vehicle/agent from simulation
			}
		}


		return replacedVehicles;
	}
}

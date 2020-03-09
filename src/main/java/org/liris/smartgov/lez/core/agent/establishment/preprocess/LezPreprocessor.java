package org.liris.smartgov.lez.core.agent.establishment.preprocess;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;

import java.util.ArrayList;

import org.liris.smartgov.lez.core.agent.driver.behavior.DriverBehavior;
import org.liris.smartgov.lez.core.agent.driver.vehicle.DeliveryVehicleFactory;
import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.copert.fields.EuroNorm;
import org.liris.smartgov.lez.core.copert.fields.Technology;
import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;
import org.liris.smartgov.lez.core.copert.tableParser.CopertParser;
import org.liris.smartgov.lez.core.copert.tableParser.CopertSelector;
import org.liris.smartgov.lez.core.environment.lez.Environment;
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
			boolean vehicleForbidden = false;
			
			if(! environment.getNeighborhood(establishment.getClosestOsmNode()).getLezCriteria().isAllowed(vehicle)) {
				//if the origin establishment does not allow the vehicle
				vehicleForbidden = true;
			}
			
			int i = 0;
			while(!vehicleForbidden && i < round.getEstablishments().size()) {
				
				if (! environment.getNeighborhood( round.getEstablishments().get(i).getClosestOsmNode() ).getLezCriteria().isAllowed(vehicle) ) {
					//if the establishments of the round do not allow the vehicle
					vehicleForbidden = true;
				}
				i++;
			}
			
			if(vehicleForbidden && establishment.acceptToReplaceVehicle()) {
				CopertSelector selector = new CopertSelector();
				selector.put(CopertHeader.CATEGORY, vehicle.getCategory());
				selector.put(CopertHeader.FUEL, vehicle.getFuel());
				selector.put(CopertHeader.EURO_STANDARD, EuroNorm.EURO6);
				//selector.put(CopertHeader.SEGMENT, Segment);
				//selector.put(CopertHeader.TECHNOLOGY, Technology.RANDOM); // Not all technologies are available for all euro norms
				
				Vehicle newVehicle =
					DeliveryVehicleFactory.generateVehicle(
						selector,
						parser,
						vehicle.getId()
						);
				
				establishment.replaceVehicle(newVehicle.getId(), newVehicle);
				replacedVehicles++;
			}
		}

		return replacedVehicles;
	}
}

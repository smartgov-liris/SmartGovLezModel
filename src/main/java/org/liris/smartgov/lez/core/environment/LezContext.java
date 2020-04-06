package org.liris.smartgov.lez.core.environment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.liris.smartgov.lez.core.agent.establishment.Establishment;
import org.liris.smartgov.lez.core.agent.establishment.Round;
import org.liris.smartgov.lez.core.environment.graph.PollutableOsmArc;
import org.liris.smartgov.lez.core.environment.lez.Environment;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;
import org.liris.smartgov.lez.core.simulation.scenario.DeliveriesScenario;
import org.liris.smartgov.lez.core.simulation.scenario.RandomTrafficPollutionScenario;
import org.liris.smartgov.lez.input.lez.CritAirLezDeserializer;
import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.scenario.Scenario;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;

public class LezContext extends OsmContext {
	
	private Map<String, Establishment> establishments;
	public Map<String, Round> ongoingRounds;

	public LezContext(String configFile) {
		super(configFile);
		this.establishments = new HashMap<>();
		this.ongoingRounds = new TreeMap<>();
	}

	public Map<String, Establishment> getEstablishments() {
		return establishments;
	}

	public void setEstablishments(Map<String, Establishment> establishments) {
		this.establishments = establishments;
	}
	
	public void reload() {
		agents = new TreeMap<>();
		ongoingRounds = new TreeMap<>();
	}
	
	public void resetVariables() {
		if (getScenario() instanceof DeliveriesScenario) {
			//if we are in the deliveries scenario, we also reset pollution of neighborhoods
			((DeliveriesScenario)getScenario()).getEnvironment().resetNeighborhoodVariables();
		}
		for (Arc arc : arcs.values()) {
			((PollutableOsmArc)arc).resetPollution();
		}
	}
	
	public void resetConfiguration() {
		for (Neighborhood neighborhood : ((DeliveriesScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setSurveillance(Surveillance.NO_SURVEILLANCE);
		}
	}

	
	@Override
	protected Scenario loadScenario(String scenarioName) {
		Scenario superScenario = super.loadScenario(scenarioName);
		if (superScenario != null) {
			return superScenario;
		}
		switch(scenarioName){
		case DeliveriesScenario.NoLezDeliveries.name:
			return new DeliveriesScenario.NoLezDeliveries();
			
		case RandomTrafficPollutionScenario.name:
			return new RandomTrafficPollutionScenario(Environment.none());
			
			
		case DeliveriesScenario.name:
			try {
				return new DeliveriesScenario(
						CritAirLezDeserializer.load(
								this.getFileLoader().load("dimensions")
								)
						);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		default:
			return null;
		}
	}
}

package org.liris.smartgov.lez.core.environment;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
import org.liris.smartgov.lez.politic.PoliticalVar;
import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.scenario.Scenario;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;

public class LezContext extends OsmContext {
	
	private Map<String, Establishment> establishments;
	public Map<String, Round> ongoingRounds;
	private boolean politic;

	public LezContext(String configFile, boolean politic) {
		super(configFile);
		this.establishments = new HashMap<>();
		this.ongoingRounds = new TreeMap<>();
		this.politic = politic;
	}
	
	public boolean getPolitic() {
		return politic;
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
	
	public void resetVariables(boolean fastReset) {
		if (getScenario() instanceof DeliveriesScenario) {
			//if we are in the deliveries scenario, we also reset pollution of neighborhoods
			((DeliveriesScenario)getScenario()).getEnvironment().resetNeighborhoodVariables();
		}
		if (!fastReset) {
			for (Arc arc : arcs.values()) {
				((PollutableOsmArc)arc).resetPollution();
			}
		}
	}
	
	public void changeConfiguration(int iteration) {
		for (Neighborhood neighborhood : ((DeliveriesScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			if (iteration ==1) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.CRITAIR_2));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.CRITAIR_2));
				neighborhood.setSurveillance(Surveillance.CAMERA);
			}
			else if (iteration == 2) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.CRITAIR_1));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.CRITAIR_1));
				neighborhood.setSurveillance(Surveillance.EXPENSIVE_TOLL);
			}
			else if (iteration == 3) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.CRITAIR_3));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.CRITAIR_3));
				neighborhood.setSurveillance(Surveillance.CHEAP_TOLL);
			}
			else if (iteration == 4) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.CRITAIR_5));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.CRITAIR_5));
				neighborhood.setSurveillance(Surveillance.NO_SURVEILLANCE);
			}
			else if (iteration == 5) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.CRITAIR_1));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.CRITAIR_1));
				neighborhood.setSurveillance(Surveillance.NO_SURVEILLANCE);
			}
		}
	}
	
	public void resetConfiguration() {
		for (Neighborhood neighborhood : ((DeliveriesScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setSurveillance(Surveillance.NO_SURVEILLANCE);
			
		}
	}
	
	public void setCompletelyRandomConfiguration() {
		for (Neighborhood neighborhood : ((DeliveriesScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			neighborhood.setDeliveryLezCriteria(new CritAirCriteria(randomEnum(CritAir.class)));
			neighborhood.setPrivateLezCriteria(new CritAirCriteria(randomEnum(CritAir.class)));
			neighborhood.setSurveillance(randomEnum(Surveillance.class));
			
		}
	}
	
	public void setPartiallyRandomConfiguration() {
		CritAir deliveryCriteria = randomEnum(CritAir.class);
		CritAir privateCriteria = randomEnum(CritAir.class);
		Surveillance surveillance = randomEnum(Surveillance.class);
		
		for (Neighborhood neighborhood : ((DeliveriesScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			if (Integer.parseInt(PoliticalVar.variables.get("distinct_criterias")) == 1 ) {
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(deliveryCriteria));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(privateCriteria));
			}
			else {
				//if we dont make the distinction between criterias, we put them at the same level
				neighborhood.setDeliveryLezCriteria(new CritAirCriteria(privateCriteria));
				neighborhood.setPrivateLezCriteria(new CritAirCriteria(privateCriteria));
			}
			neighborhood.setSurveillance(surveillance);

		}
	}
	
    public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
    	Random random = new Random();
        int x = random.nextInt(clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
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

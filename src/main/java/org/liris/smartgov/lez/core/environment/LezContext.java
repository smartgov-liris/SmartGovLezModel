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
import org.liris.smartgov.lez.core.simulation.scenario.LezScenario;
import org.liris.smartgov.lez.core.simulation.scenario.RandomTrafficPollutionScenario;
import org.liris.smartgov.lez.input.lez.CritAirLezDeserializer;
import org.liris.smartgov.lez.politic.PoliticalVar;
import org.liris.smartgov.simulator.core.environment.graph.Arc;
import org.liris.smartgov.simulator.core.scenario.Scenario;
import org.liris.smartgov.simulator.urban.osm.environment.OsmContext;

/**
 * Context of the simulation
 * @author alban
 *
 */
public class LezContext extends OsmContext {
	
	private Map<String, Establishment> establishments;
	public Map<String, Round> ongoingRounds;
	private boolean politic;

	/**
	 * 
	 * @param configFile the file with all the paths to needed files
	 * @param politic whether or not we have to launch political layer
	 */
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
	
	/**
	 * Reload agents and rounds
	 */
	public void reload() {
		agents = new TreeMap<>();
		ongoingRounds = new TreeMap<>();
	}
	
	/**
	 * Reset pollution variables, but also local perceptions of neighborhoods
	 * @param fastReset whether or not we want to reset arcs's pollution (long to do)
	 */
	public void resetVariables(boolean fastReset) {
		if (getScenario() instanceof LezScenario) {
			//if we are in the deliveries scenario, we also reset pollution of neighborhoods
			((LezScenario)getScenario()).getEnvironment().resetNeighborhoodVariables();
		}
		if (!fastReset) {
			for (Arc arc : arcs.values()) {
				((PollutableOsmArc)arc).resetPollution();
			}
		}
	}
	
	/**
	 * Reset configuration to a state with all vehicles allowed and no surveillance
	 */
	public void resetConfiguration() {
		for (Neighborhood neighborhood : ((LezScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			neighborhood.setDeliveryLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setPrivateLezCriteria(new CritAirCriteria(CritAir.NONE));
			neighborhood.setSurveillance(Surveillance.NO_SURVEILLANCE);
		}
	}
	
	/**
	 * Create new completely random configuration
	 */
	public void setCompletelyRandomConfiguration() {
		for (Neighborhood neighborhood : ((LezScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
			neighborhood.setDeliveryLezCriteria(new CritAirCriteria(randomEnum(CritAir.class)));
			neighborhood.setPrivateLezCriteria(new CritAirCriteria(randomEnum(CritAir.class)));
			neighborhood.setSurveillance(randomEnum(Surveillance.class));
			
		}
	}
	
	/**
	 * Creates a new random configuration but all neighborhood have the same criterias and the same surveillance
	 */
	public void setPartiallyRandomConfiguration() {
		CritAir deliveryCriteria = randomEnum(CritAir.class);
		CritAir privateCriteria = randomEnum(CritAir.class);
		Surveillance surveillance = randomEnum(Surveillance.class);
		
		for (Neighborhood neighborhood : ((LezScenario)getScenario()).getEnvironment().getNeighborhoods().values() ) {
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
	
	/**
	 * Returns a random value for an enumeration
	 * @param clazz class of the enumeration
	 * @return random value of the enum
	 */
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
		case LezScenario.NoLezDeliveries.name:
			return new LezScenario.NoLezDeliveries();
			
		case RandomTrafficPollutionScenario.name:
			return new RandomTrafficPollutionScenario(Environment.none());
			
			
		case LezScenario.name:
			try {
				return new LezScenario(
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

package org.liris.smartgov.lez.core.environment.lez;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;
import org.liris.smartgov.lez.core.environment.pollution.Pollution;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;


public class Environment {
	protected Map <String, Neighborhood> neighborhoods = new HashMap<>();
	
	/**
	 * 
	 * @param top the top bound
	 * @param bottom the bottom bound
	 * @param left the left bound
	 * @param right the right bound
	 * @param gridSize the size of the matrix : gridSize = 4 means a 4*4 matrix
	 * @param allowed the allowed vehicles
	 * @param surveillance the initial surveillance level
	 */
	public Environment(double top, double bottom, double left, double right, 
			int gridSize, CritAir deliveryAllowed, CritAir privateAllowed, Surveillance surveillance) {
		double horizontal_size = (right - left) / gridSize;
		double vertical_size = (top - bottom) / gridSize ;
		int id = 0;
		
		for (int i = 0 ; i < gridSize ; i ++) {
			for (int j = 0 ; j < gridSize ; j++) {
				LatLon[] perimeter = {new LatLon(bottom + j * vertical_size, left + i * horizontal_size),
						new LatLon(bottom + (j + 1) * vertical_size, left + i * horizontal_size),
						new LatLon(bottom + (j + 1) * vertical_size, left + (i + 1) * horizontal_size),
						new LatLon(bottom + j * vertical_size, left + (i + 1) * horizontal_size)
						};
				neighborhoods.put(Integer.toString(id), new Neighborhood(perimeter, 
						new CritAirCriteria(deliveryAllowed), new CritAirCriteria(privateAllowed), surveillance, Integer.toString(id)));
				id ++;
			}
		}
	}
	
	public Neighborhood getNeighborhood (OsmNode node) {
		for ( Neighborhood neighborhood : neighborhoods.values()) {
			if ( neighborhood.contains(node) ) {
				return neighborhood;
			}
		}
		return null;
	}
	
	public Neighborhood getNeighborhood (String id) {
		if (neighborhoods.get(id) == null) {
			throw new IllegalArgumentException("There is no neighborhood with this id");
		}
		return neighborhoods.get(id);
	}
	
	public Map<String,Neighborhood> getNeighborhoods() {
		return neighborhoods;
	}
	
	public void resetNeighborhoodPollution() {
		for (Neighborhood neighborhood : neighborhoods.values()) {
			neighborhood.resetPollution();
		}
	}
	
	
	private Environment() {
		neighborhoods.put("0", Neighborhood.none());
	}
	
	public Map<String, Pollution> getPollutionByNeighborhood() {
		Map<String, Pollution> pollutions = new HashMap<>();
		for ( Neighborhood neighborhood : neighborhoods.values() ) {
			pollutions.put(neighborhood.getID(), neighborhood.getPollution());
		}
		return pollutions;
	}
	
	public static Environment none() {
		return new noLezEnvironment();
	}
	
	
	public void increaseDeliveryCriteria (String id) {
		Neighborhood neighborhood = getNeighborhood(id);
		((CritAirCriteria)neighborhood.getDeliveryLezCriteria()).increaseCriteria();
	}
	
	public void increasePrivateCriteria (String id) {
		Neighborhood neighborhood = getNeighborhood(id);
		((CritAirCriteria)neighborhood.getPrivateLezCriteria()).increaseCriteria();
	}
	
	public void decreaseDeliveryCriteria (String id) {
		Neighborhood neighborhood = getNeighborhood(id);
		((CritAirCriteria)neighborhood.getDeliveryLezCriteria()).decreaseCriteria();
	}
	
	public void decreasePrivateCriteria (String id) {
		Neighborhood neighborhood = getNeighborhood(id);
		((CritAirCriteria)neighborhood.getPrivateLezCriteria()).decreaseCriteria();
	}
	
	private static class noLezEnvironment extends Environment {
		public Neighborhood getNeighborhood(OsmNode node) {
			if (neighborhoods.isEmpty()) {
				throw new IllegalStateException("There is no lez to return");
			}
			return neighborhoods.get("0");
		}
	}
}

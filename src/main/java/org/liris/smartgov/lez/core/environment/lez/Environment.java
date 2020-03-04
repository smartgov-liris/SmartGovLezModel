package org.liris.smartgov.lez.core.environment.lez;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

public class Environment {
	private List <Lez> neighborhoods = new ArrayList<Lez>();
	//gridSize is the size of the matrix : gridSize = 4 means a 4*4 matrix
	private int gridSize;
	
	public Environment(double top, double bottom, double left, double right, int gridSize, Collection<CritAir> allowed) {
		this.gridSize = gridSize;
		double horizontal_size = (right - left) / gridSize;
		double vertical_size = (top - bottom) / gridSize ;
		int id = 0;
		
		for (int i = 0 ; i < gridSize ; i ++) {
			for (int j = 0 ; j < gridSize ; j++) {
				allowed.add(CritAir.CRITAIR_1);
				LatLon[] perimeter = {new LatLon(bottom + j * vertical_size, left + i * horizontal_size),
						new LatLon(bottom + (j + 1) * vertical_size, left + i * horizontal_size),
						new LatLon(bottom + (j + 1) * vertical_size, left + (i + 1) * horizontal_size),
						new LatLon(bottom + j * vertical_size, left + (i + 1) * horizontal_size)
						};
				neighborhoods.add(new Lez(perimeter, new CritAirCriteria(allowed), id));
			}
		}
	}
	
	public Lez getNeighborhood (OsmNode node) {
		for ( Lez lez : neighborhoods) {
			if ( lez.contains(node) ) {
				return lez;
			}
		}
		return null;
	}
}

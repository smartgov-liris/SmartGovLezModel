package org.liris.smartgov.lez.urban;

import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class LatLonId extends LatLon {
	
	static private int index = 0;
	private int id;

	public LatLonId(double lat, double lon) {
		super(lat, lon);
		id = index;
		index++;
	}
	
	public LatLonId(LatLon latlon) {
		super(latlon.lat, latlon.lon);
		id = index;
		index++;
	}
	
	public int getId() {
		return id;
	}

}

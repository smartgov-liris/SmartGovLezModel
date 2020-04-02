package org.liris.smartgov.lez.core.environment.lez;

import org.locationtech.jts.algorithm.locate.IndexedPointInAreaLocator;
import org.locationtech.jts.algorithm.locate.PointOnGeometryLocator;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Location;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.impl.CoordinateArraySequence;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.liris.smartgov.lez.core.agent.driver.vehicle.Vehicle;
import org.liris.smartgov.lez.core.copert.fields.Pollutant;
import org.liris.smartgov.lez.core.copert.fields.VehicleCategory;
import org.liris.smartgov.lez.core.environment.Structure;
import org.liris.smartgov.lez.core.environment.lez.criteria.AllAllowedCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.LezCosts;
import org.liris.smartgov.lez.core.environment.lez.criteria.LezCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;
import org.liris.smartgov.lez.core.environment.pollution.Pollution;
import org.liris.smartgov.lez.politic.policyagent.FeaturesDouble;
import org.liris.smartgov.simulator.core.environment.graph.astar.Costs;
import org.liris.smartgov.simulator.urban.geo.environment.graph.DistanceCosts;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.geo.utils.lonLat.LonLat;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

/**
 * A Low Emission Zone representation.
 *
 */
public class Neighborhood implements Structure {
	
	private LatLon[] perimeter;
	private PointOnGeometryLocator locator;
	private LezCriteria deliveryLezCriteria;
	private LezCriteria privateLezCriteria;
	private String id;
	private Pollution pollution;
	private Surveillance surveillance;
	private List<Double> satisfactions;
	
	/**
	 * Lez constructor.
	 * 
	 * @param perimeter polygon that describes the perimeter of the LEZ.
	 * If the polygon is not closed, it will be completed automatically.
	 * @param lezCriteria criteria associated to this lez, that determines which
	 * vehicles are allowed or not
	 */
	public Neighborhood(LatLon[] perimeter, LezCriteria deliveryLezCriteria,
			LezCriteria privateLezCriteria, Surveillance surveillance, String id) {
		this.id = id;
		this.perimeter = perimeter;
		this.deliveryLezCriteria = deliveryLezCriteria;
		this.privateLezCriteria = privateLezCriteria;
		pollution = new Pollution();
		this.surveillance = surveillance;
		satisfactions = new ArrayList<>();
		
		GeometryFactory factory = new GeometryFactory();
		
		Coordinate[] coordinates;
		boolean closed;
		if(perimeter[0].equals(perimeter[perimeter.length - 1])) {
			coordinates = new Coordinate[perimeter.length];
			closed = true;
		}
		else {
			coordinates = new Coordinate[perimeter.length + 1];
			closed = false;
		}
		
		LonLat projector = new LonLat();
		for(int i = 0; i < perimeter.length; i++) {
			coordinates[i] = projector.project(perimeter[i]);
		}
		
		if(!closed) {
			coordinates[coordinates.length - 1] = projector.project(perimeter[0]);
		}
		
		locator = new IndexedPointInAreaLocator(
				new Polygon(
					new LinearRing(
						new CoordinateArraySequence(coordinates),
						factory
						),
					new LinearRing[] {},
					factory
					));
	}
	
	public LatLon[] getPerimeter() {
		return perimeter;
	}
	
	public Surveillance getSurveillance() {
		return surveillance;
	}
	
	/**
	 * Return the LEZ criteria associated to this LEZ, that determines
	 * which vehicles are allowed or not.
	 * 
	 * @return lez criteria
	 */
	public LezCriteria getDeliveryLezCriteria() {
		return deliveryLezCriteria;
	}
	
	public LezCriteria getPrivateLezCriteria() {
		return privateLezCriteria;
	}
	
	public void setDeliveryLezCriteria(LezCriteria lezCriteria) {
		this.deliveryLezCriteria = lezCriteria;
	}
	
	public void setPrivateLezCriteria(LezCriteria lezCriteria) {
		this.privateLezCriteria = lezCriteria;
	}
	
	public void setSurveillance (Surveillance surveillance) {
		this.surveillance = surveillance;
	}
	
	public void increasePollution(Pollutant pollutant, double increment) {
		pollution.get(pollutant).increasePollution(increment);
	}
	
	public Pollution getPollution() {
		return pollution;
	}
	
	public void giveSatisfaction ( double satisfaction ) {
		satisfactions.add(satisfaction);
	}
	
	/**
	 * Returns the cost function associated to this vehicle, depending
	 * on its permission to enter the LEZ or not.
	 * <ul>
	 * <li> If the vehicle is allowed, a normal DistanceCosts is returned, as if the
	 * lez did not exist for this vehicle.</li>
	 * <li> Else, {@link org.liris.smartgov.lez.core.environment.lez.criteria.LezCosts} are used.</li>
	 * </ul>
	 * 
	 * @param deliveryVehicle vehicle
	 * @return cost function associated to the specified vehicle in the current urban area
	 */
	public Costs costs(Vehicle vehicle) {
		
		if(isAllowed(vehicle))
			return new DistanceCosts();
		return new LezCosts(vehicle);
	}
	
	public boolean isAllowed (Vehicle vehicle) {
		if ( vehicle.getCategory() == VehicleCategory.PASSENGER_CAR ) {
			return privateLezCriteria.isAllowed(vehicle);
		}
		else {
			return deliveryLezCriteria.isAllowed(vehicle);
		}
	}
	
	public void resetPollution() {
		pollution = new Pollution();
	}

	/*
	 * Used by NoLez class below
	 */
	private Neighborhood() {
		this.deliveryLezCriteria = new AllAllowedCriteria();
		this.privateLezCriteria = new AllAllowedCriteria();
	}
	
	/**
	 * Determines if the specified osm node is contained in the lez,
	 * thanks to a <a href="https://locationtech.github.io/jts/javadoc/org/locationtech/jts/algorithm/locate/package-summary.html">
	 * JTS Point-In-Polygon algorithm</a>.
	 * 
	 * @param node osm node
	 * @return true if and only if the node is strictly contained in this LEZ
	 */
	public boolean contains(OsmNode node) {
		if(locator.locate(
					new LonLat().project(node.getPosition())
				) == Location.INTERIOR)
			return true;
		return false;
	}
	
	/**
	 * Returns a special LEZ instance with no perimeter and all vehicles allowed,
	 * that can be smartly used by scenarios with a lez parameter to compare results
	 * with or without LEZ.
	 * 
	 * @return a lez with absolutely no restriction
	 */
	public static Neighborhood none() {
		return new NoLez();
	}
	
	private static class NoLez extends Neighborhood {
		
		/*
		 * Overrides the contains method so that no node is contained
		 * in the lez, without using the JTS algorithm.
		 */
		@Override
		public boolean contains(OsmNode node) {
			return false;
		}
		

	}
	
	@Override
	public String getID() {
		return id;
	}
	
	@Override
	public String getClassName() {
		return this.getClass().getName();
	}

	@Override
	public FeaturesDouble getLocalPerformances(List<String> labels) {
		//compute pollution
		double pollution = this.pollution.get(Pollutant.N2O).getValue() +
				this.pollution.get(Pollutant.CO).getValue() +
				this.pollution.get(Pollutant.PM).getValue();
		
		double satisfaction = 0;
		for ( double satisfactionScore : satisfactions ) {
			satisfaction += satisfactionScore;
		}
		
		List<Double> features = new ArrayList<>();
		features.add(pollution);
		features.add(satisfaction);
		return new FeaturesDouble(features);
	}

}

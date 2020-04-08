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
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.LezCosts;
import org.liris.smartgov.lez.core.environment.lez.criteria.LezCriteria;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;
import org.liris.smartgov.lez.core.environment.lez.criteria.SurveillanceManager;
import org.liris.smartgov.lez.core.environment.pollution.Pollution;
import org.liris.smartgov.lez.core.simulation.files.FilePath;
import org.liris.smartgov.lez.core.simulation.files.FilesManagement;
import org.liris.smartgov.lez.politic.policyagent.ActionableByPolicyAgent;
import org.liris.smartgov.lez.politic.policyagent.FeaturesDouble;
import org.liris.smartgov.lez.politic.policyagent.PolicyAction;
import org.liris.smartgov.simulator.core.environment.graph.astar.Costs;
import org.liris.smartgov.simulator.urban.geo.environment.graph.DistanceCosts;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;
import org.liris.smartgov.simulator.urban.geo.utils.lonLat.LonLat;
import org.liris.smartgov.simulator.urban.osm.environment.graph.OsmNode;

/**
 * A Low Emission Zone representation.
 *
 */
public class Neighborhood implements Structure , ActionableByPolicyAgent{
	
	private LatLon[] perimeter;
	private PointOnGeometryLocator locator;
	private LezCriteria deliveryLezCriteria;
	private LezCriteria privateLezCriteria;
	private String id;
	private Pollution pollution;
	private Pollution referencePollution;
	private SurveillanceManager surveillance;
	private List<Double> satisfactions;
	private int nbFraud;
	private int nbChangedMobilities;
	private int nbChangedVehicles;
	
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
		this.surveillance = new SurveillanceManager(surveillance);
		satisfactions = new ArrayList<>();
		nbFraud = 0;
		nbChangedMobilities = 0;
		nbChangedVehicles = 0;
		
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
		return surveillance.getSurveillance();
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
		this.surveillance.setSurveillance(surveillance);
	}
	
	public void increasePollution(Pollutant pollutant, double increment) {
		pollution.get(pollutant).increasePollution(increment);
	}
	
	public Pollution getPollution() {
		return pollution;
	}
	
	public void giveSatisfaction ( double satisfaction, boolean changedVehicle, boolean changedMobility, boolean fraud ) {
		satisfactions.add(satisfaction);
		if ( changedVehicle ) {
			nbChangedVehicles += 1;
		}
		if ( changedMobility ) {
			nbChangedMobilities += 1;
		}
		if ( fraud ) {
			nbFraud += 1;
		}
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
	
	public void resetVariables() {
		if (referencePollution == null) {
			referencePollution = pollution;
		}
		pollution = new Pollution();
		satisfactions = new ArrayList<>();
		nbFraud = 0;
		nbChangedMobilities = 0;
		nbChangedVehicles = 0;
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

	public double getAbsPollution() {
		double pollution = this.pollution.get(Pollutant.N2O).getAbsValue() + 
				this.pollution.get(Pollutant.CO).getAbsValue() +
				this.pollution.get(Pollutant.PM).getAbsValue();
		return pollution / 1000;
	}
	
	@Override
	public FeaturesDouble getLocalPerformances(List<String> labels) {
		List<Double> features = new ArrayList<>();
		
		for ( String label : labels ) {
			if ( label.equals("Pollution") ) {
				//compute pollution
				double pollution = referencePollution.get(Pollutant.N2O).getAbsValue() - this.pollution.get(Pollutant.N2O).getAbsValue() +
						referencePollution.get(Pollutant.CO).getAbsValue() - this.pollution.get(Pollutant.CO).getAbsValue() +
						referencePollution.get(Pollutant.PM).getAbsValue() - this.pollution.get(Pollutant.PM).getAbsValue();
				features.add(pollution/1000);
			}
			else if ( label.equals("Satisfaction") ) {
				double satisfaction = 0;
				for ( double satisfactionScore : satisfactions ) {
					satisfaction += satisfactionScore;
				}
				
				features.add(satisfaction);
			}
			else if ( label.equals("ChangedVehicles") ) {
				features.add((double)nbChangedVehicles);
			}
			else if ( label.equals("changedMobilities") ) {
				features.add((double)nbChangedMobilities);
			}
			else if ( label.equals("frauded") ) {
				features.add((double)nbFraud);
			}
			else if (label.equals("gain")) {
				//compute pollution
				double pollution = (referencePollution.get(Pollutant.N2O).getAbsValue() - this.pollution.get(Pollutant.N2O).getAbsValue()) +
						(referencePollution.get(Pollutant.CO).getAbsValue() - this.pollution.get(Pollutant.CO).getAbsValue()) +
						(referencePollution.get(Pollutant.PM).getAbsValue() - this.pollution.get(Pollutant.PM).getAbsValue());
				pollution = pollution/1000;
				//compute satisfaction
				double satisfaction = 0;
				for ( double satisfactionScore : satisfactions ) {
					satisfaction += satisfactionScore;
				}
				features.add(pollution + satisfaction);
			}
		}
		return new FeaturesDouble(features);
	}

	@Override
	public List<PolicyAction> getAvailablePolicyActions() {
		List<PolicyAction> availableActions = new ArrayList<>();
		availableActions.add(PolicyAction.DO_NOTHING);
		if ( ((CritAirCriteria)deliveryLezCriteria).getCritAir() != CritAir.CRITAIR_1 ) {
			availableActions.add(PolicyAction.INCREASE_DELIVERIE_CRITERIA);
		}
		if ( ((CritAirCriteria)deliveryLezCriteria).getCritAir() != CritAir.NONE ) {
			availableActions.add(PolicyAction.DECREASE_DELIVERIE_CRITERIA);
		}
		if ( ((CritAirCriteria)privateLezCriteria).getCritAir() != CritAir.CRITAIR_1 ) {
			availableActions.add(PolicyAction.INCREASE_PRIVATE_CRITERIA);
		}
		if ( ((CritAirCriteria)privateLezCriteria).getCritAir() != CritAir.NONE ) {
			availableActions.add(PolicyAction.DECREASE_PRIVATE_CRITERIA);
		}
		if (surveillance.getSurveillance() != Surveillance.EXPENSIVE_TOLL) {
			availableActions.add(PolicyAction.INCREASE_SURVEILLANCE);
		}
		if ( surveillance.getSurveillance() != Surveillance.NO_SURVEILLANCE ) {
			availableActions.add(PolicyAction.DECREASE_SURVEILLANCE);
		}
		return availableActions;
	}

	@Override
	public void doPolicyAction(PolicyAction policyAction) {
		switch (policyAction) {
		case INCREASE_DELIVERIE_CRITERIA:
			((CritAirCriteria)deliveryLezCriteria).increaseCriteria();
			break;
		case DECREASE_DELIVERIE_CRITERIA:
			((CritAirCriteria)deliveryLezCriteria).decreaseCriteria();
			break;
		case INCREASE_PRIVATE_CRITERIA:
			((CritAirCriteria)privateLezCriteria).increaseCriteria();
			break;
		case DECREASE_PRIVATE_CRITERIA:
			((CritAirCriteria)privateLezCriteria).decreaseCriteria();
			break;
		case INCREASE_SURVEILLANCE:
			surveillance.increaseSurveillance();
			break;
		case DECREASE_SURVEILLANCE:
			surveillance.decreaseSurveillance();
			break;
		default:
	}
	}

}

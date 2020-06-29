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
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

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
 * A neightborhood representation.
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
	private double highestGain = Double.MIN_VALUE;
	private double lastGain = 0.0;
	private int[] highestConfig;
	
	/**
	 * Neighborhood constructor.
	 * 
	 * @param perimeter polygon that describes the perimeter of the neighborhood.
	 * If the polygon is not closed, it will be completed automatically.
	 * @param deliveryLezCriteria criteria associated to this neighborhood that determines which
	 * delivery vehicles are allowed or not
	 * @param privateLezCriteria criteria associated to this neighborhood that determines which
	 * private vehicles are allowed or not
	 * @param surveillance surveillance deployed in the neighborhood
	 * @param neighborhood's id
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
		highestConfig = new int[3];
		setCurrentConfigTheHighest();
		
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
	
	/**
	 * Returns neighborhood's perimeter.
	 * @return perimeter
	 */
	public LatLon[] getPerimeter() {
		return perimeter;
	}
	
	/**
	 * Save the current configuration as the best for now.
	 */
	public void setCurrentConfigTheHighest() {
		highestConfig[0] = ((CritAirCriteria)deliveryLezCriteria).getCritAir().ordinal();
		highestConfig[1] = ((CritAirCriteria)privateLezCriteria).getCritAir().ordinal();
		highestConfig[2] = surveillance.getSurveillance().ordinal();
	}
	
	/**
	 * Compare a configuration with the current one.
	 * @param config configuration to be compared
	 * @return true if they are the same, false otherwise
	 */
	public boolean isCurrentConfig(int[] config) {
		return config[0] == ((CritAirCriteria)deliveryLezCriteria).getCritAir().ordinal() &&
				config[1] == ((CritAirCriteria)privateLezCriteria).getCritAir().ordinal() &&
				config[2] == surveillance.getSurveillance().ordinal();
	}
	
	/**
	 * Returns the current surveillance
	 * @return surveillance
	 */
	public Surveillance getSurveillance() {
		return surveillance.getSurveillance();
	}
	
	/**
	 * Return the delivery LEZ criteria associated to this neighborhood, that determines
	 * which delivery vehicles are allowed or not.
	 * 
	 * @return delivery lez criteria
	 */
	public LezCriteria getDeliveryLezCriteria() {
		return deliveryLezCriteria;
	}
	
	/**
	 * Return the private LEZ criteria associated to this neighborhood, that determines
	 * which private vehicles are allowed or not.
	 * 
	 * @return private lez criteria
	 */
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
	
	/**
	 * Increase pollution in this neighborhood.
	 * @param pollutant the pollutant to be increased
	 * @param increment increment of the pollutant
	 */
	public void increasePollution(Pollutant pollutant, double increment) {
		pollution.get(pollutant).increasePollution(increment);
	}
	
	/**
	 * Returns the neighborhood's pollution
	 * @return pollution
	 */
	public Pollution getPollution() {
		return pollution;
	}
	
	/**
	 * Allows agents to give their satisfaction to this neighborhood.
	 * @param satisfaction the satisfaction to be given
	 * @param changedVehicle whether or not the agent changed his vehicle.
	 * @param changedMobility whether or not the agent changed mobility.
	 * @param fraud whether or not the agent frauded.
	 */
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
	
	public int[] getConfigAsArray() {
		int[] config = new int[3];
		config[0] = ((CritAirCriteria)deliveryLezCriteria).getCritAir().ordinal();
		config[1] = ((CritAirCriteria)privateLezCriteria).getCritAir().ordinal();
		config[2] = surveillance.getSurveillance().ordinal();
		return config;
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
	
	/**
	 * Returns main pollutions, in CO, N2O and PM
	 * @return main pollutants rates
	 */
	public Map<Pollutant, Double> getMainPollutions() {
		Map<Pollutant, Double> map = new HashMap<>();
		map.put(Pollutant.CO, pollution.get(Pollutant.CO).getAbsValue());
		map.put(Pollutant.N2O, pollution.get(Pollutant.N2O).getAbsValue());
		map.put(Pollutant.PM, pollution.get(Pollutant.PM).getAbsValue());
		return map;
	}

	/**
	 * Returns absolute pollution for CO.
	 * @return absolute pollution for CO
	 */
	public double getAbsPollution() {
		double pollution = this.pollution.get(Pollutant.CO).getAbsValue();
		return pollution;
	}
	
	/**
	 * Returns the difference between a reference configuration, and the current configuration in term of CO.
	 * @return difference of emissions
	 */
	private double getDifferencePollution() {
		return referencePollution.get(Pollutant.CO).getAbsValue() - this.pollution.get(Pollutant.CO).getAbsValue();
	}
	
	/**
	 * Returns absolute satisfaction given to this neightborhood
	 * @return absolute satisfaction
	 */
	public double getAbsSatisfaction() {
		double satisfaction = 0.0;
		for (double s : satisfactions) {
			satisfaction += s;
		}
		return satisfaction;
	}
	
	/**
	 * Returns the gain of this configuration.
	 * @return gain
	 */
	public double getGain() {
		//compute pollution
		double pollution = getDifferencePollution();
		pollution = pollution/3.5;
		//compute satisfaction
		double satisfaction = 0.0;
		for ( double satisfactionScore : satisfactions ) {
			satisfaction += satisfactionScore;
		}
		satisfaction = satisfaction/1;
		return pollution + satisfaction;
	}
	
	@Override
	public FeaturesDouble getLocalPerformances(List<String> labels) {
		FeaturesDouble features = new FeaturesDouble();
		
		for ( String label : labels ) {
			if ( label.equals("Pollution") ) {
				//compute pollution
				features.addFeature(getDifferencePollution());
			}
			else if ( label.equals("Satisfaction") ) {
				double satisfaction = 0;
				for ( double satisfactionScore : satisfactions ) {
					satisfaction += satisfactionScore;
				}
				
				features.addFeature(satisfaction);
			}
			else if ( label.equals("ChangedVehicles") ) {
				features.addFeature((double)nbChangedVehicles);
			}
			else if ( label.equals("changedMobilities") ) {
				features.addFeature((double)nbChangedMobilities);
			}
			else if ( label.equals("frauded") ) {
				features.addFeature((double)nbFraud);
			}
			else if ( label.equals("CritAirDelivery") ) {
				features.addFeature( ((double) ((CritAirCriteria) deliveryLezCriteria).getCritAir().ordinal()) );
			}
			else if ( label.equals("CritAirPrivate") ) {
				features.addFeature( ((double) ((CritAirCriteria) privateLezCriteria).getCritAir().ordinal()) );
			}
			else if ( label.equals("Surveillance") ) {
				features.addFeature ( (double)  surveillance.getSurveillance().ordinal() ) ;
			}
			else if ( label.equals("gain") ) {
				features.addFeature(getGain());
			}
			else if ( label.equals("reward") ) {
				double currentGain = getGain();
				if (currentGain < 0.1) {
					features.addFeature(-1.0);
				}
				else if ( isCurrentConfig(highestConfig) ) {
					features.addFeature(0.5);
				}
				else if ( currentGain > highestGain - 0.5 ) {
					highestGain = currentGain;
					setCurrentConfigTheHighest();
					features.addFeature(1.0);
				}
				else if ( currentGain > lastGain + 0.1 ) {
					features.addFeature(1.0);
				}
				else {
					features.addFeature(-1.0);
				}
				lastGain = currentGain;
			}
		}
		return features;
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
		case INCREASE_ALL_CRITERIA:
			((CritAirCriteria)deliveryLezCriteria).increaseCriteria();
			((CritAirCriteria)privateLezCriteria).increaseCriteria();
			break;
		case DECREASE_ALL_CRITERIA:
			((CritAirCriteria)deliveryLezCriteria).decreaseCriteria();
			((CritAirCriteria)privateLezCriteria).decreaseCriteria();
			break;
		default:
	}
	}

}

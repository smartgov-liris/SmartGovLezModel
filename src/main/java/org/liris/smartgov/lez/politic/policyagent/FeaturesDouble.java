package org.liris.smartgov.lez.politic.policyagent;

import java.util.ArrayList;
import java.util.List;

public class FeaturesDouble {

	private List<Double> features;
	
	public FeaturesDouble() {
		this.features = new ArrayList<>();
	}
	
	public FeaturesDouble(List<Double> features) {
		this.features = features;
	}
	
	public List<Double> getFeatures() {
		return features;
	}
	
	public void addFeature(double feature) {
		features.add(feature);
	}
	
	@Override
	public String toString() {
		String str = "";
		if(features.size() > 1) {
			for(int indexOfFeature = 0; indexOfFeature < features.size() - 1; indexOfFeature++) {
				str += features.get(indexOfFeature) + " ";//"\t";
			}
			return str += features.get(features.size() - 1);
		} else {
			return str += features.get(0);
		}
	}
	
	public void addFeaturesDouble(FeaturesDouble featuresDouble) {
		for(int i = 0; i< featuresDouble.features.size(); i++) {
			this.features.set(i, this.features.get(i) + featuresDouble.getFeatures().get(i));
		}
	}
	
}

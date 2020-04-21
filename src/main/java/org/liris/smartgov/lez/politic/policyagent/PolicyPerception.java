package org.liris.smartgov.lez.politic.policyagent;

public class PolicyPerception {

	private FeaturesDouble position = new FeaturesDouble();
	
	public PolicyPerception(FeaturesDouble variables) {
		this.position = variables;
	}
	
	public FeaturesDouble getPosition() {
		return this.position;
	}
	
	@Override
	public String toString() {
		String str = "[";
		for(int i = 0; i < position.getFeatures().size() - 1; i++) {
			str += position.getFeatures().get(i) + ", ";
		}
		str += position.getFeatures().get(position.getFeatures().size() - 1) + "]";
		return str;
	}
	
}
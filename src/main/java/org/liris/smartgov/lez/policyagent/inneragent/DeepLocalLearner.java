package org.liris.smartgov.lez.policyagent.inneragent;

import java.util.List;

import org.liris.smartgov.lez.core.environment.Structure;
import org.liris.smartgov.lez.policyagent.PolicyAction;
import org.liris.smartgov.lez.policyagent.learning.strategy.NNBest;

public class DeepLocalLearner extends LocalLearner {

	public DeepLocalLearner(
			Structure structure, 
			String id, 
			List<String> labels, 
			String strategy,
			int nbActions,
			List<PolicyAction> policyActions) {
		super(structure, id, labels, strategy, nbActions, policyActions);
		
	}
	
	@Override
	public PolicyAction proposeAction() {
		if(explorationMethod instanceof NNBest) {
			((NNBest) explorationMethod).setLastPerception(currentPerception.getPosition());
		}
		return explorationMethod.chooseAction();
	}
	
	@Override
	public void setLastAction(PolicyAction action) {
		this.action = action;
		explorationMethod.setLastAction(action);
	}

}

package org.liris.smartgov.lez.politic.policyagent;

import java.util.ArrayList;
import java.util.List;

/**
 * PolicyAction stores name of every available policy action in SmartGov.
 * A PolicyAction shapes a structure implementing ActionableByPolicyAgent.
 * @author spageaud
 *
 */
public enum PolicyAction {

	//WARNING : ALL AVAILABLE ACTIONS SHOULD BE IN THE FIRST NUMBERS AS IT IS STOCKED IN ARRAYS FOR THE PYTHON LEARNER
	//(if I choose not to add increase_all_criteria to available actions of my learners,
	//then I should switch its number to 9 or more, otherwise there might be out of bound exceptions
	NO_ACTION(-1),
	INCREASE_SURVEILLANCE(0),
	DECREASE_SURVEILLANCE(1),
	INCREASE_DELIVERIE_CRITERIA(2),
	DECREASE_DELIVERIE_CRITERIA(3),
	INCREASE_PRIVATE_CRITERIA(4),
	DECREASE_PRIVATE_CRITERIA(5),
	DO_NOTHING(6),
	INCREASE_ALL_CRITERIA(7),
	DECREASE_ALL_CRITERIA(8),
	NOTHING(100),
	SPLIT(101),
	MERGE(102),
	ROLLBACK(103),
	KEEP(104);

	private final int index;
	
	//https://stackoverflow.com/questions/6692664/how-to-get-enum-value-from-index-in-java
	public static final List<PolicyAction> actions = new ArrayList<PolicyAction>() {/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	{ for (PolicyAction action : PolicyAction.values()) add(action); }};
	
	public static final List<PolicyAction> specialActions = new ArrayList<PolicyAction>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			add(NOTHING);
			add(SPLIT);
			add(MERGE);
			add(ROLLBACK);
			add(KEEP);
		}
	};
	
	PolicyAction(int index){
		this.index = index;
	}
	
	public static final PolicyAction getActionFrom(String str) {
		switch (str) {
			case "increase_private_c":
				return PolicyAction.INCREASE_PRIVATE_CRITERIA;
			case "decrease_private_c":
				return PolicyAction.DECREASE_PRIVATE_CRITERIA;
			case "increase_deliverie_c":
				return PolicyAction.INCREASE_DELIVERIE_CRITERIA;
			case "decrease_deliverie_c":
				return PolicyAction.DECREASE_DELIVERIE_CRITERIA;
			case "increase_surveillance":
				return PolicyAction.INCREASE_SURVEILLANCE;
			case "decrease_surveillance":
				return PolicyAction.DECREASE_SURVEILLANCE;
			case "increase_all_c":
				return PolicyAction.INCREASE_ALL_CRITERIA;
			case "decrease_all_c":
				return PolicyAction.DECREASE_ALL_CRITERIA;
			case "do_nothing":
				return PolicyAction.DO_NOTHING;
			default:
				return null;
		}
	}
	
	public static final PolicyAction getSpecialActionFrom(String str) {
		str = str.toLowerCase();
		switch (str) {
			case "merge":
				return PolicyAction.MERGE;
			case "split":
				return PolicyAction.SPLIT;
			case "rollback":
				return PolicyAction.ROLLBACK;
			case "keep":
				return PolicyAction.KEEP;
			default:
				return null;
		}
	}
	
	public int getIndex() {
		return index;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return super.toString();
	}
}

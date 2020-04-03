package org.liris.smartgov.lez.politic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;

import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.politic.manager.AbstractManager;
import org.liris.smartgov.lez.politic.manager.ManagerQLearningScenario;
import org.liris.smartgov.lez.politic.policyagent.PolicyAgent;
import org.liris.smartgov.lez.politic.policyagent.inneragent.DeepLocalLearner;
import org.liris.smartgov.lez.politic.policyagent.inneragent.InnerAgent;
import org.liris.smartgov.simulator.core.environment.SmartGovContext;
import org.liris.smartgov.lez.core.simulation.scenario.DeliveriesScenario;



public class PoliticalVariables {
	public static Map<String, String> variables = new HashMap<>();
	public static List<PolicyAgent> policyAgents = new ArrayList<>();
	public static List<InnerAgent> innerAgentsGlobal = new ArrayList<>();
	public static AbstractManager manager;
	
	//Manage policy agent creation and allocation
	public static int POLICY_AGENT_MAX; //Max policy agents in the simulation (not specified at the moment)
	public static List<String> policyAgentIDBuffer = new ArrayList<>(); //id of policy agents during merge or split
	public static Queue<Integer> policyAgentStockId = new LinkedList<>(); //id of available policy agents for creation
	public static Map<String, List<Integer>> policyAgentIDMerged = new HashMap<>();
	
	public PoliticalVariables (SmartGovContext context) {
		
		try {
			loadVariables(((LezContext)context).getFileLoader().load("politicalLayer"));
			manager = new ManagerQLearningScenario();
		}
		catch (ClassCastException e) {
			e.printStackTrace();
		}
	}
	
	public void loadVariables (File configFile) {
		Scanner input;
		try {
			input = new Scanner(configFile);
			
			while(input.hasNext()) {
			    String nextLine = input.nextLine();
			    if(!nextLine.contains("#")){
			    	if(nextLine.contains(",")){
			    		//Indicators
			    		String lines[] = nextLine.split(":");
			    		String indicators[] = lines[1].split(",");
			    	} else {
			    		String lines[] = nextLine.split(":");
				    	variables.put(lines[0], lines[1]);
			    	}
			    }
			}
			input.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public static String requestPolicyAgentID() {
		if(policyAgentStockId.peek()==null) {
			return String.valueOf(policyAgents.size());
		} else {
			return String.valueOf(policyAgentStockId.poll());
		}
	}
	
	/**
	 * Remove policy agent from the list and store its id for the duration of the merge.
	 * @param mergeManager
	 * @param idOfMergedAgent
	 */
	public static void storePolicyAgentIDForMerge(String mergeManager, int idOfMergedAgent) {
		policyAgents.set(idOfMergedAgent, null);
		if(policyAgentIDMerged.containsKey(mergeManager)) {
			policyAgentIDMerged.get(mergeManager).add(idOfMergedAgent);
		} else {
			List<Integer> idsOfMergedAgents = new ArrayList<>();
			idsOfMergedAgents.add(idOfMergedAgent);
			policyAgentIDMerged.put(mergeManager, idsOfMergedAgents);
			
		}
	}
	
	public static void removePolicyAgentFromList(int index) {
		policyAgents.set(index, null);
	}
	
	public static void updatePolicyAgentBuffer(String id) {
		if(!policyAgentIDBuffer.contains(id)) {
			policyAgentIDBuffer.add(id);
		} else {
			policyAgentIDBuffer.remove(id);
		}
	}
	
	/**
	 * Delete stored IDs for merge.
	 * @param mergeManager
	 */
	public static void clearMergeAgents(String mergeManager) {
		if(policyAgentIDMerged.containsKey(mergeManager)) {
			policyAgentIDMerged.remove(mergeManager);
		}
	}
}
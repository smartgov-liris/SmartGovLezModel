package org.liris.smartgov.lez.politic.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Map.Entry;

import org.joda.time.DateTime;
import org.joda.time.LocalTime;
import org.liris.smartgov.lez.core.simulation.files.FileName;
import org.liris.smartgov.lez.core.simulation.files.FilePath;
import org.liris.smartgov.lez.core.simulation.files.FilesManagement;
import org.liris.smartgov.lez.core.simulation.files.JSONWriter;
import org.liris.smartgov.lez.politic.PoliticalVar;
import org.liris.smartgov.lez.politic.policyagent.PolicyAction;
import org.liris.smartgov.lez.politic.policyagent.PolicyAgent;

public class ManagerQLearningScenario extends AbstractManager {
	
	private String currentPhase;
	public boolean needToStop;
	protected String globalGainFile = "global_gain.txt";
	
	public ManagerQLearningScenario(){
		super();
		
		indexOfAction = 0;
		
		parseConfigFile();

		if (Integer.parseInt(PoliticalVar.variables.get("learning")) == 1) {
			currentPhase = "learning";
			leaveObservationPhase();
			for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
				policyAgent.applyRandomActionToStructures();
			}
		} else if(Integer.parseInt(PoliticalVar.variables.get("validation")) == 1) {
			currentPhase = "validation";
			validationPhase = true;
		}
		if(PoliticalVar.variables.get("split").equals("1")) {
			PoliticalVar.policyAgents.get(0).splitControlGroup();
		}
	}

	@Override
	public void live() {
		/*List<String> lines = new ArrayList<>();
		lines.add("");
		lines.add("--- Nouvelle simulation ---");
		lines.add("");
		FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, "Pollution.txt", lines);*/
		if(observationPhase) {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){
				this.currentTrialIndex = 0;
			}
			currentTrialIndex++;
		} else {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){	
				saveGlobalGain();
				currentIteration++;
				saveTime();
				currentTrialIndex = 1;
				randomStateGenerator(true);
				
			} else {
				currentTrialIndex++;
			}
		}
		saveManagerCounters();
	}
	
	private void callPolicyAgents() {
		for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
			if(policyAgent != null) {
				policyAgent.live();
			}
		}
	}

	private void leaveObservationPhase() {
		observationPhase = false;

		learningPhase = true;
	}

	private void saveGlobalGain() {
		List<String> lines = new ArrayList<>();
		double gain = 0.0;
		for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
			if(policyAgent != null) {
				gain = policyAgent.getLastGain();
			}
		}
		lines.add(currentIteration + "," + gain);
		FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, globalGainFile, lines);
	}
	
	@Override
	protected void init() {
		restartCounter = 0;
		currentTrialIndex = 0;
		currentSimulationIndex = 1;
		needToStop = false;
		saveManagerCounters();
		timeStamp = createTimeStamp();
	}
	
	protected void saveTime() {
		Instant now = Instant.now();
		Date date = new Date();
		String datelog = "[Time] (" + currentIteration + ")| Date: " + date.toString() + " | Iteration time: " + formatter.format(Duration.between(currentInstant, now).toMillis()) + " | Total time: " + formatter.format((System.currentTimeMillis() - beginTime)) + " |";
		currentInstant = now;
		FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, FileName.MANAGER_LOGS, datelog);
	}
	
	protected void randomStateGenerator(boolean generateRandomState) {
		if(generateRandomState) {
			//Try with a limited number of trials and a cumulative reward
			if(NUMBER_OF_SIMULATIONS_BEFORE_RESTART == currentSimulationIndex) {
				recentlyReset = true;
				restartCounter ++;
				if ( restartCounter > Integer.parseInt(PoliticalVar.variables.get("nb_sequences")) ) {
					needToStop = true;
				}
				currentSimulationIndex = 0;
			} else {
				List<String> actions = new ArrayList<>();
				List<PolicyAgent> agentsWithSpecificActions = new ArrayList<>();

				boolean keepAction = false;
				
				for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
					if(policyAgent != null) {
						//1) Do the special action first
						PolicyAction currentSpecificAction = policyAgent.getLastSpecialAction();
						if(currentSpecificAction != PolicyAction.NOTHING) {
							agentsWithSpecificActions.add(policyAgent);
						}
						if(currentSpecificAction == PolicyAction.KEEP) {
							keepAction = true;
						}
					}
				}
				//*/ TODO: Bug source ? Double fusion 29/06/19
				for(int i = 0; i < agentsWithSpecificActions.size(); i++) {
					if(agentsWithSpecificActions.get(i) != null) {
						PolicyAgent policyAgent = agentsWithSpecificActions.get(i);
						actions.add(policyAgent.applyPolicyAction(policyAgent.getLastSpecialAction()));
					}
				}
				//*/
				if(keepAction) {
					JSONWriter.writePolicyAgents(FilePath.currentLocalLearnerFolder, 
							currentIteration + "_" + FileName.PolicyAgentsFile,
							PoliticalVar.policyAgents);
				}
				
				for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
					if(policyAgent != null) {
						//2) Do the normal action of the remaining agents
						PolicyAction currentAction = policyAgent.getLastAction();
						actions.add(policyAgent.applyPolicyAction(currentAction));
						if(currentPhase.equals("learning")) {
							policyAgent.updateLocalLearners(currentAction);
						}
					}
				}
				recentlyReset = false;
				currentSimulationIndex++;
				saveActionsPerIteration(actions);
			}
		} else {
			//No limited number of trials before hard reset
			for(PolicyAgent policyAgent : PoliticalVar.policyAgents) {
				if(policyAgent != null) {
					if(currentPhase.equals("learning")) {
						policyAgent.askAndUpdateLocalLearners();
					} else if(currentPhase.equals("validation")) {
						policyAgent.askLocalLearners();
					}
				}
			}
			currentSimulationIndex++;
		}
	}
	
	protected void saveActionsPerIteration(List<String> actions) {
		List<String> lines = new ArrayList<>();
		String line = currentIteration + ")";
		for(int i = 0; i < actions.size(); i++) {
			line += actions.get(i);
		}
		lines.add(line);
		FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, FileName.MANAGER_ACTIONS, lines);
	}
	
	protected void saveConfigOfSimulation(List<String> additionnalInfo) {
		System.out.println("Save parameters for current simulations.");
		List<String> lines = new ArrayList<>();
		lines.add("Time: " + timeStamp + ".");
		for(Entry<String, String> value : PoliticalVar.variables.entrySet()) {
			lines.add(value.getKey() + ": " + value.getValue());
		}
		FilesManagement.writeToFile(FilePath.currentLocalLearnerFolder, FileName.MANAGER_PARAMETERS_FILE, lines);
		if(additionnalInfo != null && !additionnalInfo.isEmpty()) {
			
		}
	}

}
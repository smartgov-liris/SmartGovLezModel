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
import org.liris.smartgov.lez.politic.PoliticalVariables;
import org.liris.smartgov.lez.politic.policyagent.PolicyAgent;

public class ManagerQLearningScenario extends AbstractManager {

	//Behavior change
	private int windowSize = 5;
	private double lowBoundForStateChange = 0.6;
	private double highBoundForStateChange = 1.0;
	
	private String currentPhase;
	
	//Initial state
	private Map<String, String> initialStateConfig;
	
	public ManagerQLearningScenario(){
		super();
		
		indexOfAction = 0;
		
		initialStateConfig = new HashMap<>();

		if (Integer.parseInt(PoliticalVariables.variables.get("learning")) == 1) {
			currentPhase = "learning";
			leaveObservationPhase();
			for(PolicyAgent policyAgent : PoliticalVariables.policyAgents) {
				policyAgent.applyRandomActionToStructures();
			}
		} else if(Integer.parseInt(PoliticalVariables.variables.get("validation")) == 1) {
			currentPhase = "validation";
			validationPhase = true;
			initialStateConfig = parseInitialStateFile(FilePath.policyFolder + FileName.INITIAL_STATE);
			createInitialState();
			recentlyReset = true;
		}
		if(PoliticalVariables.variables.get("split").equals("1")) {
			PoliticalVariables.policyAgents.get(0).splitControlGroup();
		}
		
		clearAgents(); //Force a reset
	}

	@Override
	public void live() {
			
		if(observationPhase) {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){
				this.currentTrialIndex = 0;
			}
			resetStateOfSimulation();
			currentTrialIndex++;
		} else {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){
				currentIteration++;
				saveTime();
				currentTrialIndex = 0;
				randomStateGenerator(true);
			} else {
				currentTrialIndex++;
			}
			resetStateOfSimulation();
		}
		saveManagerCounters();
	}
	
	private void callPolicyAgents() {
		for(PolicyAgent policyAgent : PoliticalVariables.policyAgents) {
			if(policyAgent != null) {
				policyAgent.live();
			}
		}
	}

	private void leaveObservationPhase() {
		observationPhase = false;

		learningPhase = true;
	}

	@Override
	protected void init() {
		this.iterationCounter = 1;
		this.restartCounter = 0;
		
		iterationCounter = 0;
		currentTrialIndex = 0;
		currentSimulationIndex = 0;
		currentSimulationIndex = 1;
		saveManagerCounters();
		timeStamp = createTimeStamp();
	}
	
	protected void randomStateGenerator(boolean generateRandomState) {
		if(generateRandomState) {
			//Try with a limited number of trials and a cumulative reward
			if(NUMBER_OF_SIMULATIONS_BEFORE_RESTART == currentSimulationIndex) {
				recentlyReset = true;
				if(currentPhase.equals("learning")) {
					randomState();
				} else if(currentPhase.equals("validation")) {
					createInitialState();
				}
				currentSimulationIndex = 0;
			} else {
				List<String> actions = new ArrayList<>();
				List<PolicyAgent> agentsWithSpecificActions = new ArrayList<>();

				boolean keepAction = false;
				
				for(PolicyAgent policyAgent : EnvVar.policyAgents) {
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
							EnvVar.policyAgents);
				}
				
				for(PolicyAgent policyAgent : EnvVar.policyAgents) {
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
			for(PolicyAgent policyAgent : EnvVar.policyAgents) {
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
	
	protected void saveTime() {
		Instant now = Instant.now();
		Date date = new Date();
		String datelog = "[Time] (" + currentIteration + ")| Date: " + date.toString() + " | Iteration time: " + formatter.format(Duration.between(currentInstant, now).toMillis()) + " | Total time: " + formatter.format((System.currentTimeMillis() - beginTime)) + " |";
		currentInstant = now;
		FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, FileName.MANAGER_LOGS, datelog);
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

}
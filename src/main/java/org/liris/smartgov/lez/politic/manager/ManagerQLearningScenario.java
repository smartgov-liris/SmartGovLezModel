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
			recentlyReset = true;
		}
		if(PoliticalVariables.variables.get("split").equals("1")) {
			PoliticalVariables.policyAgents.get(0).splitControlGroup();
		}
	}

	@Override
	public void live() {
			
		if(observationPhase) {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){
				this.currentTrialIndex = 0;
			}
			currentTrialIndex++;
		} else {
			callPolicyAgents();
			if(NUMBER_OF_ITERATIONS_BEFORE_APPLYING_POLICIES == currentTrialIndex){
				currentIteration++;
				saveTime();
				currentTrialIndex = 0;
			} else {
				currentTrialIndex++;
			}
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
	
	protected void saveConfigOfSimulation(List<String> additionnalInfo) {
		System.out.println("Save parameters for current simulations.");
		List<String> lines = new ArrayList<>();
		lines.add("Time: " + timeStamp + ".");
		for(Entry<String, String> value : PoliticalVariables.variables.entrySet()) {
			lines.add(value.getKey() + ": " + value.getValue());
		}
		FilesManagement.writeToFile(FilePath.currentLocalLearnerFolder, FileName.MANAGER_PARAMETERS_FILE, lines);
		if(additionnalInfo != null && !additionnalInfo.isEmpty()) {
			
		}
	}

}
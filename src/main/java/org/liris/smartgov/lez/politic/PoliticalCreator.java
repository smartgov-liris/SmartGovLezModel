package org.liris.smartgov.lez.politic;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.liris.smartgov.lez.core.environment.Structure;
import org.liris.smartgov.lez.core.environment.lez.Environment;
import org.liris.smartgov.lez.core.simulation.files.FilePath;
import org.liris.smartgov.lez.politic.policyagent.Perimeter;
import org.liris.smartgov.lez.politic.policyagent.PolicyAction;
import org.liris.smartgov.lez.politic.policyagent.PolicyAgent;
import org.liris.smartgov.lez.politic.socket.ClientCommunication;
import org.liris.smartgov.lez.politic.socket.Server;

import org.apache.commons.io.filefilter.DirectoryFileFilter;

/**
 * Creates the main parts of the political layer.
 * @author alban
 *
 */
public class PoliticalCreator {
	static int simulationIndex = 1;
	
	public static void createPoliticalLayer(Environment environment) {
		createFolder();
		startServer();
		createPolicyAgent(environment);
		
	}
	
	/**
	 * Create a specific folder using current date and increment folder 
	 * index using previous folders of the same date.
	 */
	protected static void createFolder() {
		ZoneId z = ZoneId.of("Europe/Paris");
		LocalDate ld = LocalDate.now(z);
		int dayOfMonth  = ld.getDayOfMonth();
		String day = (dayOfMonth < 10) ? ("0" + dayOfMonth) : String.valueOf(dayOfMonth);
		int monthOfYear = ld.getMonthValue();
		String month = (monthOfYear < 10) ? ("0" + monthOfYear) : String.valueOf(monthOfYear);
		int yearInt = ld.getYear();
		String year = String.valueOf(yearInt).substring(2);
		String date = day + month + year;
		
		File directory = new File(FilePath.localLearnerFolder);
		File[] subdirs = directory.listFiles((FileFilter) DirectoryFileFilter.DIRECTORY);
		//if(subdirs.length != 0) {
			for (File dir : subdirs) {
				String[] dirDate = dir.getName().split("_");
				if(dirDate[0].equals(date)) {
					if(Integer.parseInt(dirDate[1]) >= simulationIndex) {
						simulationIndex = Integer.parseInt(dirDate[1]) + 1;
					}
				}
			}
		//}
		
		String scenarioID = Integer.parseInt(PoliticalVar.variables.get("scenarioID")) > 0 ? "_" + PoliticalVar.variables.get("scenarioID") : "";
		String dirName = "";
		if(PoliticalVar.variables.get("simulation_debug").equals("0")) {
			dirName = date + "_" + simulationIndex + "_" + PoliticalVar.variables.get("scenario") + scenarioID + File.separator;
		} else {
			dirName = date + "_" + simulationIndex + "_debug_" + PoliticalVar.variables.get("scenario") + scenarioID + File.separator;
		}
		new File(FilePath.localLearnerFolder + dirName).mkdirs();
		FilePath.currentLocalLearnerFolder = FilePath.localLearnerFolder + dirName;
		
	}
	
	/**
	 * Start the server to communicate with python learning scripts.
	 */
	protected static void startServer() {
		//create folder for current simulation
		//createFolder();
		FilePath.currentAgentDetailsFolder = FilePath.humanAgentFolder + "scenario//" + 
				PoliticalVar.variables.get("scenario") + "//" +
				PoliticalVar.variables.get("scenarioID") + "//";
		//Update server port with simulationIndex
		//*/
		ClientCommunication.port += simulationIndex;
		if(PoliticalVar.variables.get("server_debug").equals("0")) {
			Server.startServer(FilePath.externalSourceFolder + "server.py -p " + ClientCommunication.port, "python");
		} else {
			//Use this when bug in python server
			try {
				Runtime.getRuntime().exec("cmd /c start extsrc\\server.bat " + ClientCommunication.port);
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		//*/ Add 5 seconds delay to load Tensorflow in server
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		//*/
	}
	
	/**
	 * Create policy agents in charge of political decisions
	 * @param environment environment of the the simulation
	 */
	public static void createPolicyAgent(Environment environment) {
		List<PolicyAction> actions = new ArrayList<>();
		if (Integer.parseInt(PoliticalVar.variables.get("distinct_criterias")) == 1 ) {
			actions.add(PolicyAction.INCREASE_DELIVERIE_CRITERIA);
			actions.add(PolicyAction.INCREASE_PRIVATE_CRITERIA);
			actions.add(PolicyAction.DECREASE_DELIVERIE_CRITERIA);
			actions.add(PolicyAction.DECREASE_PRIVATE_CRITERIA);
		}
		else {
			actions.add(PolicyAction.INCREASE_ALL_CRITERIA);
			actions.add(PolicyAction.DECREASE_ALL_CRITERIA);
		}
		
		actions.add(PolicyAction.DECREASE_SURVEILLANCE);
		actions.add(PolicyAction.INCREASE_SURVEILLANCE);
		actions.add(PolicyAction.DO_NOTHING);
		
		List<PolicyAction> specialActions = getSpecialPolicyActions() ;
		List<Structure> structures = new ArrayList<>();
		int cpt = 0;
		for(Structure structure : environment.getNeighborhoods().values()) {
			structures.add(structure);
			Perimeter perimeter = new Perimeter(structures);
			PoliticalVar.policyAgents.add(new PolicyAgent(String.valueOf(cpt), perimeter, actions, specialActions));
			structures = new ArrayList<>();
		}
		
		/*Perimeter perimeter = new Perimeter(structures);
		
		PoliticalVar.policyAgents.add(new PolicyAgent("0", perimeter, actions, specialActions));*/
	}
	
	/**
	 * Returns the available special actions
	 * @return special actions
	 */
	protected static List<PolicyAction> getSpecialPolicyActions() {
		List<PolicyAction> policyActions = new ArrayList<>();
		policyActions.add(PolicyAction.MERGE);
		policyActions.add(PolicyAction.SPLIT);
		policyActions.add(PolicyAction.ROLLBACK);
		policyActions.add(PolicyAction.KEEP);
		return policyActions;
	}
	
}

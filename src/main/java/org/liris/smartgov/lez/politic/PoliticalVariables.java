package org.liris.smartgov.lez.politic;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.politic.policyagent.PolicyAgent;
import org.liris.smartgov.lez.politic.policyagent.inneragent.DeepLocalLearner;
import org.liris.smartgov.lez.politic.policyagent.inneragent.InnerAgent;

import org.liris.smartgov.lez.core.simulation.scenario.DeliveriesScenario;



public class PoliticalVariables {
	public static Map<String, String> variables = new HashMap<>();
	
	public PoliticalVariables (LezContext context) {
		
		loadVariables(context.getFileLoader().load("politicalLayer"));
		
		
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
}
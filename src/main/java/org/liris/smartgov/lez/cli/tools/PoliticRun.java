package org.liris.smartgov.lez.cli.tools;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.lez.core.environment.lez.EnvironmentSerializer;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.core.simulation.ExtendedSimulationBuilder;
import org.liris.smartgov.lez.core.simulation.ExtendedSimulationRuntime;
import org.liris.smartgov.lez.core.simulation.ExtendedSmartGov;
import org.liris.smartgov.lez.core.simulation.files.FilePath;
import org.liris.smartgov.lez.core.simulation.files.FilesManagement;
import org.liris.smartgov.lez.core.simulation.scenario.DeliveriesScenario;
import org.liris.smartgov.lez.politic.PoliticalVar;
import org.liris.smartgov.lez.politic.manager.ManagerQLearningScenario;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.events.SimulationStopped;


/**
 * Politic run task, launches simulations and RL algorithms.
 * @author alban
 *
 */
public class PoliticRun {
	public static final Logger logger = LogManager.getLogger(PoliticRun.class);
	static int iterator;
	
	
	public static void main(String[] args) throws ParseException {
		iterator = 0;
		
		logger.getLevel();
		Options opts = new Options();
		
		opts.addOption(new Option("h", "help", false, "Displays this help message"));
		
		Option config = new Option("c", "config-file", true, "Input configuration file");
		config.setArgName("file");
		opts.addOption(config);
		
		Option maxTicks = new Option("t", "max-ticks", true, "Max ticks");
		maxTicks.setArgName("int");
		opts.addOption(maxTicks);
		
		Option iterations = new Option("i", "max-iterations", true, "Max iterations of the simulation");
		iterations.setArgName("int");
		opts.addOption(iterations);
		
		Option agents = new Option("a", "nb-agents", true, "Number of agents in simulation");
		agents.setArgName("int");
		opts.addOption(agents);
		
		opts.addOption(new Option("p", "pretty-print", false, "Enables JSON pretty printing"));
		
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(opts, args);
		
		if(cmd.hasOption("h")) {
			String header = "Run the simulations and learn optimal policies";
			String footer = "\n"
					+ "Results at the end of each sequence are saved in output_political folder.";
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("smartgovlez run", header, opts, footer, true);
			return;
		}
		
		final String configFile;
		
		if(cmd.hasOption("c")) {
			configFile = cmd.getOptionValue("c");
		} else {
			configFile = "input/static_config_lez.properties";
		}
		
		final int nb_iterations;
		if( cmd.hasOption("i") ) {
			nb_iterations = Integer.valueOf(cmd.getOptionValue("i"));
		}
		else {
			nb_iterations = Integer.MAX_VALUE;
		}
		
		final int maxTicksValue;
		if(cmd.hasOption("t")) {
			maxTicksValue = Integer.valueOf(cmd.getOptionValue("t"));
		} else {
			maxTicksValue = 3600 * 24;
		}
		
		if (cmd.hasOption("a")) {
			DeliveriesScenario.nbAgents = Integer.valueOf(cmd.getOptionValue("a"));
		} else {
			DeliveriesScenario.nbAgents = Integer.MAX_VALUE;
		}
		
		LezContext ctxt = new LezContext(configFile, true);
		
        ExtendedSmartGov smartGov = new ExtendedSmartGov(
        		ctxt, new ExtendedSimulationRuntime(ctxt),
        		new ExtendedSimulationBuilder(ctxt)
        		);
        
        SmartGov.getRuntime().addSimulationStoppedListener((event) -> {
			int simulationTime = (int) Math.floor(SmartGov.getRuntime().getTickCount() * SmartGov.getRuntime().getTickDuration());
			int days = (int) Math.floor(simulationTime / (24 * 3600));
			int hours = (int) Math.floor((simulationTime - days * 24 * 3600) / 3600);
			int minutes = (int) Math.floor((simulationTime - days * 24 * 3600 - hours * 3600) / 60);
			int seconds = (int) Math.floor((simulationTime - days * 24 * 3600 - hours * 3600 - minutes * 60));
			logger.info(
				"Total simulated period : "
				+ days + " days, "
				+ hours + " hours, "
				+ minutes + " minutes, "
				+ seconds + "s"
				);
		});
        
        final long simulationStart = System.nanoTime();
        
        EventHandler<SimulationStopped> relaunchSimulation = new EventHandler<SimulationStopped>() {
        	
			@Override
			public void handle(SimulationStopped event) {
				
				long simulationEnd = System.nanoTime();
				logger.info("It took " + (int)((simulationEnd - simulationStart) / 1E9) + " seconds to play the simulation" );
				
				PoliticalVar.manager.live();
				
				if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).needToStop || 
						PoliticalVar.manager.getCurrentIteration() + 1 >= nb_iterations ) {
					
					EnvironmentSerializer.SerializeEnvironment(FilePath.outputFolder,
							((DeliveriesScenario) ctxt.getScenario()).getEnvironment(), 999);
				}
				

				else {
					if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).isRecentlyReset() ) {
						EnvironmentSerializer.SerializeEnvironment(FilePath.outputFolder,
								((DeliveriesScenario) ctxt.getScenario()).getEnvironment(), PoliticalVar.manager.getRestartCounter());
						double cpt = 0.0;
						for (Neighborhood n : ((DeliveriesScenario) ctxt.getScenario()).getEnvironment().getNeighborhoods().values()) {
							List<String> l = new ArrayList<>();
							l.add("gain");
							cpt += n.getLocalPerformances(l).getFeatures().get(0);
						}
						FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, "gain.txt", PoliticalVar.manager.getRestartCounter() 
								+ " " + cpt);
						
						if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).getRestartCounter() >= 
								Integer.parseInt(PoliticalVar.variables.get("nb_epoch")) - 5  || 
								 ((ManagerQLearningScenario)(PoliticalVar.manager)).getRestartCounter() % 5 == 0) {
							//for the last 5 epochs, we start from the base configuration
							ctxt.resetConfiguration();
						}
						else {
							ctxt.setPartiallyRandomConfiguration();
						}
					}
					
					logger.info("\n\n\n"
							+ "_________________________________ \n"
							+ "|                               | \n"
							+ "|     Relaunching simulation    | \n"
							+ "|                               | \n"
							+ "|_______________________________| \n"
							+ "Iteration " + PoliticalVar.manager.getCurrentIteration());
					
					if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).getRestartCounter() % 80 == 0 ) {
						//every 80 iterations we make a slow reset
						ctxt.resetVariables(false);
					}
					else {
						ctxt.resetVariables(true);
					}
					
					ctxt.reload();
			        smartGov.restart(ctxt);
					
				
				/*if (iterator < 6) {
					if (iterator != 0) {
						double cpt = 0.0;
						for ( Neighborhood n : ((DeliveriesScenario) ctxt.getScenario()).getEnvironment().getNeighborhoods().values()) {
							List<String> l = new ArrayList<>();
							l.add("gain");
							cpt+= n.getLocalPerformances(l).getFeatures().get(0);
						}
					FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, "gagain.txt", iterator
							+ " " + cpt);
					}
					iterator ++;
					ctxt.changeConfiguration(iterator);
					ctxt.resetVariables(false);
					ctxt.reload();
			        smartGov.restart(ctxt);*/
				}
					
				}
				
			        
			        /*else {
					File outputFolder = new File(
							smartGov.getContext().getFileLoader().load("outputDir"),
							"simulation"
							);
					File agentOutput = new File(outputFolder, "agents_" + SmartGov.getRuntime().getTickCount() +".json");
					File arcsOutput = new File(outputFolder, "arcs_" + SmartGov.getRuntime().getTickCount() +".json");
					File pollutionPeeksOutput = new File(outputFolder, "pollution_peeks_" + SmartGov.getRuntime().getTickCount() +".json");
					
					
					ObjectMapper mapper;
	
					if(cmd.hasOption("p")) {
						mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
					}
					else {
						mapper = new ObjectMapper();
					}
					
					// logger.info("Saving agents state to " + agentOutput.getPath());
					// objectMapper.writeValue(agentOutput, smartGov.getContext().agents.values());
					
					logger.info("Saving arcs state to " + arcsOutput.getPath());
					Cli.writeOutput(smartGov.getContext().arcs.values(), arcsOutput, mapper);
					
					logger.info("Saving pollution peeks to " + pollutionPeeksOutput.getPath());
					Cli.writeOutput(Pollution.pollutionRatePeeks, pollutionPeeksOutput, mapper);
				}*/
        };
        
        SmartGov.getRuntime().addSimulationStoppedListener(relaunchSimulation);
        
		SmartGov.getRuntime().start((int) Math.floor(maxTicksValue));
	}
	
	
	
}

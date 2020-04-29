package org.liris.smartgov.lez.cli.tools;

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

public class PoliticRun {
	public static final Logger logger = LogManager.getLogger(PoliticRun.class);
	
	
	public static void main(String[] args) throws ParseException {
		
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
			String header = "Run the simutation, with the specified configuration.";
			String footer = "\n"
					+ "Raw results are written to the <outputDir>/simulation folder.\n"
					+ "The simulation runs until max ticks count as been reached (default to 10 days) or "
					+ "when the last round has ended.";
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
		
		LezContext ctxt = new LezContext(configFile);
		
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
				
				/*Map<String, Pollution> pollutionMap = ((DeliveriesScenario)ctxt.getScenario())
						.getEnvironment().getPollutionByNeighborhood();
				
				for (Pollution pollution : pollutionMap.values()) {
					logger.info(pollution.get(Pollutant.CO));
				}*/
				
				if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).needToStop || 
						PoliticalVar.manager.getCurrentIteration() + 1 >= nb_iterations ) {
					
					EnvironmentSerializer.SerializeEnvironment(FilePath.outputFolder,
							((DeliveriesScenario) ctxt.getScenario()).getEnvironment());
				}

				else {
					if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).isRecentlyReset() ) {
						FilesManagement.appendToFile(FilePath.currentLocalLearnerFolder, "reset.txt", 
								String.valueOf( PoliticalVar.manager.getCurrentIteration()));
						if ( ((ManagerQLearningScenario)(PoliticalVar.manager)).lastEpoch ) {
							ctxt.resetConfiguration();
						}
						else {
							ctxt.setRandomConfiguration();
						}

					}
					
					PoliticalVar.manager.live();
					
					logger.info("\n\n\n"
							+ "_________________________________ \n"
							+ "|                               | \n"
							+ "|     Relaunching simulation    | \n"
							+ "|                               | \n"
							+ "|_______________________________| \n\n");
					
					ctxt.resetVariables();
					ctxt.reload();
			        smartGov.restart(ctxt);
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
			}
        };
        
        SmartGov.getRuntime().addSimulationStoppedListener(relaunchSimulation);
        
		SmartGov.getRuntime().start((int) Math.floor(maxTicksValue));
	}
	
	
	
}

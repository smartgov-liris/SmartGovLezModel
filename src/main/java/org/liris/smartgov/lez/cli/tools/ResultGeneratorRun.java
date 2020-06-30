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
import org.liris.smartgov.lez.core.simulation.scenario.LezScenario;
import org.liris.smartgov.lez.politic.PoliticalVar;
import org.liris.smartgov.lez.politic.manager.ManagerQLearningScenario;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.events.SimulationStopped;

/**
 * ResultGeneratorRun task, launches simulations with random configuration.
 * @author alban
 *
 */
public class ResultGeneratorRun {
	public static final Logger logger = LogManager.getLogger(ResultGeneratorRun.class);
	static int iteration;
	
	public static void main(String[] args) throws ParseException {
		iteration = 0;
		
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
			String header = "Run simulations with random configurations and save results.";
			String footer = "\n"
					+ "Results are written in output/config.txt file";
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
			nb_iterations = 1000;
		}
		
		final int maxTicksValue;
		if(cmd.hasOption("t")) {
			maxTicksValue = Integer.valueOf(cmd.getOptionValue("t"));
		} else {
			maxTicksValue = 3600 * 24;
		}
		
		if (cmd.hasOption("a")) {
			LezScenario.setNbAgents(Integer.valueOf(cmd.getOptionValue("a")));
		} else {
			LezScenario.setNbAgents(Integer.MAX_VALUE);
		}
		
		LezContext ctxt = new LezContext(configFile, false);
		
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
				
				if (iteration < nb_iterations) {
					long simulationEnd = System.nanoTime();
					logger.info("It took " + (int)((simulationEnd - simulationStart) / 1E9) + " seconds to play the simulation" );
	
					double cpt_pollution = 0.0;
					double cpt_satisfaction = 0.0;
					String config = "";
					for (int i = 0 ; i < ((LezScenario) ctxt.getScenario()).getEnvironment().getNeighborhoods().size() ; i ++) {
						Neighborhood n = ((LezScenario) ctxt.getScenario()).getEnvironment().getNeighborhoods().get(String.valueOf(i));
						int[] configs = n.getConfigAsArray();
						if (i == 0) {
							config += configs[0] + "_" + configs[1] + "_" + configs[2];
						}
						else {
							config += "_" + configs[0] + "_" + configs[1] + "_" + configs[2];
						}
						cpt_pollution += n.getAbsPollution();
						cpt_satisfaction += n.getAbsSatisfaction();
					}
					iteration++;
					
					config += ":" + cpt_pollution + "_" + cpt_satisfaction;
					
					FilesManagement.appendToFile("output/", "config.txt", config);
	
					ctxt.setCompletelyRandomConfiguration();
	
					
					logger.info("\n\n\n"
							+ "_________________________________ \n"
							+ "|                               | \n"
							+ "|     Relaunching simulation    | \n"
							+ "|                               | \n"
							+ "|_______________________________| \n"
							+ "Iteration " + iteration);
					
					if ( iteration % 80 == 0 ) {
						//every 80 iterations we make a slow reset
						ctxt.resetVariables(false);
					}
					else {
						ctxt.resetVariables(true);
					}
					
					ctxt.reload();
			        smartGov.restart(ctxt);
				}
			}
        };
        
        SmartGov.getRuntime().addSimulationStoppedListener(relaunchSimulation);
        
		SmartGov.getRuntime().start((int) Math.floor(maxTicksValue));
	}
}

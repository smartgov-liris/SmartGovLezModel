package org.liris.smartgov.lez.cli.tools;

import java.io.File;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.liris.smartgov.lez.cli.Cli;
import org.liris.smartgov.lez.core.environment.LezContext;
import org.liris.smartgov.lez.core.environment.pollution.Pollution;
import org.liris.smartgov.lez.core.simulation.ExtendedSimulationRuntime;
import org.liris.smartgov.simulator.SmartGov;
import org.liris.smartgov.simulator.core.events.EventHandler;
import org.liris.smartgov.simulator.core.simulation.events.SimulationStopped;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class PoliticRun {
	public static final Logger logger = LogManager.getLogger(PoliticRun.class);
	
	
	
	
	public static void main(String[] args) throws ParseException {
		
		
		Options opts = new Options();
		
		opts.addOption(new Option("h", "help", false, "Displays this help message"));
		
		Option config = new Option("c", "config-file", true, "Input configuration file");
		config.setArgName("file");
		opts.addOption(config);
		
		Option maxTicks = new Option("t", "max-ticks", true, "Max ticks");
		maxTicks.setArgName("int");
		opts.addOption(maxTicks);
		
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
			configFile = "config.properties";
		}
		
		final int maxTicksValue;
		if(cmd.hasOption("t")) {
			maxTicksValue = Integer.valueOf(cmd.getOptionValue("t"));
		} else {
			maxTicksValue = 3600 * 24 * 10;
		}
		
		LezContext ctxt = new LezContext(configFile);
		
        SmartGov smartGov = new SmartGov(
        		ctxt, new ExtendedSimulationRuntime(ctxt)
        		);
        
        
        
        EventHandler<SimulationStopped> relaunchSimulation = new EventHandler<SimulationStopped>() {
        	
			@Override
			public void handle(SimulationStopped event) {
				
				if (true) {
			        logger.info(smartGov.getRuntime().getClock().getHour() + ":" + smartGov.getRuntime().getClock().getMinutes());
			        smartGov.restart(new LezContext(configFile));
			        
			        
				} /*else {
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
		
		SmartGov.getRuntime().start((int) Math.floor(maxTicksValue));
	}
	
	
	
}

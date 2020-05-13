package org.liris.smartgov.lez.core.environment.lez;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.cli.Cli;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class EnvironmentSerializer {
	
	public static void SerializeEnvironment(String filePath, Environment environment, int epoch) {
		ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        Map<String, Map<String, Object>> mapEnv = new HashMap<>();
        for (Neighborhood n : environment.getNeighborhoods().values()) {
        	Map<String, Object> mapNei = new HashMap<>();
        	mapNei.put("surveillance", n.getSurveillance().toString());
        	mapNei.put("private criteria", ((CritAirCriteria)n.getPrivateLezCriteria()).getCritAir().toString() );
        	mapNei.put("delivery criteria", ((CritAirCriteria)n.getDeliveryLezCriteria()).getCritAir().toString() );
        	mapNei.put("pollution", n.getMainPollutions());
        	mapNei.put("satisfaction", n.getAbsSatisfaction());
        	mapNei.put("perimeter", n.getPerimeter());
        	mapEnv.put(n.getID(), mapNei);
        }
        File envFile = new File(filePath, "environment" + epoch + ".json");
        Cli.writeOutput(mapEnv, envFile, mapper);
    }

}

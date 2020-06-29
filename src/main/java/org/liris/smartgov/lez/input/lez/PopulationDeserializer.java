package org.liris.smartgov.lez.input.lez;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.liris.smartgov.lez.core.agent.driver.personality.PersonalityType;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * Not used for now, but allows to create different kinds of population from a file.
 * @author alban
 *
 */
public class PopulationDeserializer extends StdDeserializer<Map<String, Map<PersonalityType, Double>>> {
	
	public PopulationDeserializer() {
		this(null);
	}

	protected PopulationDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Map<String, Map<PersonalityType, Double>> deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		Map<String, Map<PersonalityType, Double>> personalities = new HashMap<>();
		JsonNode jsonPersonality = p.getCodec().readTree(p);
		
		JsonNode groups = jsonPersonality.get("Population_type");
		
		for (JsonNode j : groups) {
			JsonNode population = j.get("population");
			
			Map<PersonalityType, Double> type = new HashMap<>();
			type.put(PersonalityType.POOR, Double.valueOf(population.get("Poor").asText()));
			type.put(PersonalityType.MEDIUM, Double.valueOf(population.get("Medium").asText()));
			type.put(PersonalityType.RICH, Double.valueOf(population.get("Rich").asText()));
			
			JsonNode neighborhoods = j.get("Neighborhoods");
			
			for (JsonNode neighborhood : neighborhoods) {
				personalities.put(neighborhood.asText(), type);
			}
			
		}
		
		/*for (Map.Entry<String, Map<PersonalityType, Double>> entry : personalities.entrySet()) {
			System.out.println(entry.getKey());
			for (Map.Entry<PersonalityType, Double> entry2 : entry.getValue().entrySet()) {
				System.out.println("  " + entry2.getKey() + " : " + entry2.getValue());
			}
		}*/
		
		return personalities;
	}
	
	public static Map<String, Map<PersonalityType, Double>> load(File personalityFile) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Map.class, new PopulationDeserializer());
		mapper.registerModule(module);
		
		return mapper.readValue(personalityFile, Map.class);
	}
	
}

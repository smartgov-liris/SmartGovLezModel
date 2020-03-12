package org.liris.smartgov.lez.input.lez;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import org.liris.smartgov.lez.core.environment.lez.Environment;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.Surveillance;

public class CritAirLezDeserializer extends StdDeserializer<Environment> {

	private static final long serialVersionUID = 1L;

	public CritAirLezDeserializer() {
		this(null);
	}
	
	protected CritAirLezDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Environment deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		JsonNode jsonLez = p.getCodec().readTree(p);
		
		JsonNode dimensions = jsonLez.get("dimensions");
		
		
		return new Environment(dimensions.get("north_bound").asDouble(),
				dimensions.get("south_bound").asDouble(),
				dimensions.get("west_bound").asDouble(),
				dimensions.get("east_bound").asDouble(),
				jsonLez.get("nb_squares").asInt(),
				CritAir.valueOf(jsonLez.get("deliveryAllowed").asText()),
				CritAir.valueOf(jsonLez.get("privateAllowed").asText()),
				Surveillance.valueOf(jsonLez.get("surveillance").asText()));
	}
	
	public static Environment load(File environmentFile) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		SimpleModule module = new SimpleModule();
		module.addDeserializer(Environment.class, new CritAirLezDeserializer());
		mapper.registerModule(module);
		
		return mapper.readValue(environmentFile, Environment.class);
	}

}

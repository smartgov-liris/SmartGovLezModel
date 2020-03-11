package org.liris.smartgov.lez.input.lez;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

import org.liris.smartgov.lez.cli.tools.Run;
import org.liris.smartgov.lez.core.environment.lez.Environment;
import org.liris.smartgov.lez.core.environment.lez.Neighborhood;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAir;
import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;
import org.liris.smartgov.lez.input.lez.CritAirLezDeserializer;
import org.liris.smartgov.simulator.urban.geo.utils.LatLon;

public class DeserializeLezTest {

	/**
	 * Test with only one big lez/neighborhood
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	
	@Test
	public void deserializeLezTest() throws JsonParseException, JsonMappingException, IOException {
		
		Environment environment = CritAirLezDeserializer.load(
				new File(this.getClass().getResource("dimension.json").getFile())
				);
		
		List<Neighborhood> allNeighborhoods = environment.getNeighborhoods();
		
		assertNotNull(allNeighborhoods);
		
		assertThat(
			allNeighborhoods.get(0).getLezCriteria() instanceof CritAirCriteria,
			is(true)
			);
		
		System.out.println(((CritAirCriteria) allNeighborhoods.get(0).getLezCriteria()).getAllowedCritAirs());
		
		assertThat(
			((CritAirCriteria) allNeighborhoods.get(0).getLezCriteria()).getAllowedCritAirs(),
			contains(
				CritAir.CRITAIR_1,
				CritAir.CRITAIR_2,
				CritAir.CRITAIR_3
				)
			);
				
		assertThat(
			Arrays.asList(allNeighborhoods.get(0).getPerimeter()),
			containsInAnyOrder(
				new LatLon(46.146, 4.4),
				new LatLon(46.146, 5.46),
				new LatLon(45.43, 4.4),
				new LatLon(45.43, 5.46)
				)
			);
	}
}

package org.liris.smartgov.lez.core.environment.lez;

import java.util.ArrayList;
import java.util.List;

import org.liris.smartgov.lez.core.environment.lez.criteria.CritAirCriteria;

public class EnvironmentSerializer {
	
	public static void SerializeEnvironment(String filePath, Environment environment) {
		List<String> lines = new ArrayList<>();
		lines.add("{");
		for (Neighborhood n : environment.getNeighborhoods().values()) {
			lines.add("  {");
			lines.add("    id : " + n.getID());
			lines.add("    {");
			lines.add("      surveillance : " + n.getSurveillance());
			lines.add("      private criteria : " + ((CritAirCriteria)n.getPrivateLezCriteria()).getCritAir() );
			lines.add("      delivery criteria : " + ((CritAirCriteria)n.getDeliveryLezCriteria()).getCritAir() );
			lines.add("      pollution : " + n.getAbsPollution());
			lines.add("      satisfaction : " + n.getAbsSatisfaction());
			lines.add("    }");
			lines.add("  }");
		}
		lines.add("}");
	}
}

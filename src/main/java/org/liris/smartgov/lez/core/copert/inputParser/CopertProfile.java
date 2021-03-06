package org.liris.smartgov.lez.core.copert.inputParser;

import java.util.ArrayList;
import java.util.List;

import org.liris.smartgov.lez.core.copert.tableParser.CopertHeader;

public class CopertProfile {

	private CopertHeader header;
	private List<CopertRate> values;
	
	public CopertProfile() {
		values = new ArrayList<>();
	}
	
	public CopertProfile(List<CopertRate> values) {
		this.values = values;
	}
	
	public CopertHeader getHeader() {
		return header;
	}
	
	public List<CopertRate> getValues() {
		return values;
	}
}

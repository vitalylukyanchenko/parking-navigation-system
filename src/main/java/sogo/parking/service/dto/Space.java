package sogo.parking.service.dto;

import java.util.Map;

public class Space {
	private String id;
	private Map<String, String> navigationInstructions; //key - junctionId, value - navigation direction
	
	public Space(String id, Map<String, String> navigationInstructions) {
		this.id = id;
		this.navigationInstructions = navigationInstructions;
	}

	public String getId() {
		return id;
	}

	public Map<String, String> getNavigationInstructions() {
		return navigationInstructions;
	}

	@Override
	public String toString() {
		return "Space [id=" + id + ", navigationInstructions=" + navigationInstructions.toString() + "]";
	}
	
	
	
}

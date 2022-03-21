package sogo.parking.dto;

public class Event {
	EventType type;
	String carId;
	String junctionId;
	String spaceId;
	
	
	
	public EventType getType() {
		return type;
	}

	public void setType(EventType type) {
		this.type = type;
	}

	public String getCarId() {
		return carId;
	}

	public void setCarId(String carId) {
		this.carId = carId;
	}

	public String getJunctionId() {
		return junctionId;
	}

	public void setJunctionId(String junctionId) {
		this.junctionId = junctionId;
	}

	public String getSpaceId() {
		return spaceId;
	}

	public void setSpaceId(String spaceId) {
		this.spaceId = spaceId;
	}



	public static Event parse(String str) {
		validate(str);
		Event event = new Event();
		fillType(str, event);
		fillCarId(str, event);
		if (event.type == EventType.ON_GATE || event.type == EventType.ON_JUNCTION) {
			fillJunctionId(str, event);
		}
		if (event.type == EventType.ON_SPACE) {			
			fillSpaceId(str, event);
		}
		return event;
	}

	private static void validate(String str) {
		// TODO Auto-generated method stub
		
	}

	private static void fillSpaceId(String str, Event event) {
		event.setSpaceId(str.substring(str.indexOf("space") + "space".length()));
	}

	private static void fillJunctionId(String str, Event event) {
		if (event.type == EventType.ON_GATE) {
			event.setJunctionId("gate");
		} else {			
			event.setJunctionId(str.substring(str.indexOf("junction") + "junction".length()));
		}
	}

	private static void fillCarId(String str, Event event) {
		int startIndex = str.indexOf("car") + "car".length();
		event.setCarId(str.substring(startIndex, str.indexOf(".", startIndex)));
	}

	private static void fillType(String str, Event event) {
		if (str.contains("reserve")) {
			event.type = EventType.RESERVE;
		} else if (str.contains("gate")) {
			event.type = EventType.ON_GATE;
		} else if (str.contains("junction")) {
			event.type = EventType.ON_JUNCTION;
		} else {
			event.type = EventType.ON_SPACE;
		}
	}

	@Override
	public String toString() {
		return "Event [type=" + type + ", carId=" + carId + ", junctionId=" + junctionId + ", spaceId=" + spaceId + "]";
	}
	
	
	
}

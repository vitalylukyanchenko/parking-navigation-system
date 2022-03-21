package sogo.parking.service;

import java.io.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import sogo.parking.ParkingApplication;
import sogo.parking.service.dto.Car;
import sogo.parking.service.dto.Space;

@Service
public class ParkingServiceImpl implements ParkingService {
	static private Logger LOG = LoggerFactory.getLogger(ParkingService.class);
	@Value("${app.parking.parkingConfigurationFileName}")
	private String fileName;
	private Map<String, Car> carsOnTheWay = new HashMap<>();
	private Map<String, Car> parkedCars = new HashMap<>();
	private TreeMap<LocalDateTime, Car> parkingTimeStamps = new TreeMap<>();
	private Map<String, Space> spaces = new HashMap<>();
	private List<Space> freeSpaces = new ArrayList<>(); // list of free spaceIds
	private Map<String, Space> spacesByCar = new HashMap<>(); // spaces reserved or occupied by cars
	
	private void buildParkingR(JsonElement jsonElement, String junctionName,
			Map<String, String> navigationInstructions) throws Exception {
		if (jsonElement.isJsonArray()) {
			JsonArray directions = jsonElement.getAsJsonArray();
			for (int i = 0; i < directions.size(); i++) {
				// add nav instruction to the nav instuctions map
				navigationInstructions.put(junctionName,
						directions.get(i).getAsJsonObject().get("direction").getAsString());
				// add spaces to spaces list
				if (directions.get(i).getAsJsonObject().get("spaces") != null) {
					directions.get(i).getAsJsonObject().get("spaces").getAsJsonArray()
							.forEach(spaceJsonElement -> addSpace(spaceJsonElement.getAsString(), navigationInstructions));
				// drilldown to junction details
				} else {
					JsonElement detailsJsonElement = directions.get(i).getAsJsonObject().get("details");
					if (detailsJsonElement == null) {
						throw new Exception("Incorrect parking configuration structure. Every direction "
								+ "must contain details or spaces");
					}
					buildParkingR(detailsJsonElement, directions.get(i).getAsJsonObject().get("junction").getAsString(),
							navigationInstructions);
				}
			}
		}
	}

	private void addSpace(String spaceId, Map<String, String> navigationInstructions) {
		Space space = new Space(spaceId, new HashMap<>(navigationInstructions));
		freeSpaces.add(space);
		spaces.put(spaceId, space);
	}

	private static String readJSONFromFile(String fileName) throws IOException {
        // Create Buffer reader for the File that is downloaded
        BufferedReader reader = new BufferedReader(new FileReader(fileName));
        // create StringBuilder object
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        // Append items from the file to string builder
        String ls = System.getProperty("line.separator");
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
            stringBuilder.append(ls);
        }
        // delete the last new line separator
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        reader.close();
        return stringBuilder.toString();
    }
	 
	private void parkCar(String carId) {
		Car car = carsOnTheWay.get(carId);
		car.setFinishParking(LocalDateTime.now());
		parkingTimeStamps.put(car.getFinishParking(), car);
		parkedCars.put(carId, carsOnTheWay.remove(carId));
		
	}
	
	private Space getNextFreeSpace(String id) {
		Car car = new Car(id, LocalDateTime.now());
		Space space = freeSpaces.remove(freeSpaces.size() - 1);
		spacesByCar.put(id, space);
		carsOnTheWay.put(id, car);
		return space;
	}
		
	@Override
	@PostConstruct
	public void loadParkingConfiguration() throws Exception {
		String parkingConfigJson = readJSONFromFile(fileName);
		JsonElement gateElement = JsonParser.parseString(parkingConfigJson).getAsJsonObject().get("gate");
		ParkingService parkingService = new ParkingServiceImpl();
		buildParkingR(gateElement, "gate", new HashMap<String, String>());
		LOG.debug(spaces.toString());
	}
	
	@Override
	public String recerveSpace(String id) {
		if (freeSpaces.isEmpty()) {
			return "Parking is full. You for sure find a place on the next parking";
		}
		if (carsOnTheWay.containsKey(id) || parkedCars.containsKey(id)) {
			return "Car is already on the parking. One car can't ocupy more then 1 place";
		}
		Space space = getNextFreeSpace(id);
		return String.format("Welcome. Your space is %s", space.getId());
	}

	@Override
	public String displayInstructions(String carId, String junctionId) {
		if(!carsOnTheWay.containsKey(carId)) {
			return "No space reserved for you. Please, go to parking administration";
		}
		Space reservedSpace = spacesByCar.get(carId);
		String navInstruction = reservedSpace.getNavigationInstructions().get(junctionId);
		if (navInstruction == null) {
			return String.format("screen: %s  - wrong way", junctionId);
		}
		return String.format("screen: %s - go %s", junctionId, navInstruction);
	}

	@Override
	public String park(String carId, String spaceId) {
		if(!carsOnTheWay.containsKey(carId)) {
			return "No space reserved for you. Please, go to parking administration";
		}
		if (spaces.get(spaceId) == null) {
			return String.format("Service message. Space %s is not registered on the parking", spaceId);
		}
		Space reservedSpace = spacesByCar.get(carId);
		if (reservedSpace.getId().equals(spaceId)) {
			parkCar(carId);
			LOG.debug(parkedCars.toString());
			return String.format("screen: %s - welcome to your place %s", spaceId, spaceId);
		} else {
			return String.format("screen: %s - wrong space", spaceId);
		}
	}

	@Override
	public int getAverageParkingTimeSec(int periodSec) {
		return (int) Math.round(
				parkingTimeStamps.subMap(LocalDateTime.now().minusSeconds(periodSec), LocalDateTime.now())
					.values()
					.stream()
					.map(car -> (int) ChronoUnit.SECONDS.between(car.getStartParking(), car.getFinishParking()))
					.collect(Collectors.averagingInt(Integer :: intValue))
			);
	}

}

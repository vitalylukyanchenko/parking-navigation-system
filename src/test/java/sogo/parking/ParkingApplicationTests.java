package sogo.parking;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import sogo.parking.service.ParkingService;
import sogo.parking.service.dto.Space;

@SpringBootTest
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ParkingApplicationTests {
	private static final int N_PARKING_PLACES = 18;
	@Autowired
	ParkingService parkingService;
	
	private String getSpaceId(String carId) throws Exception {
		Field field = parkingService.getClass().getDeclaredField("spacesByCar");
		field.setAccessible(true);
		Map<String, Space> spacesByCar = (Map<String, Space>) field.get(parkingService);
		return spacesByCar.get(carId).getId();
	}

	@Test
	void contextLoads() {
	}
	
	@Test
	@Order(1)
	void carRegistration() {
		// correct registration
		assertTrue(parkingService.recerveSpace("car0").matches("Welcome. Your space is .+"));
		// registration car with the same id
		assertEquals(parkingService.recerveSpace("car0"),
				"Car is already on the parking. One car can't ocupy more then 1 place");
		// register all parking places
		for (int i = 1; i < N_PARKING_PLACES; i++) {
			assertTrue(parkingService.recerveSpace("car" + i).matches("Welcome. Your space is .+"));
		}
		// register 1 more car (parking overloading)
		assertEquals(parkingService.recerveSpace("wrong_car"),
				"Parking is full. You for sure find a place on the next parking");
	}
	
	@Test
	@Order(2)
	void gateNavigation() throws Exception {
		// correct gate navigation
		assertTrue(parkingService.displayInstructions("car0", "gate").matches("screen: .+ - go .+"));
		// wrong carId
		assertEquals(parkingService.displayInstructions("wrong_car", "gate"),
				"No space reserved for you. Please, go to parking administration");
		// already parked car
		parkingService.park("car0", getSpaceId("car0"));
		assertEquals(parkingService.displayInstructions("car0", "gate"),
				"No space reserved for you. Please, go to parking administration");
	}
	
	@Test
	@Order(3)
	void junctionNavigation() throws Exception {
		String junctionId = "J1";
		// correct junction navigation
		assertTrue(parkingService.displayInstructions("car1", junctionId).matches("screen: .+ - go .+"));
		// wrong carId
		assertEquals(parkingService.displayInstructions("wrong_car", junctionId),
				"No space reserved for you. Please, go to parking administration");
		// wrong junctionId
		assertEquals(parkingService.displayInstructions("car1", "wrong_junction"),
				"screen: wrong_junction - wrong way");
	}
	
	@Test
	@Order(4)
	void park() throws Exception {
		// correct parking place
		assertTrue(parkingService.park("car1", getSpaceId("car1")).matches("screen: .+ - welcome to your place .+"));
		// incorrect parking place
		assertTrue(parkingService.park("car2", getSpaceId("car3")).matches("screen: .+ - wrong space"));
		// place does not exist on the parking
		assertTrue(parkingService.park("car2", "wrong_space").matches("Service message. Space .+ is not registered on the parking"));
		// car has already been parked
		assertEquals(parkingService.park("car1", getSpaceId("car1")), "No space reserved for you. Please, go to parking administration");
	}

}

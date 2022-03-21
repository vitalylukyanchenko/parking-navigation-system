package sogo.parking.service;

public interface ParkingService {
	/**
	 * Reserves next free parking space for a new car and return welcome message with reserved space.
	 * If parking is full returns message "Parking is full. You for sure find a place on the next parking" 
	 * @return message with reserved space
	 */
	String recerveSpace(String id);
	String displayInstructions(String carId, String junctionId);
	String park(String carId, String spaceId);
	int getAverageParkingTimeSec(int periodSec);
}

package sogo.parking.service.dto;

import java.time.LocalDateTime;

public class Car {
	private String id;
	private LocalDateTime startParking;
	private LocalDateTime finishParking;
	
	public Car(String id, LocalDateTime startParking) {
		this.id = id;
		this.startParking = startParking;
	}

	public LocalDateTime getFinishParking() {
		return finishParking;
	}

	public void setFinishParking(LocalDateTime finishParking) {
		this.finishParking = finishParking;
	}

	public String getId() {
		return id;
	}

	public LocalDateTime getStartParking() {
		return startParking;
	}

	@Override
	public String toString() {
		return "Car [id=" + id + ", startParking=" + startParking + ", finishParking=" + finishParking + "]";
	}
	
	

}

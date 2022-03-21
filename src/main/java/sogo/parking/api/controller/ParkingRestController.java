package sogo.parking.api.controller;

import static sogo.parking.api.ApiConstants.*;

import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import sogo.parking.service.ParkingService;

@RestController
@RequestMapping(PARKING_MAPPING)
public class ParkingRestController {
	@Autowired
	ParkingService parkingService;
	
	@GetMapping("/statistics/avg_parking_time")
	ResponseEntity<?> getAverageparkingTime(
			@RequestParam(name = "period", defaultValue = DEFAULT_PERIOD_SEC, required = false) @Min(1) int period) {
		return ResponseEntity.ok(parkingService.getAverageParkingTimeSec(period));
	}
}

package sogo.parking;

import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;

import sogo.parking.dto.Event;
import sogo.parking.service.ParkingService;
import sogo.parking.service.ParkingServiceImpl;

@SpringBootApplication
public class ParkingApplication implements CommandLineRunner {
	static ConfigurableApplicationContext ctx;
	static private Logger LOG = LoggerFactory.getLogger(ParkingApplication.class);
	@Autowired
	private ParkingService parkingService;
	

	public static void main(String[] args) {
		ctx = SpringApplication.run(ParkingApplication.class, args);
		ctx.close();
	}

	@Override
	public void run(String... args) throws Exception {
		while(true) {
			Scanner scanner = new Scanner(System.in);
			String str = scanner.nextLine();
			if ("exit".equals(str)) {
				LOG.debug("Application is closing");
				break;
			}
			Event event = Event.parse(str);
			switch (event.getType()) {
				case RESERVE : { LOG.debug(event.toString()); System.out.println(parkingService.recerveSpace(event.getCarId())); break; }
				case ON_GATE : { LOG.debug(event.toString()); System.out.println(parkingService.displayInstructions(event.getCarId(), event.getJunctionId())); break; }
				case ON_JUNCTION : { LOG.debug(event.toString()); System.out.println(parkingService.displayInstructions(event.getCarId(), event.getJunctionId())); break; }
				case ON_SPACE : { LOG.debug(event.toString()); System.out.println(parkingService.park(event.getCarId(), event.getSpaceId())); break; }
				case NOT_EVENT : { LOG.debug(event.toString()); System.out.println("Incorrect event. Please try again"); break; }
				default: break;
			}
		}
			
	}

}

package frequentflyer.com;

import frequentflyer.com.domain.DomainMapper;
import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import frequentflyer.com.repositories.AirportRepository;
import frequentflyer.com.repositories.CombinationRepository;
import frequentflyer.com.repositories.RotationRepository;
import frequentflyer.com.services.AirportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;

import java.text.DateFormat;
import java.time.Instant;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Locale;


@SpringBootApplication
@Slf4j
public class NvtApplication implements CommandLineRunner {


	@Autowired
	private AirportService airportService;

	@Autowired
	private CombinationRepository combinationRepository;

	@Autowired
	private RotationRepository rotationRepository;

	@Autowired
	private AirportRepository airportRepository;


	public static void main(String[] args) {
		SpringApplication.run(NvtApplication.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		System.out.println("NvtApplication.run RUNNING ");


		InputStream inputStream = new ClassPathResource("/data/airports.csv").getInputStream();


		long airportsInDb = airportRepository.count();

		log.info("AIRPORTS IN DB " + airportsInDb);

		if (airportsInDb == 0) {
			log.info("--------------------------------");
			log.info("Starting airport processing");
			log.info("--------------------------------");

			airportService.loadAirports(inputStream);
		}

		Combination combination = combinationRepository.findByCombinationName("MyAirline");
		if (combination == null) {

			combination = new Combination();
			combination.setCombinationName("MyAirline");
			combination.setCombinationColor("#6699ff");
			combination = combinationRepository.save(combination);

			Airport AMS = airportRepository.findByIataCode("AMS");
			Airport CDG = airportRepository.findByIataCode("CDG");
			Airport NRT = airportRepository.findByIataCode("NRT");
			Airport JFK = airportRepository.findByIataCode("JFK");
			Airport GIG = airportRepository.findByIataCode("GIG");
			Airport BEG = airportRepository.findByIataCode("BEG");

			Rotation rotation1 = new Rotation();
			rotation1.setCombination(combination);
			rotation1.setOrigin(AMS);
			rotation1.setDestination(CDG);
			rotation1.setFlightLength(120);
			rotation1.setFrequency("1/2/3/4/5/6/7");
			rotation1.setLocalDepartureTime("10:00");
			rotation1 = rotationRepository.save(rotation1);

			Rotation rotation2 = new Rotation();
			rotation2.setCombination(combination);
			rotation2.setOrigin(AMS);
			rotation2.setDestination(NRT);
			rotation2.setFlightLength(600);
			rotation2.setFrequency("1/4/5/6");
			rotation2.setLocalDepartureTime("22:30");
			rotation2 = rotationRepository.save(rotation2);


			Rotation rotation3 = new Rotation();
			rotation3.setCombination(combination);
			rotation3.setOrigin(AMS);
			rotation3.setDestination(JFK);
			rotation3.setFlightLength(420);
			rotation3.setFrequency("1/2/3/4/5/6/7");
			rotation3.setLocalDepartureTime("16:15");
			rotation3 = rotationRepository.save(rotation3);
		}


		Airport schiphol = airportRepository.findByIataCode("AMS");
		Airport jfk = airportRepository.findByIataCode("JFK");
		Airport narita = airportRepository.findByIataCode("NRT");

		Rotation example = rotationRepository.findByOriginAndDestinationAndCombination(schiphol, jfk, combination);

		Instant instant = Instant.now(); //can be LocalDateTime
		ZoneId schipholZone = schiphol.getTimezone().toZoneId(); // my timezone
		ZoneOffset depZoneOffset = schipholZone.getRules().getOffset(instant);

		ZoneId jfkZone = jfk.getTimezone().toZoneId(); // my timezone
		ZoneOffset arrZoneOffset = jfkZone.getRules().getOffset(instant);

		log.info(" AMS - JFK rotation ");
		LocalTime departureTime = LocalTime.parse(example.getLocalDepartureTime());
		departureTime.atOffset(depZoneOffset);

		Locale locale = Locale.getDefault();


		DateFormat formatter = DateFormat.getDateTimeInstance(
				DateFormat.DEFAULT,
				DateFormat.DEFAULT,
				locale);

		log.info(" AMS " + departureTime + " TIMEZONE OFFSET FROM UTC: " + schiphol.getTimezone().getOffset(new Date().getTime()));




		LocalTime arrivalTime = departureTime.plusMinutes(example.getFlightLength());
		arrivalTime.atOffset(arrZoneOffset);
		log.info(" JFK " + arrivalTime + " TIMEZONE OFFSET FROM UTC: " + jfk.getTimezone().getOffset(new Date().getTime()));

		Rotation rot = rotationRepository.findByOriginAndDestinationAndCombination(schiphol, narita, combination);

		RotationDto rto1 = DomainMapper.rotationToRotationDto(example);
		RotationDto rto2 = DomainMapper.rotationToRotationDto(rot);

		log.info("AMS - NRT route {}", rto2.getDayMap());
		log.info("AMS - NRT time {}", rto2.getUtcDepartureTime());
		log.info("Flight time {}", rto2.getFlightTime());

	}
}

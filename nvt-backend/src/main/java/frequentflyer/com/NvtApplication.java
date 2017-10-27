package frequentflyer.com;

import frequentflyer.com.domain.DomainMapper;
import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.entities.AirlineRoute;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import frequentflyer.com.repositories.*;
import frequentflyer.com.services.AirlineRouteService;
import frequentflyer.com.services.AirlineService;
import frequentflyer.com.services.AirportService;
import frequentflyer.com.services.impl.AirlineServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

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

	@Autowired
	private AirlineRepository airlineRepository;

	@Autowired
	private AirlineService airlineService;

	@Autowired
	private AirlineRouteRepository airlineRouteRepository;

	@Autowired
	private AirlineRouteService airlineRouteService;


	public static void main(String[] args) {
		SpringApplication.run(NvtApplication.class, args);
	}

	/**
	 *
	 * Async Task Executor for Async methods
	 *
	 * @return {@link TaskExecutor}
	 */
	@Bean
	public TaskExecutor taskExecutor() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(10);
		executor.setQueueCapacity(25);
		return executor;
	}


	@Override
	public void run(String... strings) throws Exception {
		System.out.println("NvtApplication.run RUNNING ");


		InputStream inputStream = new ClassPathResource("/data/airports.csv").getInputStream();

		InputStream airlineInputStream = new ClassPathResource("/data/airlines.csv").getInputStream();

		InputStream airlineRoutesInputStream = new ClassPathResource("/data/airlineRoutes.csv").getInputStream();

		long airportsInDb = airportRepository.count();

		long airlinesInDb = airlineRepository.count();

		long airlineRoutesInDb = airlineRouteRepository.count();


		if (airportsInDb == 0) {
			log.info("--------------------------------");
			log.info("Starting airport processing");
			log.info("--------------------------------");

			airportService.loadAirports(inputStream);
		}

		if (airlinesInDb == 0) {
			log.info("--------------------------------");
			log.info("Starting airline processing");
			log.info("--------------------------------");

			airlineService.loadAirlines(airlineInputStream);
		}


		if (airlineRoutesInDb == 0) {
			log.info("--------------------------------");
			log.info("Starting airline route processing");
			log.info("--------------------------------");

			airlineRouteService.loadAirlineRoutes(airlineRoutesInputStream);
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
			Airport LUX = airportRepository.findByIataCode("LUX");

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
			rotation2.setLocalDepartureTime("01:30");
			rotation2 = rotationRepository.save(rotation2);


			Rotation rotation3 = new Rotation();
			rotation3.setCombination(combination);
			rotation3.setOrigin(AMS);
			rotation3.setDestination(JFK);
			rotation3.setFlightLength(420);
			rotation3.setFrequency("1/2/3/4/5/6/7");
			rotation3.setLocalDepartureTime("16:15");
			rotation3 = rotationRepository.save(rotation3);


			Rotation rotation4 = new Rotation();
			rotation4.setCombination(combination);
			rotation4.setOrigin(AMS);
			rotation4.setDestination(BEG);
			rotation4.setFlightLength(120);
			rotation4.setFrequency("1/2/3/4/5/6/7");
			rotation4.setLocalDepartureTime("02:15");
			rotation4 = rotationRepository.save(rotation4);


			Rotation rotation5 = new Rotation();
			rotation5.setCombination(combination);
			rotation5.setOrigin(AMS);
			rotation5.setDestination(LUX);
			rotation5.setFlightLength(55);
			rotation5.setFrequency("1/2/3/4/5/6/7");
			rotation5.setLocalDepartureTime("02:25");
			rotation5 = rotationRepository.save(rotation5);
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

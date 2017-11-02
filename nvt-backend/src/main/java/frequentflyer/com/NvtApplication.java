package frequentflyer.com;

import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import frequentflyer.com.repositories.*;
import frequentflyer.com.services.AirlineRouteService;
import frequentflyer.com.services.AirlineService;
import frequentflyer.com.services.AirportService;

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

		Combination combination = combinationRepository.findByCombinationName("SuperAirline S2018");
		if (combination == null) {

			combination = new Combination();
			combination.setCombinationName("SuperAirline S2018");
			combination.setCombinationColor("#6699ff");
			combination = combinationRepository.save(combination);

			Airport AMS = airportRepository.findByIataCode("AMS");
			Airport CDG = airportRepository.findByIataCode("CDG");
			Airport NRT = airportRepository.findByIataCode("NRT");
			Airport JFK = airportRepository.findByIataCode("JFK");
			Airport GIG = airportRepository.findByIataCode("GIG");
			Airport BEG = airportRepository.findByIataCode("BEG");
			Airport KVO = airportRepository.findByIataCode("KVO");
			Airport ALA = airportRepository.findByIataCode("ALA");
			Airport ZNZ = airportRepository.findByIataCode("ZNZ");
			Airport ZAG = airportRepository.findByIataCode("ZAG");
			Airport PUY = airportRepository.findByIataCode("PUY");

			saveRotation(combination, AMS, CDG, 90, "1/2/3/4/5/6/7", "07:00");
			saveRotation(combination, CDG, AMS, 90, "1/2/3/4/5/6/7", "09:30");

			saveRotation(combination, AMS, NRT, 600, "1/2/3/4/5/6/7", "11:15");
			saveRotation(combination, NRT, AMS, 620, "1/2/3/4/5/6/7", "18:45");

			saveRotation(combination, AMS, BEG, 120, "1/2/3/4/5/6/7", "02:15");
			saveRotation(combination, BEG, ZAG, 45, "1/2/3/4/5/6/7", "05:15");
			saveRotation(combination, ZAG, ZAG, 45, "1/2/3/4/5/6/7", "07:15");
			saveRotation(combination, BEG, AMS, 120, "1/2/3/4/5/6/7", "09:20");

			saveRotation(combination, AMS, BEG, 120, "1/2/3/4/5/6/7", "02:35");
			saveRotation(combination, BEG, KVO, 30, "1/2/3/4/5/6/7", "05:15");
			saveRotation(combination, KVO, BEG, 30, "1/2/3/4/5/6/7", "06:15");
			saveRotation(combination, BEG, AMS, 120, "1/2/3/4/5/6/7", "08:45");

			saveRotation(combination, AMS, PUY, 110, "1/3/5/7", "20:15");
			saveRotation(combination, PUY, AMS, 110, "1/3/5/7", "23:35");

			saveRotation(combination, AMS, ALA, 365, "1/2/4/6", "15:00");
			saveRotation(combination, ALA, AMS, 375, "1/2/4/6", "20:00");

			saveRotation(combination, AMS, JFK, 480, "1/2/3/4/5/6/7", "16:15");
			saveRotation(combination, JFK, AMS, 460, "1/2/3/4/5/6/7", "22:00");

			saveRotation(combination, AMS, GIG, 660, "1/2/3/4/5/6/7", "11:25");
			saveRotation(combination, GIG, AMS, 680, "1/2/3/4/5/6/7", "16:00");

			saveRotation(combination, AMS, ZNZ, 415, "1/2/3/4/5/6/7", "11:05");
			saveRotation(combination, ZNZ, AMS, 425, "1/2/3/4/5/6/7", "19:25");

		}

	}


	private void saveRotation (Combination combination, Airport origin, Airport destination, int length, String frequency, String localDepartureTime) {
		Rotation rotation = new Rotation();
		rotation.setCombination(combination);
		rotation.setOrigin(origin);
		rotation.setDestination(destination);
		rotation.setFlightLength(length);
		rotation.setFrequency(frequency);
		rotation.setLocalDepartureTime(localDepartureTime);
		rotationRepository.save(rotation);
	}
}

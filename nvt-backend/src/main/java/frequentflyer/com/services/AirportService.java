package frequentflyer.com.services;

import frequentflyer.com.domain.AirportDetailed;
import frequentflyer.com.domain.AirportSearchDto;
import frequentflyer.com.domain.AirportVicinityStats;
import frequentflyer.com.entities.Airport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface AirportService {

    /**
     *
     * Load airports from a file
     *
     * @param inputStream
     */
    void loadAirports(InputStream inputStream);

    /**
     *
     * Retrieve airport by iata airport code
     *
     * @param code
     * @return {@link Airport} object
     */
    Airport findByAirportCode(String code);

    /**
     *
     * Perform a partial search of airport data
     *
     * @param searchCriteria
     * @return {@link AirportSearchDto} object
     */
    AirportSearchDto partialSearch(String searchCriteria);


    /**
     *
     * Retrieve detailed stats of an airport
     *
     * @param iataCode - IATA code of an airport
     * @return {@link AirportDetailed} object
     */
    AirportDetailed airportData(String iataCode);

    /**
     *
     * Retrieve all statistics data for airports in vicinity
     *
     * @param iataCode - IATA code of target airport
     * @param radius - radius in km around the airport
     * @return - {@link AirportVicinityStats} object
     */
    AirportVicinityStats airportsInVicinity(String iataCode, int radius);

}

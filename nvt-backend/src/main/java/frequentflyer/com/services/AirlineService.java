package frequentflyer.com.services;

import frequentflyer.com.domain.AirlineDto;
import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.entities.Airline;

import java.io.InputStream;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
public interface AirlineService {

    /**
     *
     * Load airlines from a file
     *
     * @param inputStream
     */
    void loadAirlines(InputStream inputStream);

    /**
     *
     * Find airline bu IATA code of an airline
     *
     * @param iataCode
     * @return {@link Airline} object
     */
    Airline findAirlineByIataCode(String iataCode);

    /**
     *
     * Find airline by Unique airline code (bare in mind that certain airlines have the same IATA code)
     * i.e. Turkish and Turkish Cargo therefore we cannot rely on Iata code here
     *
     * @param uniqueCode
     * @return {@link Airline} object
     */
    Airline findAirlineByUniqueCode(String uniqueCode);

    /**
     *
     * Perform partial search of airline data
     *
     * @param searchCriteria
     * @return {@link AirlineSearchDto} object
     */
    AirlineSearchDto partialSearch(String searchCriteria);

    /**
     *
     * Retrieve airline data
     *
     * @param uniqueId
     * @return {@link AirlineDto} objects
     */
    AirlineDto getAirlineData(String uniqueId);
}

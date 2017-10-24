package frequentflyer.com.services;

import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.entities.Airline;

import java.io.InputStream;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
public interface AirlineService {

    void loadAirlines(InputStream inputStream);

    Airline findAirlineByIataCode(String iataCode);

    Airline findAirlineByUniqueCode(String uniqueCode);

    AirlineSearchDto partialSearch(String searchCriteria);
}

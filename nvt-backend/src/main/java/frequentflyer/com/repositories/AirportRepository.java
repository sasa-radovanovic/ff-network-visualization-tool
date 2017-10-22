package frequentflyer.com.repositories;

import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface AirportRepository extends CrudRepository<Airport, Long> {

    Airport findByIataCode(String iataCode);

    Page<Airport> findByAirportNameContainsOrCityContainsOrCountryContainsOrIataCodeContainsOrIcaoCodeContains(String airportName,
                                                                                                               String city,
                                                                                                               String country,
                                                                                                               String iataCode,
                                                                                                               String icaoCode,
                                                                                                               Pageable pageable);

}

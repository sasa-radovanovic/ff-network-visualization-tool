package frequentflyer.com.repositories;

import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.Airport;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Repository
public interface AirlineRepository extends CrudRepository<Airline, Long> {

    Airline findByIataCode(String iataCode);

    Airline findByUniqueId(String uniqueId);

    Page<Airline> findByAirlineNameContainsAndIataCodeContainsAndIcaoCodeContainsAndCountryContains(String airlineName,
                                                                                                               String iata,
                                                                                                               String icao,
                                                                                                               String country,
                                                                                                               Pageable pageable);
}

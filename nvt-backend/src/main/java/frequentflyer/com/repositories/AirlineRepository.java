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

    /**
     *
     * Retrieve Airline by IATA code
     *
     * @param iataCode
     * @return
     */
    Airline findByIataCode(String iataCode);

    /**
     *
     * Retrieve Airline by Unique ID
     *
     * @param uniqueId
     * @return
     */
    Airline findByUniqueId(String uniqueId);

    /**
     *
     * Retrieve a page for searching airlines
     *
     * @param airlineName
     * @param iata
     * @param icao
     * @param country
     * @param pageable
     * @return
     */
    Page<Airline> findByAirlineNameContainsOrIataCodeContainsOrIcaoCodeContainsOrCountryContains(String airlineName,
                                                                                                               String iata,
                                                                                                               String icao,
                                                                                                               String country,
                                                                                                 Pageable pageable);
}

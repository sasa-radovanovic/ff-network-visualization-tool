package frequentflyer.com.repositories;

import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Repository
public interface AirportRepository extends CrudRepository<Airport, Long> {

    /**
     *
     * Retrieve airport by Iata code
     *
     * @param iataCode
     * @return {@link Airport} object
     */
    Airport findByIataCode(String iataCode);

    /**
     *
     *
     * Retrieve a single page for airport search
     *
     * @param airportName
     * @param city
     * @param country
     * @param iataCode
     * @param icaoCode
     * @param pageable
     * @return Page of {@link Airport} objects
     */
    Page<Airport> findByAirportNameContainsOrCityContainsOrCountryContainsOrIataCodeContainsOrIcaoCodeContains(String airportName,
                                                                                                               String city,
                                                                                                               String country,
                                                                                                               String iataCode,
                                                                                                               String icaoCode,
                                                                                                               Pageable pageable);

    /**
     *
     * Retrieve all airports in a square where (lat1 < X < lat2) and (lon1 < y < lon2)
     *
     * @param latitude1
     * @param latitude2
     * @param longitude1
     * @param longitude2
     * @return List of {@link Airport} objects
     */
    List<Airport> findAllByLatitudeBetweenAndLongitudeBetween(double latitude1, double latitude2, double longitude1, double longitude2);
}

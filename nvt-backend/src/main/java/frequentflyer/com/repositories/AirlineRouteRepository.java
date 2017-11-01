package frequentflyer.com.repositories;

import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.AirlineRoute;
import frequentflyer.com.entities.Airport;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Repository
public interface AirlineRouteRepository extends CrudRepository<AirlineRoute, Long> {

    /**
     *
     * Retrieve all airline routes by Airline (taking into account codeshare flag)
     *
     * @param airline - {@link Airline} object
     * @param codeshare - include codeshared flights or not
     * @return List of {@link AirlineRoute} objects
     */
    List<AirlineRoute> findAllByAirlineAndCodeshare(Airline airline, boolean codeshare);

    /**
     *
     * Retrieve all airline routes from specific airport
     *
     * @param origin - {@link Airport} object
     * @param destination - {@link Airport} object
     * @param codeshare - include codeshared flights
     * @return - List of {@link AirlineRoute} objects
     */
    List<AirlineRoute> findAllByOriginOrDestinationAndCodeshare(Airport origin, Airport destination, boolean codeshare);
}

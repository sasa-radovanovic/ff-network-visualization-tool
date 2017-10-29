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

    List<AirlineRoute> findAllByAirlineAndCodeshare(Airline airline, boolean codeshare);

    List<AirlineRoute> findAllByOriginOrDestinationAndCodeshare(Airport origin, Airport destination, boolean codeshare);
}

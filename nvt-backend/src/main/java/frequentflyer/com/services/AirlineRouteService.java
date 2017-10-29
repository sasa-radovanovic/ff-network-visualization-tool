package frequentflyer.com.services;

import frequentflyer.com.domain.AirlineRouteDto;
import frequentflyer.com.entities.AirlineRoute;
import frequentflyer.com.entities.Airport;

import java.io.InputStream;
import java.util.List;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
public interface AirlineRouteService {

    void loadAirlineRoutes(InputStream inputStream);

    List<AirlineRouteDto> getAirlineRoutes (String uniqueId, boolean codeshares);

    List<AirlineRoute> getRoutesFromAirport (Airport airport);
}

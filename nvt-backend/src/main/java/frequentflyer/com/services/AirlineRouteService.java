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

    /**
     *
     * Load airline routes from file
     *
     * @param inputStream
     */
    void loadAirlineRoutes(InputStream inputStream);

    /**
     *
     * Retrieve routes of a certain airline
     *
     * @param uniqueId - Unique ID of an airline
     * @param codeshares -
     * @return List of {@link AirlineRouteDto} objects
     */
    List<AirlineRouteDto> getAirlineRoutes (String uniqueId, boolean codeshares);

    /**
     *
     * Retrieve routes from a certain airport
     *
     * @param airport - {@link Airport} object
     * @return List of {@link AirlineRoute} objects
     */
    List<AirlineRoute> getRoutesFromAirport (Airport airport);
}

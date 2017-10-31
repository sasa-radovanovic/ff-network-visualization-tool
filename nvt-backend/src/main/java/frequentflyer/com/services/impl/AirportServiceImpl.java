package frequentflyer.com.services.impl;

import frequentflyer.com.domain.*;
import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.AirlineRoute;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.repositories.AirportRepository;
import frequentflyer.com.services.AirlineRouteService;
import frequentflyer.com.services.AirportService;
import frequentflyer.com.services.exceptions.NvtServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.*;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;


/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Service
@Slf4j
public class AirportServiceImpl implements AirportService {

    private final String COMMA = ",";

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private AirlineRouteService airlineRouteService;

    @Override
    public void loadAirports(InputStream inputStream) {
        log.info("AirportServiceImpl.loadAirports | Loading airports to DB");


        try {
            log.info("AirportServiceImpl.loadAirports | Reading file...");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // skip the header of the csv
            br.lines().map(mapToItem).forEach(a -> {
                if (a != null) {
                    airportRepository.save(a);
                }
            });
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    /**
     * Map line from input file to database entity
     */
    private Function<String, Airport> mapToItem = (line) -> {
        String[] p = line.split(COMMA);
        Airport airport = null;
        if (!trimQuotes(p[4]).equalsIgnoreCase("") && trimQuotes(p[4]).length() == 3 &&
                !trimQuotes(p[11]).equalsIgnoreCase("\\N")) {
            airport = new Airport();
            airport.setAirportName(trimQuotes(p[1]));
            airport.setCity(trimQuotes(p[2]));
            airport.setCountry(trimQuotes(p[3]));
            airport.setIataCode(trimQuotes(p[4]));
            airport.setIcaoCode(trimQuotes(p[5]));
            airport.setLatitude(Double.parseDouble(p[6]));
            airport.setLongitude(Double.parseDouble(p[7]));
            ZoneId zoneId = ZoneId.of(trimQuotes(p[11]));
            airport.setTimezone(TimeZone.getTimeZone(zoneId));
        }
        return airport;
    };

    private String trimQuotes(String s) {
        return s.replaceAll("^\"|\"$", "");
    }


    @Override
    public Airport findByAirportCode(String code) {
        return airportRepository.findByIataCode(code);
    }

    @Override
    public AirportSearchDto partialSearch(String searchCriteria) {
        AirportSearchDto airportSearchDto = new AirportSearchDto();

        Page<Airport> airports = airportRepository.findByAirportNameContainsOrCityContainsOrCountryContainsOrIataCodeContainsOrIcaoCodeContains(searchCriteria,
                searchCriteria, searchCriteria, searchCriteria, searchCriteria, new PageRequest(0, 50));

        airportSearchDto.setTotalNum(airports.getTotalElements());

        List<AirportDto> airportDtoList = new ArrayList<>();


        airports.forEach(a -> {
            AirportDto airportDto = new AirportDto();
            airportDto.setName(a.getAirportName());
            airportDto.setCity(a.getCity());
            airportDto.setCountry(a.getCountry());
            airportDto.setIataCode(a.getIataCode());
            airportDto.setIcaoCode(a.getIcaoCode());
            airportDto.setTimezone(a.getTimezone().getDisplayName());
            airportDtoList.add(airportDto);
        });

        airportSearchDto.setAirportDtoList(airportDtoList);

        return airportSearchDto;
    }

    @Override
    public AirportDetailed airportData(String iataCode) {

        AirportDetailed airportDetailed = new AirportDetailed();

        Airport airport = airportRepository.findByIataCode(iataCode.toUpperCase());

        if (airport == null) {
            throw new NvtServiceException("Airport not found");
        }

        airportDetailed.setLongitude(airport.getLongitude());
        airportDetailed.setLatitude(airport.getLatitude());

        List<AirlineRoute> routesFromAirport = airlineRouteService.getRoutesFromAirport(airport);
        ArrayList<AirportConnection> connections = new ArrayList<>();

        HashMap<String, AirlineDto> airlinesOperating = new HashMap<>();

        routesFromAirport.forEach(route -> {

            if (!airlinesOperating.containsKey(route.getAirline().getUniqueId())) {
                airlinesOperating.put(route.getAirline().getUniqueId(), DomainMapper.airlineToAirlineDto(route.getAirline()));
            }

            AirportConnection airportConnection = new AirportConnection();
            if (route.getOrigin().getIataCode().equalsIgnoreCase(airport.getIataCode())) {
                airportConnection.setCity(route.getDestination().getCity());
                airportConnection.setCountry(route.getDestination().getCountry());
                airportConnection.setIataCode(route.getDestination().getIataCode());
                airportConnection.setIcaoCode(route.getDestination().getIcaoCode());
                airportConnection.setName(route.getDestination().getAirportName());
                airportConnection.setLatitude(route.getDestination().getLatitude());
                airportConnection.setLongitude(route.getDestination().getLongitude());
            } else {
                airportConnection.setCity(route.getOrigin().getCity());
                airportConnection.setCountry(route.getOrigin().getCountry());
                airportConnection.setIataCode(route.getOrigin().getIataCode());
                airportConnection.setIcaoCode(route.getOrigin().getIcaoCode());
                airportConnection.setName(route.getOrigin().getAirportName());
                airportConnection.setLatitude(route.getOrigin().getLatitude());
                airportConnection.setLongitude(route.getOrigin().getLongitude());
            }
            connections.add(airportConnection);
        });

        airportDetailed.setConnections(connections);
        airportDetailed.setOperatingCarriers(airlinesOperating);

        return airportDetailed;
    }

    @Override
    public AirportVicinityStats airportsInVicinity(String iataCode, int radius) {

        AirportVicinityStats airportVicinityStats = new AirportVicinityStats();

        Airport airport = airportRepository.findByIataCode(iataCode.toUpperCase());


        List<Airport> airportsApprox = airportRepository.findAllByLatitudeBetweenAndLongitudeBetween(airport.getLatitude() - (radius / 100 + 1),
                airport.getLatitude() + (radius / 100 + 1),
                airport.getLongitude() - (radius / 100 + 1),
                airport.getLongitude() + (radius / 100 + 1));

        List<AirportVicinitySingleAirport> airportVicinitySingleAirports = new ArrayList<>();

        airportsApprox.forEach(a -> {
            if (distance(a.getLatitude(), airport.getLatitude(), a.getLongitude(), airport.getLongitude()) <= radius * 1000) {
                AirportVicinitySingleAirport airportVicinitySingleAirport = new AirportVicinitySingleAirport();
                airportVicinitySingleAirport.setLongitude(a.getLongitude());
                airportVicinitySingleAirport.setLatitude(a.getLatitude());
                airportVicinitySingleAirport.setName(a.getAirportName());
                airportVicinitySingleAirport.setCountry(a.getCountry());
                airportVicinitySingleAirport.setCity(a.getCity());
                airportVicinitySingleAirport.setIataCode(a.getIataCode());
                airportVicinitySingleAirport.setIcaoCode(a.getIcaoCode());

                List<AirlineRoute> routesFromAirport = airlineRouteService.getRoutesFromAirport(a);

                HashMap<String, Airline> airlines = new HashMap<>();

                routesFromAirport.forEach(ar -> {
                    if (!airlines.containsKey(ar.getAirline().getIataCode())) {
                        airlines.put(ar.getAirline().getIataCode(), ar.getAirline());
                    }
                });

                airportVicinitySingleAirport.setCarriers(airlines.keySet().size());

                airportVicinitySingleAirport.setRoutes(routesFromAirport.size());

                airportVicinitySingleAirports.add(airportVicinitySingleAirport);
            }

        });

        airportVicinityStats.setAirports(airportVicinitySingleAirports);

        return airportVicinityStats;

    }


    /**
     * Calculate distance between two points in latitude and longitude taking
     * into account height difference. If you are not interested in height
     * difference pass 0.0. Uses Haversine method as its base.
     * <p>
     * lat1, lon1 Start point lat2, lon2 End point el1 Start altitude in meters
     * el2 End altitude in meters
     *
     * @returns Distance in Meters
     */
    private static double distance(double lat1, double lat2, double lon1,
                                   double lon2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        distance = Math.pow(distance, 2);

        return Math.sqrt(distance);
    }

}

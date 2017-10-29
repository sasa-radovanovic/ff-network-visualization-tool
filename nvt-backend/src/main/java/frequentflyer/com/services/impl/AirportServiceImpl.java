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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;
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


        try{
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

    private String trimQuotes (String s) {
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

        List<AirlineDto> airlinesOperating = new ArrayList<>();

        routesFromAirport.forEach(route -> {

            Optional<AirlineDto> existing = airlinesOperating.stream().filter(airlineDto -> {
                if (airlineDto != null && route.getAirline() != null) {
                    return airlineDto.getUniqueId().equalsIgnoreCase(route.getAirline().getUniqueId());
                } else {
                    return false;
                }
            }).findAny();

            if (!existing.isPresent()) {
                airlinesOperating.add(DomainMapper.airlineToAirlineDto(route.getAirline()));
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

}

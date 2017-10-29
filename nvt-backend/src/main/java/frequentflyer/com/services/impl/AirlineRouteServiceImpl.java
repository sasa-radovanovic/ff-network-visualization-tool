package frequentflyer.com.services.impl;

import frequentflyer.com.domain.AirlineRouteDto;
import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.AirlineRoute;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.repositories.AirlineRouteRepository;
import frequentflyer.com.services.AirlineRouteService;
import frequentflyer.com.services.AirlineService;
import frequentflyer.com.services.AirportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Service
@Slf4j
@EnableAsync
public class AirlineRouteServiceImpl implements AirlineRouteService {

    private final String COMMA = ",";

    @Autowired
    private AirlineRouteRepository airlineRouteRepository;

    @Autowired
    private AirlineService airlineService;

    @Autowired
    private AirportService airportService;

    int loaded = 0;

    @Override
    @Async
    public void loadAirlineRoutes(InputStream inputStream) {
        log.info("AirlineRouteServiceImpl.loadAirlineRoutes | Loading airlineRoutes to DB");

        try{
            log.info("AirlineRouteServiceImpl.loadAirlineRoutes | Reading file...");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // skip the header of the csv
            br.lines().map(mapToItem).forEach(a -> {
                if (a != null) {
                    airlineRouteRepository.save(a);
                    loaded ++;
                    if (loaded % 1000 == 0) {
                        log.info("AirlineRouteServiceImpl.loadAirlineRoutes | Processed " + loaded);
                    }
                }
            });

            log.info("AirlineRouteServiceImpl.loadAirlineRoutes | Loaded airline routes " + loaded);
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Function<String, AirlineRoute> mapToItem = (line) -> {
        String[] p = line.split(COMMA);
        AirlineRoute airlineRoute = null;
        if (!trimQuotes(p[0]).equalsIgnoreCase("") && trimQuotes(p[0]).length() == 2) {
            Airline airline;
            Airport originAirport;
            Airport destinationAirport;
            try {
                airline = airlineService.findAirlineByUniqueCode(trimQuotes(p[1]));
                originAirport = airportService.findByAirportCode(trimQuotes(p[2]));
                destinationAirport = airportService.findByAirportCode(trimQuotes(p[4]));
            } catch (NonUniqueResultException e) {
                return airlineRoute;
            }
            if (airline == null || originAirport == null || destinationAirport == null) {
                log.warn("Skipping airline " + p[0] + "/" + p[1]);
                return airlineRoute;
            }
            airlineRoute = new AirlineRoute();
            airlineRoute.setAirline(airline);
            airlineRoute.setOrigin(originAirport);
            airlineRoute.setDestination(destinationAirport);
            if (p.length > 8) {
                airlineRoute.setAirplaneTypes(trimQuotes(p[8]));
            } else {
                airlineRoute.setAirplaneTypes("");
            }
            airlineRoute.setCodeshare(trimQuotes(p[6]).equalsIgnoreCase("Y") ? true : false);
        }
        return airlineRoute;
    };

    private String trimQuotes (String s) {
        return s.replaceAll("^\"|\"$", "");
    }



    @Override
    public List<AirlineRouteDto> getAirlineRoutes(String uniqueId, boolean codeshares) {

        List<AirlineRouteDto> airlineRouteDtoList = new ArrayList<>();

        Airline airline = airlineService.findAirlineByUniqueCode(uniqueId);

        List<AirlineRoute> airlineRoutes = airlineRouteRepository.findAllByAirlineAndCodeshare(airline, false);

        if (codeshares) {
            airlineRoutes.addAll(airlineRouteRepository.findAllByAirlineAndCodeshare(airline, true));
        }

        log.info("FOUND AIRLINES ROUTES " + airlineRoutes.size());

        airlineRoutes.forEach(ar -> {
            AirlineRouteDto airlineRouteDto = new AirlineRouteDto();

            airlineRouteDto.setCodeshare(ar.getCodeshare());
            airlineRouteDto.setAircraftTypes(ar.getAirplaneTypes());
            airlineRouteDto.setId(ar.getId());

            airlineRouteDto.setOriginName(ar.getOrigin().getAirportName());
            airlineRouteDto.setOriginIataCode(ar.getOrigin().getIataCode());
            airlineRouteDto.setOriginIcaoCode(ar.getOrigin().getIcaoCode());
            airlineRouteDto.setOriginCityName(ar.getOrigin().getCity());
            airlineRouteDto.setOriginCountry(ar.getOrigin().getCountry());
            airlineRouteDto.setOriginLatitude(ar.getOrigin().getLatitude());
            airlineRouteDto.setOriginLongitude(ar.getOrigin().getLongitude());

            airlineRouteDto.setDestinationName(ar.getDestination().getAirportName());
            airlineRouteDto.setDestinationIataCode(ar.getDestination().getIataCode());
            airlineRouteDto.setDestinationIcaoCode(ar.getDestination().getIcaoCode());
            airlineRouteDto.setDestinationCityName(ar.getDestination().getCity());
            airlineRouteDto.setDestinationCountry(ar.getDestination().getCountry());
            airlineRouteDto.setDestinationLatitude(ar.getDestination().getLatitude());
            airlineRouteDto.setDestinationLongitude(ar.getDestination().getLongitude());

            airlineRouteDtoList.add(airlineRouteDto);
        });

        return airlineRouteDtoList;
    }

    @Override
    public List<AirlineRoute> getRoutesFromAirport(Airport airport) {
        return airlineRouteRepository.findAllByOriginOrDestinationAndCodeshare(airport, airport, false);
    }

}

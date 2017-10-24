package frequentflyer.com.services.impl;

import frequentflyer.com.domain.AirlineDto;
import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.repositories.AirlineRepository;
import frequentflyer.com.services.AirlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.persistence.NonUniqueResultException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.function.Function;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Service
@Slf4j
public class AirlineServiceImpl implements AirlineService {

    private final String COMMA = ",";

    @Autowired
    private AirlineRepository airlineRepository;

    @Override
    public void loadAirlines(InputStream inputStream) {
        log.info("AirlineServiceImpl.loadAirlines | Loading airlines to DB");


        try{
            log.info("AirlineServiceImpl.loadAirlines | Reading file...");

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            // skip the header of the csv
            br.lines().map(mapToItem).forEach(a -> {
                if (a != null) {
                    airlineRepository.save(a);
                }
            });
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private Function<String, Airline> mapToItem = (line) -> {
        String[] p = line.split(COMMA);
        Airline airline = null;
        if (!trimQuotes(p[3]).equalsIgnoreCase("") && trimQuotes(p[3]).length() == 2 &&
                trimQuotes(p[7]).equalsIgnoreCase("Y")) {
            airline = new Airline();
            airline.setAirlineName(trimQuotes(p[1]));
            airline.setIataCode(trimQuotes(p[3]));
            airline.setIcaoCode(trimQuotes(p[4]));
            airline.setCountry(trimQuotes(p[6]));
            airline.setUniqueId(trimQuotes(p[0]));
        }
        return airline;
    };

    private String trimQuotes (String s) {
        return s.replaceAll("^\"|\"$", "");
    }


    @Override
    public Airline findAirlineByIataCode(String iataCode) {
        try {
            return airlineRepository.findByIataCode(iataCode.toUpperCase());
        } catch (Exception e) {
            log.error(" NON UNIQUE AIRLINE BY IATA CODE " + iataCode);
            return null;
        }
    }

    @Override
    public Airline findAirlineByUniqueCode(String uniqueCode) {
        return airlineRepository.findByUniqueId(uniqueCode);
    }

    @Override
    public AirlineSearchDto partialSearch(String searchCriteria) {
        AirlineSearchDto airlineSearchDto = new AirlineSearchDto();

        Page<Airline> airlines = airlineRepository.findByAirlineNameContainsAndIataCodeContainsAndIcaoCodeContainsAndCountryContains(searchCriteria,
                searchCriteria, searchCriteria, searchCriteria, new PageRequest(0, 50));

        airlineSearchDto.setTotalNum(airlines.getTotalElements());

        List<AirlineDto> airlineDtoList = new ArrayList<>();

        airlines.forEach(a -> {
            AirlineDto airlineDto = new AirlineDto();
            airlineDto.setCountry(a.getCountry());
            airlineDto.setIataCode(a.getIataCode());
            airlineDto.setIcaoCode(a.getIcaoCode());
            airlineDto.setName(a.getAirlineName());
            airlineDto.setUniqueId(a.getUniqueId());
            airlineDtoList.add(airlineDto);
        });

        airlineSearchDto.setAirlineDtoList(airlineDtoList);

        return airlineSearchDto;
    }

}

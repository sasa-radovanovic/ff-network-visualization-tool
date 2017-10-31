package frequentflyer.com.services.impl;

import frequentflyer.com.domain.AirlineDto;
import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.domain.DomainMapper;
import frequentflyer.com.entities.Airline;
import frequentflyer.com.repositories.AirlineRepository;
import frequentflyer.com.services.AirlineService;
import frequentflyer.com.services.exceptions.NvtServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

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
public class AirlineServiceImpl implements AirlineService {

    private final String COMMA = ",";

    /**
     * {@inheritDoc}
     */
    @Autowired
    private AirlineRepository airlineRepository;

    /**
     * {@inheritDoc}
     */
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

    /**
     * Map line from input file to database entity
     */
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

    /**
     * Trim quotes from input data
     * @param s
     * @return
     */
    private String trimQuotes (String s) {
        return s.replaceAll("^\"|\"$", "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Airline findAirlineByIataCode(String iataCode) {
        try {
            return airlineRepository.findByIataCode(iataCode.toUpperCase());
        } catch (Exception e) {
            log.error(" NON UNIQUE AIRLINE BY IATA CODE " + iataCode);
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Airline findAirlineByUniqueCode(String uniqueCode) {
        return airlineRepository.findByUniqueId(uniqueCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AirlineSearchDto partialSearch(String searchCriteria) {

        log.info(" Search for airlines " + searchCriteria);
        AirlineSearchDto airlineSearchDto = new AirlineSearchDto();

        Page<Airline> airlines = airlineRepository.findByAirlineNameContainsOrIataCodeContainsOrIcaoCodeContainsOrCountryContains(searchCriteria,
                searchCriteria, searchCriteria, searchCriteria, new PageRequest(0, 50));

        log.info(" Retrieved from DB " + airlines.getTotalElements());

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

    /**
     * {@inheritDoc}
     */
    @Override
    public AirlineDto getAirlineData(String uniqueId) {
        Airline airline = airlineRepository.findByUniqueId(uniqueId);
        if (airline == null) {
            throw new NvtServiceException("No airline found");
        }
        return DomainMapper.airlineToAirlineDto(airline);
    }

}

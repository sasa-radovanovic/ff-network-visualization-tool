package frequentflyer.com.controllers.rest;

import frequentflyer.com.domain.AirportDetailed;
import frequentflyer.com.domain.AirportSearchDto;
import frequentflyer.com.domain.AirportVicinityStats;
import frequentflyer.com.services.AirportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sasaradovanovic on 10/21/17.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AirportController {


    @Autowired
    private AirportService airportService;


    @RequestMapping(value = "airports", method = RequestMethod.GET)
    @ResponseBody
    public AirportSearchDto airportPartialSearch(@RequestParam(required = true) String searchCriteria) {
        log.info("Airport search for " + searchCriteria);
        return airportService.partialSearch(searchCriteria);
    }


    @RequestMapping(value = "airports/stats", method = RequestMethod.GET)
    @ResponseBody
    public AirportDetailed airportData(@RequestParam(required = true) String iataCode) {
        log.info("Airport data for " + iataCode);
        return airportService.airportData(iataCode);
    }

    @RequestMapping(value = "airports/vicinity", method = RequestMethod.GET)
    @ResponseBody
    public AirportVicinityStats airportVicinity(@RequestParam(required = true) String iata,
                                                @RequestParam(required = true) int radius) {
        return airportService.airportsInVicinity(iata, radius);
    }


}

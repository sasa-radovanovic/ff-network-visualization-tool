package frequentflyer.com.controllers.rest;

import frequentflyer.com.domain.AirlineRouteDto;
import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.services.AirlineRouteService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AirlineRouteController {

    @Autowired
    private AirlineRouteService airlineRouteService;

    @RequestMapping(value = "airline-routes", method = RequestMethod.GET)
    @ResponseBody
    public List<AirlineRouteDto> retrieveAirlineRoutes(@RequestParam(required = true) String uniqueId, @RequestParam(required = true) Boolean codeshare) {
        return airlineRouteService.getAirlineRoutes(uniqueId, codeshare);
    }

}

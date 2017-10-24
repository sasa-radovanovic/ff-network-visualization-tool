package frequentflyer.com.controllers.rest;

import frequentflyer.com.domain.AirlineSearchDto;
import frequentflyer.com.services.AirlineService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class AirlineController {

    @Autowired
    private AirlineService airlineService;


    @RequestMapping(value = "airlines", method = RequestMethod.GET)
    @ResponseBody
    public AirlineSearchDto partialSearch(@RequestParam(required = true) String searchCriteria) {
        return airlineService.partialSearch(searchCriteria);
    }

}

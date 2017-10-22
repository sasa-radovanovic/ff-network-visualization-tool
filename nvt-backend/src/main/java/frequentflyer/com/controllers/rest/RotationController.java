package frequentflyer.com.controllers.rest;

import frequentflyer.com.domain.CreateRotationDto;
import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.services.RotationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class RotationController {

    @Autowired
    private RotationService rotationService;


    @RequestMapping(value = "rotations", method = RequestMethod.GET)
    @ResponseBody
    public List<RotationDto> getAllRotationsForCombination(@RequestParam String combinationId) {
        Long id = Long.parseLong(combinationId);
        return rotationService.getAllRotationsForCombination(id);
    }


    @RequestMapping(value = "rotations", method = RequestMethod.POST)
    public void createRotation(@RequestBody CreateRotationDto createRotationDto) {
        rotationService.createNewRotation(createRotationDto.getOrigin(), createRotationDto.getDestination(),
                createRotationDto.getFrequency(),
                createRotationDto.getLocalDepartureTime(),
                createRotationDto.getFlightLength(),
                createRotationDto.getCombinationId());
    }



    @RequestMapping(value = "rotations", method = RequestMethod.DELETE)
    public void createRotation(@RequestParam String rotationId) {
        rotationService.removeRotation(Long.parseLong(rotationId));
    }

}

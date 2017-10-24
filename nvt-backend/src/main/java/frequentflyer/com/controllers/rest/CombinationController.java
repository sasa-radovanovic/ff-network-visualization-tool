package frequentflyer.com.controllers.rest;

import frequentflyer.com.domain.CombinationDto;
import frequentflyer.com.domain.CreateCombinationDto;
import frequentflyer.com.services.CombinationService;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Slf4j
@RestController
@RequestMapping("/api")
public class CombinationController {

    @Autowired
    private CombinationService combinationService;


    @RequestMapping(value = "combinations", method = RequestMethod.GET)
    @ResponseBody
    public List<CombinationDto> getAllCombinations() {
        return combinationService.getAllCombinations();
    }


    @RequestMapping(value = "combinations", method = RequestMethod.POST)
    public void createCombination (@RequestBody(required = true) CreateCombinationDto createCombinationDto) throws ServiceException {
        combinationService.createCombination(createCombinationDto.getName(), createCombinationDto.getColor(), createCombinationDto.getRotations());
    }


    @RequestMapping(value = "combinations", method = RequestMethod.DELETE)
    public void deleteCombination (@RequestParam(required = true) String id) throws ServiceException {
        long combinationId = Long.parseLong(id);
        combinationService.removeCombination(combinationId);
    }

}

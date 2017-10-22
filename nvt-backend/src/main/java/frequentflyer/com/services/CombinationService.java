package frequentflyer.com.services;

import frequentflyer.com.domain.CombinationDto;
import frequentflyer.com.domain.CreateCombinationRotationDto;
import frequentflyer.com.entities.Combination;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface CombinationService {

    void createCombination (String name, String color, List<CreateCombinationRotationDto> rotations);

    void removeCombination (Long id);

    List<CombinationDto> getAllCombinations();

    Combination findById (Long id);
}

package frequentflyer.com.services;

import frequentflyer.com.domain.CombinationDto;
import frequentflyer.com.domain.CreateCombinationRotationDto;
import frequentflyer.com.entities.Combination;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface CombinationService {

    /**
     *
     * Create a new combination
     *
     * @param name - Name of combination
     * @param color - Color of combination
     * @param rotations - List of {@link CreateCombinationRotationDto} objects
     */
    void createCombination (String name, String color, List<CreateCombinationRotationDto> rotations);

    /**
     *
     * Remove combination
     *
     * @param id
     */
    void removeCombination (Long id);

    /**
     *
     * Retrieve all combinations
     *
     * @return - List of {@link CombinationDto} objects
     */
    List<CombinationDto> getAllCombinations();

    /**
     *
     * Retrieve combination based on id
     *
     * @param id
     * @return
     */
    Combination findById (Long id);
}

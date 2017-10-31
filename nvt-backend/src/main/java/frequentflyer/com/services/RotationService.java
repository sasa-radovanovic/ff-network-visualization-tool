package frequentflyer.com.services;

import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.services.exceptions.NvtServiceException;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface RotationService {

    /**
     *
     * Remove all rotations from a combination
     *
     * @param combination
     */
    void removeAllRotationsForCombination(Combination combination);

    /**
     *
     * Create new rotations
     *
     * @param origin - IATA code of orgin airport
     * @param destination - destination code of destination airport
     * @param frequency - Frequency string, i.e. '1/3/7'
     * @param localDepartureTime - HH:mm string
     * @param flightLength - Total flight length in minutes
     * @param combinationId - ID combination
     * @throws NvtServiceException
     */
    void createNewRotation(String origin, String destination, String frequency,
                           String localDepartureTime, int flightLength, long combinationId) throws NvtServiceException;


    /**
     *
     * Retrieve all rotations from a combination
     *
     * @param combinationId - ID of combination
     * @return List of {@link RotationDto} objects
     */
    List<RotationDto> getAllRotationsForCombination(long combinationId);

    /**
     *
     * Remove certain rotation
     *
     * @param id - rotationId
     */
    void removeRotation(long id);

}

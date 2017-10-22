package frequentflyer.com.services;

import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.services.exceptions.NvtServiceException;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface RotationService {

    void removeAllRotationsForCombination(Combination combination);

    void createNewRotation(String origin, String destination, String frequency,
                           String localDepartureTime, int flightLength, long combinationId) throws NvtServiceException;

    List<RotationDto> getAllRotationsForCombination(long combinationId);

    void removeRotation(long id);

}

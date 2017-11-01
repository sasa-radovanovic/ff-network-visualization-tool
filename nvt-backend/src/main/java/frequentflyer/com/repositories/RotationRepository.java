package frequentflyer.com.repositories;


import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Repository
public interface RotationRepository extends CrudRepository<Rotation, Long> {

    /**
     *
     * Retrieve Rotating based on Origin airport, Destination airport and combination
     *
     * @param origin
     * @param destination
     * @param combination
     * @return {@link Rotation} object
     */
    Rotation findByOriginAndDestinationAndCombination(Airport origin, Airport destination, Combination combination);

    /**
     *
     * Find all rotations in the system
     *
     * @param combination
     * @return List of {@link Rotation} objects
     */
    List<Rotation> findAllByCombination(Combination combination);
}

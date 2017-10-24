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

    Rotation findByOriginAndDestinationAndCombination(Airport origin, Airport destination, Combination combination);

    List<Rotation> findAllByCombination(Combination combination);
}

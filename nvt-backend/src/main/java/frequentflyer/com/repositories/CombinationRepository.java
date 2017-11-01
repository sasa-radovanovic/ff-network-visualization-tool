package frequentflyer.com.repositories;

import frequentflyer.com.entities.Combination;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Repository
public interface CombinationRepository extends CrudRepository<Combination, Long> {

    /**
     *
     * Retrieve combination by name
     *
     * @param combinationName
     * @return {@link Combination} object
     */
    Combination findByCombinationName(String combinationName);

    /**
     *
     * Retrieve combination by ID
     *
     * @param id
     * @return {@link Combination}
     */
    Combination findById(Long id);

    /**
     *
     * Retrieve all combinations
     *
     * @return List of {@link Combination} objects
     */
    List<Combination> findAll();
}

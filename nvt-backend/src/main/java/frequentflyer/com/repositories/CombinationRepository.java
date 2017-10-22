package frequentflyer.com.repositories;

import frequentflyer.com.entities.Combination;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface CombinationRepository extends CrudRepository<Combination, Long> {

    Combination findByCombinationName(String combinationName);

    Combination findById(Long id);

    List<Combination> findAll();
}

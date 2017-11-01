package frequentflyer.com.services.impl;

import frequentflyer.com.domain.CombinationDto;
import frequentflyer.com.domain.CreateCombinationRotationDto;
import frequentflyer.com.domain.DomainMapper;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import frequentflyer.com.repositories.AirportRepository;
import frequentflyer.com.repositories.CombinationRepository;
import frequentflyer.com.repositories.RotationRepository;
import frequentflyer.com.services.CombinationService;
import frequentflyer.com.services.RotationService;
import frequentflyer.com.services.exceptions.NvtServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Service
@Slf4j
public class CombinationServiceImpl implements CombinationService {

    @Autowired
    private CombinationRepository combinationRepository;

    @Autowired
    private RotationService rotationService;

    @Autowired
    private AirportRepository airportRepository;

    @Autowired
    private RotationRepository rotationRepository;


    /**
     * {@inheritDoc
     */
    @Override
    public void createCombination(String name, String color, List<CreateCombinationRotationDto> rotations) {

        // Check if there is already combination with given name
        Combination combination = combinationRepository.findByCombinationName(name);
        if (combination != null) {
            log.info(combination.getCombinationName());
            log.error(" Combination already exists ");
            throw new NvtServiceException("Combination already exists.");
        }

        combination = new Combination();
        combination.setCombinationName(name);
        combination.setCombinationColor(color);

        final Combination savedCombination = combinationRepository.save(combination);

        // Save all rotations for new combination
        rotations.forEach(rot -> {
            Airport origin = airportRepository.findByIataCode(rot.getOrigin().toUpperCase());
            Airport destination = airportRepository.findByIataCode(rot.getDestination().toUpperCase());
            Rotation rotation = new Rotation();
            rotation.setOrigin(origin);
            rotation.setDestination(destination);
            rotation.setFrequency(rot.getFrequency());
            rotation.setFlightLength(rot.getFlightLength());
            rotation.setLocalDepartureTime(rot.getDepartureTime());
            rotation.setCombination(savedCombination);
            rotationRepository.save(rotation);
        });
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void removeCombination(Long id) {
        Combination combination = combinationRepository.findById(id);
        if (combination == null) {
            throw new NvtServiceException("Combination does not exist.");
        }
        // Remove all rotations for given combination
        rotationService.removeAllRotationsForCombination(combination);
        combinationRepository.delete(combination);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<CombinationDto> getAllCombinations() {
        List<Combination> combinationList = combinationRepository.findAll();
        List<CombinationDto> combinationDtos = combinationList.stream()
                .map(c -> DomainMapper.combinationToCombinationDto(c))
                .collect(Collectors.toList());
        return combinationDtos;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public Combination findById(Long id) {
        return combinationRepository.findById(id);
    }


}

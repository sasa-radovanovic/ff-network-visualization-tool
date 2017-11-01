package frequentflyer.com.services.impl;

import frequentflyer.com.domain.DomainMapper;
import frequentflyer.com.domain.RotationDto;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import frequentflyer.com.repositories.RotationRepository;
import frequentflyer.com.services.AirportService;
import frequentflyer.com.services.CombinationService;
import frequentflyer.com.services.RotationService;
import frequentflyer.com.services.exceptions.NvtServiceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Service
public class RotationServiceImpl implements RotationService {


    @Autowired
    private RotationRepository rotationRepository;

    @Autowired
    private CombinationService combinationService;

    @Autowired
    private AirportService airportService;

    /**
     * {@inheritDoc
     */
    @Override
    public void removeAllRotationsForCombination(Combination combination) {

        List<Rotation> rotationList = rotationRepository.findAllByCombination(combination);
        if (rotationList != null) {
            rotationList.forEach(rotation -> rotationRepository.delete(rotation));
        }
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void createNewRotation(String origin, String destination, String frequency,
                                  String localDepartureTime, int flightLength, long combinationId) throws NvtServiceException {

        // Retrieve combination
        Combination combination = combinationService.findById(combinationId);
        if (combination == null) {
            throw new NvtServiceException("No Combination found.");
        }

        // Retrieve origin and destination airports
        Airport originAirport = airportService.findByAirportCode(origin.toUpperCase());
        Airport destinationAirport = airportService.findByAirportCode(destination.toUpperCase());
        if (originAirport == null || destinationAirport == null) {
            throw new NvtServiceException("Non-existing airport.");
        }

        // Create rotation
        Rotation rotation = new Rotation();
        rotation.setOrigin(originAirport);
        rotation.setDestination(destinationAirport);
        rotation.setCombination(combination);
        rotation.setLocalDepartureTime(localDepartureTime);
        rotation.setFrequency(frequency);
        rotation.setFlightLength(flightLength);

        // Validate rotation
        validateRotation(rotation);

        rotationRepository.save(rotation);
    }

    /**
     * {@inheritDoc
     */
    @Override
    public List<RotationDto> getAllRotationsForCombination(long combinationId) {
        Combination combination = combinationService.findById(combinationId);
        if (combination == null) {
            throw new NvtServiceException("Unknown combination.");
        }
        List<Rotation> rotations = rotationRepository.findAllByCombination(combination);
        List<RotationDto> rotationDtos = rotations.stream()
                .map(rotation -> DomainMapper.rotationToRotationDto(rotation))
                .collect(Collectors.toList());
        return rotationDtos;
    }

    /**
     * {@inheritDoc
     */
    @Override
    public void removeRotation(long id) {
        Rotation rotation = rotationRepository.findOne(id);
        if (rotation != null) {
            rotationRepository.delete(rotation);
        }
    }

    /**
     *
     * Validate rotations
     *
     * @param rotation
     */
    private void validateRotation(Rotation rotation) {
        // Validate frequency
        if (rotation.getFrequency() == null || rotation.getFrequency().length() == 0) {
            throw new NvtServiceException("Frequency non existing.");
        }
        String[] frequencySegments = rotation.getFrequency().split("/");
        if (frequencySegments.length == 0) {
            throw new NvtServiceException("No frequency segments.");
        }
        // Validate days in freqency
        for (String day: frequencySegments) {
            try {
                int dayNum = Integer.parseInt(day);
                if (dayNum < 1 || dayNum > 7) {
                    throw new NvtServiceException("Invalid day (1<x<=7)");
                }
            } catch (NumberFormatException nfe) {
                throw new NvtServiceException("Invalid day in frequency.");
            }
        }
    }

}

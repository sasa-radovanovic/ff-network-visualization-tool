package frequentflyer.com.domain;

import frequentflyer.com.entities.Airline;
import frequentflyer.com.entities.Airport;
import frequentflyer.com.entities.Combination;
import frequentflyer.com.entities.Rotation;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Slf4j
public class DomainMapper {

    private static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("HH:mm");


    /**
     *
     * Map Combination to Combination DTO object
     *
     * @param combination
     * @return {@link CombinationDto} object
     */
    public static CombinationDto combinationToCombinationDto(Combination combination) {
        CombinationDto combinationDto = new CombinationDto();
        combinationDto.setId(combination.getId());
        combinationDto.setName(combination.getCombinationName());
        combinationDto.setColor(combination.getCombinationColor());
        return combinationDto;
    }

    /**
     *
     * Map rotation to Rotation DTO object
     *
     * @param rotation
     * @return
     */
    public static RotationDto rotationToRotationDto(Rotation rotation) {

        RotationDto rotationDto = new RotationDto();

        Airport origin = rotation.getOrigin();
        Airport destination = rotation.getDestination();

        rotationDto.setOriginIataCode(origin.getIataCode());
        rotationDto.setOriginIcaoCode(origin.getIcaoCode());
        rotationDto.setOriginName(origin.getAirportName());
        rotationDto.setOriginCityName(origin.getCity());
        rotationDto.setOriginCountry(origin.getCountry());
        rotationDto.setOriginTimezone(origin.getTimezone().getDisplayName());
        rotationDto.setOriginLatitude(origin.getLatitude());
        rotationDto.setOriginLongitude(origin.getLongitude());


        rotationDto.setDestinationIataCode(destination.getIataCode());
        rotationDto.setDestinationIcaoCode(destination.getIcaoCode());
        rotationDto.setDestinationName(destination.getAirportName());
        rotationDto.setDestinationCityName(destination.getCity());
        rotationDto.setDestinationCountry(destination.getCountry());
        rotationDto.setDestinationTimezone(destination.getTimezone().getDisplayName());
        rotationDto.setDestinationLatitude(destination.getLatitude());
        rotationDto.setDestinationLongitude(destination.getLongitude());


        rotationDto.setLocalDepartureTime(rotation.getLocalDepartureTime());

        // Set departure tine and length
        LocalTime lt = LocalTime.parse(rotation.getLocalDepartureTime());


        // Retrieve time zone difference between local departure time and UTC today
        long tzDiff = origin.getTimezone().getOffset(new Date().getTime());


        // Form Local Time in UTC
        LocalTime utcStandardized;

        if (tzDiff > 0) {
            utcStandardized = lt.minusSeconds(tzDiff / 1000);
        } else if (tzDiff < 0) {
            utcStandardized = lt.plusSeconds(Math.abs(tzDiff) / 1000);
        } else {
            utcStandardized = lt;
        }

        // Readapt days
        // For example if flight departs on Monday at 01:00 local time, but
        // local departure time is 2h in front of utc, that means that in UTC
        // departure time is on Sunday at 23:00
        // Other way (i.e. timezone behind UTC 4 hours and LDT is Sunday at 22:00) applies too
        if (tzDiff > 0) {
            if (utcStandardized.getHour() > lt.getHour()) {
                // readapt days
                log.info("Timezone diff was > 0. Adapting frequencies to utc... ");
                rotationDto.setUtcDayMap(formDayMap(rotation.getFrequency(), -1));
            } else {
                rotationDto.setUtcDayMap(formDayMap(rotation.getFrequency(), 0));
            }
        } else if (tzDiff < 0) {
            if (utcStandardized.getHour() < lt.getHour()) {
                // readapt days
                log.info("Timezone diff was < 0. Adapting frequencies to utc... ");
                rotationDto.setUtcDayMap(formDayMap(rotation.getFrequency(), 1));
            } else {
                rotationDto.setUtcDayMap(formDayMap(rotation.getFrequency(), 0));
            }
        } else {
            rotationDto.setUtcDayMap(formDayMap(rotation.getFrequency(), 0));
        }


        rotationDto.setDayMap(formDayMap(rotation.getFrequency(), 0));

        rotationDto.setUtcDepartureTime(utcStandardized.format(dtf));
        rotationDto.setFlightTime(rotation.getFlightLength());
        rotationDto.setId(rotation.getId());


        return rotationDto;
    }

    /**
     *
     * Map Airline to Airline DTO object
     *
     * @param airline - {@link Airline} object
     * @return {@link AirlineDto} object
     */
    public static AirlineDto airlineToAirlineDto(Airline airline) {
        AirlineDto airlineDto = new AirlineDto();
        airlineDto.setName(airline.getAirlineName());
        airlineDto.setIataCode(airline.getIataCode());
        airlineDto.setIcaoCode(airline.getIcaoCode());
        airlineDto.setCountry(airline.getCountry());
        airlineDto.setUniqueId(airline.getUniqueId());
        return airlineDto;
    }

    /**
     *
     * Form day map for Rotation object consisting of mappings DAY_NO : flying_that_day
     *
     * @param frequencyString - i.e. "1/4/5/6/7"
     * @param shift - weather or not to readapt values due to UTC shift
     * @return - HashMap of DAY_NO : flying_that_day values
     */
    private static HashMap<String, Boolean> formDayMap (String frequencyString, int shift) {
        HashMap<String, Boolean> dayMap = new HashMap<>();
        for (int i=1; i<=7; i++) {
            if (frequencyString.contains(String.valueOf(i))) {
                dayMap.put(String.valueOf(getDayIndexWithShift(i, shift)), true);
            } else {
                dayMap.put(String.valueOf(getDayIndexWithShift(i, shift)), false);
            }
        }
        return dayMap;
    }

    /**
     *
     * Get number of day taking into account UTC shift
     *
     * @param i
     * @param shift
     * @return ordinal number of day
     */
    private static int getDayIndexWithShift(int i, int shift) {
        if (shift > 0) {
            return (i + shift) <= 7 ? (i + shift) : 1;
        } else if (shift < 0) {
            return (i + shift) >= 1 ? (i + shift) : 7;
        } else {
            return i;
        }
    }

}

package frequentflyer.com.domain;

import lombok.Data;

import java.util.HashMap;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Data
public class RotationDto {

    private String originIataCode;

    private String originIcaoCode;

    private String originName;

    private String originCityName;

    private String originCountry;

    private String originTimezone;

    private String destinationIataCode;

    private String destinationIcaoCode;

    private String destinationName;

    private String destinationCityName;

    private String destinationCountry;

    private String destinationTimezone;

    private HashMap<String, Boolean> dayMap;

    private String utcDepartureTime;

    private int flightTime;

}

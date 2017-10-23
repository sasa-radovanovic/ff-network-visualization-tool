package frequentflyer.com.domain;

import lombok.Data;

import java.util.HashMap;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Data
public class RotationDto {

    public long id;

    private String originIataCode;

    private String originIcaoCode;

    private String originName;

    private String originCityName;

    private String originCountry;

    private String originTimezone;

    private Double originLatitude;

    private Double originLongitude;

    private String destinationIataCode;

    private String destinationIcaoCode;

    private String destinationName;

    private String destinationCityName;

    private String destinationCountry;

    private String destinationTimezone;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private HashMap<String, Boolean> utcDayMap;

    private HashMap<String, Boolean> dayMap;

    private String utcDepartureTime;

    private String localDepartureTime;

    private int flightTime;

}

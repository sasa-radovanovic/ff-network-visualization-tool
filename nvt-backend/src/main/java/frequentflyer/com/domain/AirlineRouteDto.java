package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Data
public class AirlineRouteDto {

    private long id;

    private String originIataCode;

    private String originIcaoCode;

    private String originName;

    private String originCityName;

    private String originCountry;

    private Double originLatitude;

    private Double originLongitude;

    private String destinationIataCode;

    private String destinationIcaoCode;

    private String destinationName;

    private String destinationCityName;

    private String destinationCountry;

    private Double destinationLatitude;

    private Double destinationLongitude;

    private String aircraftTypes;


}

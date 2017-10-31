package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/31/17.
 */
@Data
public class AirportVicinitySingleAirport {

    private double longitude;

    private double latitude;

    private String name;

    private String city;

    private String country;

    private int carriers;

    private int routes;

    private String iataCode;

    private String icaoCode;

}

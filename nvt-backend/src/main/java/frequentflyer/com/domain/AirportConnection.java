package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/29/17.
 */
@Data
public class AirportConnection {

    private String iataCode;

    private String icaoCode;

    private String name;

    private String city;

    private String country;

    private double longitude;

    private double latitude;

}

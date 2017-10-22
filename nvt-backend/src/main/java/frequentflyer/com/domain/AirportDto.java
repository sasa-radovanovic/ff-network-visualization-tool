package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/21/17.
 */
@Data
public class AirportDto {

    private String iataCode;

    private String icaoCode;

    private String name;

    private String city;

    private String country;

    private String timezone;
}

package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Data
public class AirlineDto {

    private String uniqueId;

    private String iataCode;

    private String icaoCode;

    private String name;

    private String country;

}

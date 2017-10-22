package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/22/17.
 */
@Data
public class CreateCombinationRotationDto {

    int id;

    String origin;

    String destination;

    String frequency;

    String departureTime;

    int flightLength;

}

package frequentflyer.com.domain;

import lombok.Data;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Data
public class CreateRotationDto {

    String origin;

    String destination;

    String frequency;

    String localDepartureTime;

    int flightLength;

    long combinationId;
}

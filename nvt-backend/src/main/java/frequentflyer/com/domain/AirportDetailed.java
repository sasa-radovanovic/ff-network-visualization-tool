package frequentflyer.com.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/29/17.
 */
@Data
public class AirportDetailed {

    private double longitude;

    private double latitude;

    private List<AirlineDto> operatingCarriers;

    private List<AirportConnection> connections;
}

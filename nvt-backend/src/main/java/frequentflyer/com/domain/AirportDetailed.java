package frequentflyer.com.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.List;

/**
 * Created by sasaradovanovic on 10/29/17.
 */
@Data
public class AirportDetailed {

    private double longitude;

    private double latitude;

    private HashMap<String, AirlineDto> operatingCarriers;

    private List<AirportConnection> connections;
}

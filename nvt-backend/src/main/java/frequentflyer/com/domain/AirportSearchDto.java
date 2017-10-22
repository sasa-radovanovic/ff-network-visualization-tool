package frequentflyer.com.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/21/17.
 */
@Data
public class AirportSearchDto {

    private long totalNum;

    private List<AirportDto> airportDtoList;

}

package frequentflyer.com.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Data
public class AirlineSearchDto {

    private long totalNum;

    private List<AirlineDto> airlineDtoList;
}

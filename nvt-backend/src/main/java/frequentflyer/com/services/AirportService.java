package frequentflyer.com.services;

import frequentflyer.com.domain.AirportDetailed;
import frequentflyer.com.domain.AirportSearchDto;
import frequentflyer.com.entities.Airport;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
public interface AirportService {

    void loadAirports(InputStream inputStream);

    Airport findByAirportCode(String code);

    AirportSearchDto partialSearch(String searchCriteria);

    AirportDetailed airportData(String iataCode);

}

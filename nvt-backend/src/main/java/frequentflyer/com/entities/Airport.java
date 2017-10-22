package frequentflyer.com.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.util.TimeZone;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Entity
@Data
public class Airport {

    @Id
    @GeneratedValue
    @Column(name="AIRPORT_ID")
    private long id;

    @Column
    private String airportName;

    @Column
    private String city;

    @Column
    private String country;

    @Column
    private String iataCode;

    @Column
    private String icaoCode;

    @Column
    private double latitude;

    @Column
    private double longitude;

    @Column
    private TimeZone timezone;

}

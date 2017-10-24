package frequentflyer.com.entities;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Entity
@Data
public class Airline {

    @Id
    @GeneratedValue
    @Column(name="AIRLINE_ID")
    private Long id;

    @Column
    private String airlineName;

    @Column(length = 2)
    private String iataCode;

    @Column(length = 3)
    private String icaoCode;

    private String country;

    private String uniqueId;

}

package frequentflyer.com.entities;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by sasaradovanovic on 10/24/17.
 */
@Entity
@Data
public class AirlineRoute {


    @Id
    @GeneratedValue
    @Column(name="AIRLINE_ROUTE_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name="ORIGIN_AIRPORT_ID")
    private Airport origin;

    @ManyToOne
    @JoinColumn(name="DESTINATION_AIRPORT_ID")
    private Airport destination;


    @ManyToOne
    @JoinColumn(name="AIRLINE_ID")
    private Airline airline;

    @Column
    private String airplaneTypes;

    @Column
    private Boolean codeshare;

}

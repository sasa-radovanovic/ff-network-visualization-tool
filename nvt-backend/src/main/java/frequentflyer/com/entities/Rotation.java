package frequentflyer.com.entities;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Entity
@Data
public class Rotation {

    @Id
    @GeneratedValue
    @Column(name="ROTATION_ID")
    private long id;

    @ManyToOne
    @JoinColumn(name="ORIGIN_AIRPORT_ID")
    private Airport origin;

    @ManyToOne
    @JoinColumn(name="DESTINATION_AIRPORT_ID")
    private Airport destination;

    @Column
    private String frequency;

    @Column
    private String localDepartureTime;

    @Column
    private Integer flightLength;

    @ManyToOne
    @JoinColumn(name="COMBINATION_ID")
    private Combination combination;

}

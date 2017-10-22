package frequentflyer.com.entities;

import lombok.Data;

import javax.persistence.*;

/**
 * Created by sasaradovanovic on 10/10/17.
 */
@Entity
@Data
public class Combination {

    @Id
    @GeneratedValue
    @Column(name="COMBINATION_ID")
    private long id;

    @Column
    private String combinationName;

    @Column
    private String combinationColor;

}

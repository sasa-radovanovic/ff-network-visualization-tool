package frequentflyer.com.domain;

import lombok.Data;

import java.util.List;

/**
 * Created by sasaradovanovic on 10/17/17.
 */
@Data
public class CreateCombinationDto {

    private String name;

    private String color;

    private List<CreateCombinationRotationDto> rotations;

}

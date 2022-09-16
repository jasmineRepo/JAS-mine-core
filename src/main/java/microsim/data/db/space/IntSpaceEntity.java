package microsim.data.db.space;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
public class IntSpaceEntity implements IntegerSpaceEntity {

    @Setter
    @Getter
    private Double simulationTime;

    @Setter
    @Getter
    private Long simulationRun;

    @Setter
    @Getter
    private Integer x;

    @Setter
    @Getter
    private Integer y;

    @Setter
    @Getter
    private Integer value;
}

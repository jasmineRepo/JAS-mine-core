package microsim.data.db.space;

import jakarta.persistence.Embeddable;

@Embeddable
public interface IntegerSpaceEntity {

    Double getSimulationTime();

    void setSimulationTime(Double time);

    Long getSimulationRun();

    void setSimulationRun(Long run);

    Integer getX();

    void setX(Integer x);

    Integer getY();

    void setY(Integer y);

    Integer getValue();

    void setValue(Integer value);

}

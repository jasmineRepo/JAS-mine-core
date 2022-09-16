package microsim.data.db.space;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import lombok.NonNull;
import microsim.engine.SimulationEngine;
import microsim.space.IntSpace;
import microsim.space.ObjectSpace;

import java.lang.reflect.Field;

public class SpaceEntityPersister {

    public static void persistIntSpace(final @NonNull EntityManager entityManager,
                                       final @NonNull IntSpace space,
                                       final @NonNull Class<? extends IntegerSpaceEntity> entityClass) {
        EntityTransaction tx;
        tx = entityManager.getTransaction();
        tx.begin();

        try {
            for (int x = 0; x < space.getXSize(); x++) {
                for (int y = 0; y < space.getYSize(); y++) {
                    final IntegerSpaceEntity entity = entityClass.newInstance();
                    entity.setSimulationRun((long) SimulationEngine.getInstance().getCurrentRunNumber());
                    entity.setSimulationTime(SimulationEngine.getInstance().getTime());
                    entity.setX(x);
                    entity.setY(y);
                    entity.setValue(space.get(x, y));
                    entityManager.persist(entity);
                }
            }
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            try {
                throw e;
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        tx.commit();
    }

    public static void persistObjectSpace(final @NonNull EntityManager entityManager,
                                          final @NonNull ObjectSpace space,
                                          final @NonNull Class<? extends IntegerSpaceEntity> entityClass,
                                          final @NonNull String idField) {
        EntityTransaction tx;
        tx = entityManager.getTransaction();
        tx.begin();

        Field field;
        try {
            field = entityClass.getField(idField);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        field.setAccessible(true);

        try {
            for (int x = 0; x < space.getXSize(); x++) {
                for (int y = 0; y < space.getYSize(); y++) {
                    final IntegerSpaceEntity entity = entityClass.newInstance();
                    entity.setSimulationRun((long) SimulationEngine.getInstance().getCurrentRunNumber());
                    entity.setSimulationTime(SimulationEngine.getInstance().getTime());
                    entity.setX(x);
                    entity.setY(y);
                    final long value = field.getLong(space.get(x, y));
                    entity.setValue((int) value);
                    entityManager.persist(entity);
                }
            }
        } catch (Exception e) {
            if (tx.isActive())
                tx.rollback();
            try {
                throw e;
            } catch (InstantiationException | IllegalAccessException ex) {
                throw new RuntimeException(ex);
            }
        }

        tx.commit();
    }
}

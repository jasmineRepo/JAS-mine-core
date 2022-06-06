package microsim.data.db.space;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import microsim.engine.SimulationEngine;
import microsim.space.IntSpace;
import microsim.space.ObjectSpace;

import java.lang.reflect.Field;

public class SpaceEntityPersister {

	public static void persistIntSpace(EntityManager entityManager, IntSpace space, Class<? extends IIntSpaceEntity> entityClass) throws Exception {
		EntityTransaction tx;
		tx = entityManager.getTransaction();
		tx.begin();
		
		try {
			for (int x = 0; x < space.getXSize(); x++) {
				for (int y = 0; y < space.getYSize(); y++) {
					final IIntSpaceEntity entity = entityClass.newInstance();
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
			throw e;
		}
		
		tx.commit();
	}

	public static void persistObjectSpace(EntityManager entityManager, ObjectSpace space, Class<? extends IIntSpaceEntity> entityClass, String idField) throws Exception {
		EntityTransaction tx;
		tx = entityManager.getTransaction();
		tx.begin();
		
		Field field = entityClass.getField(idField);
		field.setAccessible(true);
		
		try {
			for (int x = 0; x < space.getXSize(); x++) {
				for (int y = 0; y < space.getYSize(); y++) {
					final IIntSpaceEntity entity = entityClass.newInstance();
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
			throw e;
		}
		
		tx.commit();
	}
}

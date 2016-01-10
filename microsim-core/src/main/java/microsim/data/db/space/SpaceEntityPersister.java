package microsim.data.db.space;

import java.lang.reflect.Field;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import microsim.engine.SimulationEngine;
import microsim.space.IntSpace;
import microsim.space.ObjectSpace;

public class SpaceEntityPersister {

	public static void persistIntSpace(EntityManager entityManager, IntSpace space, Class<? extends IIntSpaceEntity> entityClass) throws Exception {
		EntityTransaction tx = null;
		tx = entityManager.getTransaction();
		tx.begin();
		
		try {
			for (int x = 0; x < space.getXSize(); x++) {
				for (int y = 0; y < space.getYSize(); y++) {
					final IIntSpaceEntity entity = entityClass.newInstance();
					entity.setSimulationRun(new Long(SimulationEngine.getInstance().getCurrentRunNumber()));
					entity.setSimulationTime(SimulationEngine.getInstance().getTime());
					entity.setX(x);
					entity.setY(y);
					entity.setValue(space.get(x, y));
					entityManager.persist(entity);
				}
			}
		} catch (Exception e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw e;
		}
		
		tx.commit();
	}

	public static void persistObjectSpace(EntityManager entityManager, ObjectSpace space, Class<? extends IIntSpaceEntity> entityClass, String idField) throws Exception {
		EntityTransaction tx = null;
		tx = entityManager.getTransaction();
		tx.begin();
		
		Field field = entityClass.getField(idField);
		field.setAccessible(true);
		
		try {
			for (int x = 0; x < space.getXSize(); x++) {
				for (int y = 0; y < space.getYSize(); y++) {
					final IIntSpaceEntity entity = entityClass.newInstance();
					entity.setSimulationRun(new Long(SimulationEngine.getInstance().getCurrentRunNumber()));
					entity.setSimulationTime(SimulationEngine.getInstance().getTime());
					entity.setX(x);
					entity.setY(y);
					final Long value = field.getLong(space.get(x, y));  
					entity.setValue(value.intValue());
					entityManager.persist(entity);
				}
			}
		} catch (Exception e) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw e;
		}
		
		tx.commit();
	}
}

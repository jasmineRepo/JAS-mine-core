package microsim.data.db;

import java.lang.reflect.Field;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;

import jakarta.persistence.*;

import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.tool.hbm2ddl.SchemaExport;
import org.hibernate.tool.hbm2ddl.SchemaUpdate;
import org.hibernate.tool.schema.TargetType;

import microsim.annotation.GUIparameter;
import microsim.data.MultiKeyCoefficientMap;
import microsim.data.MultiKeyCoefficientMapFactory;
import microsim.engine.SimulationEngine;

public class DatabaseUtils {

	private static final Logger log = Logger.getLogger(DatabaseUtils.class.toString());

	private static EntityManagerFactory emf = null;
	private static EntityManagerFactory outEntityManagerFactory = null;

	public static String databaseInputUrl = null;
	
	public static String databaseOutputUrl = null;
		
	public static Long autoincrementSeed = 1000000L;
	
	public static Experiment createExperiment(EntityManager entityManager,
											  Experiment experiment, Object... models) throws IllegalArgumentException,
			IllegalAccessException {
	
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return experiment;
		
		EntityTransaction tx = entityManager.getTransaction();
		tx.begin();

		experiment.parameters = new ArrayList<>();

		for (Object model : models) {
			Field[] fields = model.getClass().getDeclaredFields();
			for (Field field : fields) {
				GUIparameter modelParamter = field.getAnnotation(GUIparameter.class);
				if (modelParamter != null) {
					field.setAccessible(true);
					ExperimentParameter parameter = new ExperimentParameter();
					parameter.experiment = experiment;
					parameter.name = field.getName();
					Object obj = field.get(model);
					parameter.value = (obj != null ? obj.toString() : "null");

					experiment.parameters.add(parameter);
				}
			}
			
		}

		experiment = entityManager.merge(experiment);
		tx.commit();

		return experiment;
	}
	

	public static void snap(EntityManager em, Long run, Double time, Object target)
			throws Exception {
		
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return;
		
		final Field[] targetFields = target.getClass().getDeclaredFields();
    	Field idField = null;
//    	String idFieldName;
    	for(Field fld : targetFields) {
    		if(fld.getType().equals(PanelEntityKey.class)) {		//Doesn't rely on the name of the field
    			idField = fld;
//    			idFieldName = fld.getName();
    			break;
    		}
    	}
//		final Field idField = target.getClass().getDeclaredField("id");		//Relies on the name of the field being 'id', which may not be true if the user gives it another name.
//		if (idField != null)
//			idField.setAccessible(true);
//
//		if (idField == null || !idField.getType().equals(PanelEntityKey.class))
//			throw new IllegalArgumentException("Object of type " + Object.class
//					+ " cannot be snapped!");
    	if (idField != null)
			idField.setAccessible(true);
		else throw new IllegalArgumentException("Object of type "
				+ target.getClass() + " cannot be exported to database as it does not have a field of type PanelEntityKey.class or it is null!");

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			em.detach(target);
			final PanelEntityKey key = (PanelEntityKey) idField.get(target);
			PanelEntityKey newId = new PanelEntityKey();
			if (key != null)
				newId.setId(key.getId());
			else
				newId.setId(autoincrementSeed++);
			newId.setSimulationTime(SimulationEngine.getInstance().getTime());
			newId.setSimulationRun(SimulationEngine.getInstance().getCurrentExperiment().id);
			idField.set(target, newId);
			em.merge(target);
			idField.set(target, key);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		tx.commit();
	}

	public static void snap(Object target) throws Exception {
		snap(DatabaseUtils.getOutEntityManger(),
				Long.valueOf(SimulationEngine.getInstance().getCurrentRunNumber()),
				SimulationEngine.getInstance().getTime(),
				target);
	}
	
	public static void snap(Collection<?> targetCollection) throws Exception {
		snap(DatabaseUtils.getOutEntityManger(),
				Long.valueOf(SimulationEngine.getInstance().getCurrentRunNumber()),
				SimulationEngine.getInstance().getTime(),
				targetCollection);
	}
	
	public static void snap(EntityManager em, Long run, Double time,
			Collection<?> targetCollection) throws Exception {

		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return;
		
		if (targetCollection != null && targetCollection.size() > 0) {

			EntityTransaction tx = null;

			final Field[] targetFields = targetCollection.iterator().next().getClass().getDeclaredFields();
	    	Field idField = null;
//	    	String idFieldName;
	    	for(Field fld : targetFields) {
	    		if(fld.getType().equals(PanelEntityKey.class)) {		//Doesn't rely on the name of the field
	    			idField = fld;
//	    			idFieldName = fld.getName();
	    			break;
	    		}
	    	}
	    	if (idField != null)
				idField.setAccessible(true);
			else throw new IllegalArgumentException("Object of type "
					+ Object.class + " cannot be exported to database as it does not have a field of type PanelEntityKey.class or it is null!");

//			final Field idField = targetCollection.iterator().next().getClass()
//					.getDeclaredField("id");
//			if (idField != null)
//				idField.setAccessible(true);
//
//			if (idField == null
//					|| !idField.getType().equals(PanelEntityKey.class))
//				throw new IllegalArgumentException("Object of type "
//						+ Object.class + " cannot be snapped!");

			tx = em.getTransaction();
			tx.begin();

			for (Object panelTarget : targetCollection) {
				try {
					em.detach(panelTarget);
					final PanelEntityKey key = (PanelEntityKey) idField.get(panelTarget);
					PanelEntityKey newId = new PanelEntityKey();
					if (key != null)
						newId.setId(key.getId());
					else
						newId.setId(autoincrementSeed++);
					newId.setSimulationTime(SimulationEngine.getInstance().getTime());
					newId.setSimulationRun(SimulationEngine.getInstance().getCurrentExperiment().id);
					idField.set(panelTarget, newId);
					em.merge(panelTarget);
					idField.set(panelTarget, key);
				} catch (Exception e) {
					if (tx != null && tx.isActive())
						tx.rollback();
					throw e;
				}
			}
			tx.commit();
		}
	}

	public static void copy(EntityManager em, Long run, Double time, Object target)
			throws Exception {
		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {
			em.merge(target);
		} catch (Exception e) {
			tx.rollback();
			throw e;
		}

		tx.commit();
	}

	public static void copy(EntityManager em, Long run, Double time,
			Collection<?> targetCollection) throws Exception {
		if (targetCollection != null && targetCollection.size() > 0) {
			EntityTransaction tx = em.getTransaction();
			tx.begin();

			for (Object panelTarget : targetCollection) {
				try {
					em.merge(panelTarget);
				} catch (Exception e) {
					tx.rollback();
					throw e;
				}
			}

			tx.commit();
		}
	}

	public static EntityManager getEntityManger() {
		return getEntityManger(true);
	}
	
	/**
	 * Singleton of hibernate session factory
	 * 
	 * @return The static session factory. If null something went wrong during
	 *         initialization.
	 */
	public static EntityManager getEntityManger(boolean autoUpdate) {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return null;
		
		if (emf == null) {
			try {
				Map<String, String> configOverrides = new HashMap<>();
				if (autoUpdate) 
					configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseInputUrl != null)
					configOverrides.put("hibernate.connection.url", databaseInputUrl);

				emf = Persistence.createEntityManagerFactory("sim-model", configOverrides);

			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Initial EntityManagerFactory creation failed." + ex);
				if (ex instanceof PersistenceException)
					log.log(Level.SEVERE, ex.getCause().toString());
				throw new ExceptionInInitializerError(ex);
			}
		}

		return emf.createEntityManager();
	}

	public static void inputSchemaUpdateEntityManger() {
		if (emf == null) {
			try {
				Map<String, String> configOverrides = new HashMap<>();
				configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseInputUrl != null)
					configOverrides.put("hibernate.connection.url", databaseInputUrl);


				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configOverrides).build();
				MetadataSources metadata = new MetadataSources(serviceRegistry);
				EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
				SchemaExport schemaExport = new SchemaExport();
				schemaExport.create(enumSet, metadata.buildMetadata());

				EntityManager em = Persistence.createEntityManagerFactory("sim-model",
						configOverrides).createEntityManager();

				EntityTransaction tx = em.getTransaction();
				tx.begin();
				em.flush();
				tx.commit();
								
			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Initial EntityManagerFactory creation failed." + ex);
				if (ex instanceof PersistenceException)
					log.log(Level.SEVERE, ex.getCause().toString());
				throw new ExceptionInInitializerError(ex);
			}
		}
		
	}

	public static void safeRollback(EntityTransaction tx) {
		if (tx != null && tx.isActive())
			tx.rollback();
	}

	public static EntityManager getOutEntityManger() {
		return getOutEntityManger("sim-model-out");
	}

	public static EntityManager getOutEntityManger(String persistenceUnitName) {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return null;
		
		if (outEntityManagerFactory == null) {
			try {
				// Create the EntityManagerFactory
				Map<String, String> configOverrides = new HashMap<>();
				configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseOutputUrl != null)
					configOverrides.put("hibernate.connection.url", databaseOutputUrl);

				ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configOverrides).build();
				MetadataSources metadata = new MetadataSources(serviceRegistry)
												.addAnnotatedClass(Experiment.class)
												.addAnnotatedClass(ExperimentParameter.class);
				EnumSet<TargetType> enumSet = EnumSet.of(TargetType.DATABASE);
				SchemaUpdate schemaUpdate = new SchemaUpdate();
				schemaUpdate.execute(enumSet, metadata.buildMetadata());  // TODO check this part

				outEntityManagerFactory = Persistence.createEntityManagerFactory(persistenceUnitName, configOverrides);

			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Initial EntityManagerFactory creation failed." + ex);
				if (ex instanceof PersistenceException)
					log.log(Level.SEVERE, ex.getCause().toString());
				throw new ExceptionInInitializerError(ex);
			}
		}

		return outEntityManagerFactory.createEntityManager();
	}

	public static List<?> loadTable(Class<?> clazz) {
		return loadTable(getEntityManger(), clazz);
	}

	public static List<?> loadTable(EntityManager entityManager, Class<?> clazz) {
		final Query query = entityManager.createQuery("from " + clazz.getSimpleName() + " rec");
		return query.getResultList();
	}

	public static MultiKeyCoefficientMap loadCoefficientMap(Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {
		return loadCoefficientMap(getEntityManger(), clazz);
	}

	public static MultiKeyCoefficientMap loadCoefficientMap(
			EntityManager entityManager, Class<?> clazz)
			throws IllegalArgumentException, SecurityException,
			IllegalAccessException, NoSuchFieldException {

		final EntityTransaction tx = entityManager.getTransaction();
		tx.begin();

		final String hql = "from " + clazz.getSimpleName() + " rec ";

		final Query query = entityManager.createQuery(hql);

		final List<?> res = query.getResultList();

		tx.commit();

		return MultiKeyCoefficientMapFactory.createMapFromAnnotatedList(res);
	}

	
}

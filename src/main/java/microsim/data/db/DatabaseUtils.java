package microsim.data.db;

import jakarta.persistence.*;
import lombok.extern.java.Log;
import lombok.val;
import microsim.annotation.GUIparameter;
import microsim.data.MultiKeyCoefficientMap;
import microsim.data.MultiKeyCoefficientMapFactory;
import microsim.engine.SimulationEngine;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

@Log public class DatabaseUtils {

	private static EntityManagerFactory emf = null;
	private static EntityManagerFactory outEntityManagerFactory = null;

	public static String databaseInputUrl = null;
	
	public static String databaseOutputUrl = null;
		
	public static Long autoincrementSeed = 1000000L;
	
	public static Experiment createExperiment(final EntityManager entityManager, Experiment experiment,
											  final Object... models)
			throws IllegalArgumentException, IllegalAccessException {
	
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return experiment;
		
		val transaction = entityManager.getTransaction();
		transaction.begin();

		experiment.parameters = new ArrayList<>();

		for (Object model : models) {
			for (Field field : model.getClass().getDeclaredFields()) {
				GUIparameter modelParameter = field.getAnnotation(GUIparameter.class);
				if (modelParameter != null) {
					field.setAccessible(true);
					val parameter = new ExperimentParameter();
					parameter.experiment = experiment;
					parameter.name = field.getName();
					val obj = field.get(model);
					parameter.value = (obj != null ? obj.toString() : "null");

					experiment.parameters.add(parameter);
				}
			}
		}
		experiment = entityManager.merge(experiment);
		transaction.commit();

		return experiment;
	}

	public static void snap(EntityManager em, Long run, Double time, Object target) throws Exception {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return;

    	Field idField = null;
    	for(Field fld : target.getClass().getDeclaredFields()) {
    		if(fld.getType().equals(PanelEntityKey.class)) {
    			idField = fld;
    			break;
    		}
    	}
    	if (idField != null) idField.setAccessible(true);
		else {
			String m = "Object of type %s cannot be exported to database as it does not have a field " +
					   "of type PanelEntityKey.class or it is null!";
			throw new IllegalArgumentException(String.format(m, target.getClass()));
		}

		val transaction = em.getTransaction();
		transaction.begin();

		try {
			em.detach(target);
			val key = (PanelEntityKey) idField.get(target);
			PanelEntityKey newId = new PanelEntityKey();
			newId.setId(key != null ? key.getId() : autoincrementSeed++);
			newId.setSimulationTime(SimulationEngine.getInstance().getTime());
			newId.setSimulationRun(SimulationEngine.getInstance().getCurrentExperiment().id);
			idField.set(target, newId);
			em.merge(target);
			idField.set(target, key);
		} catch (Exception e) {
			transaction.rollback();
			throw e;
		}

		transaction.commit();
	}

	public static void snap(Object target) throws Exception {// fixme collection is an object too
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
	
	public static void snap(EntityManager em, Long run, Double time, Collection<?> targetCollection) throws Exception {

		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return;
		
		if (targetCollection != null && targetCollection.size() > 0) {
			EntityTransaction transaction;
	    	Field idField = null;
	    	for(Field fld : targetCollection.iterator().next().getClass().getDeclaredFields()) {
	    		if(fld.getType().equals(PanelEntityKey.class)) {
	    			idField = fld;
	    			break;
	    		}
	    	}
	    	if (idField != null) idField.setAccessible(true);
			else {
				String m = "Object of type %s cannot be exported to database as it does not have a field " +
						"of type PanelEntityKey.class or it is null!";
				throw new IllegalArgumentException(String.format(m, Object.class));
			}

			transaction = em.getTransaction();
			transaction.begin();

			for (Object panelTarget : targetCollection) {
				try {
					em.detach(panelTarget);// fixme duplicate
					val key = (PanelEntityKey) idField.get(panelTarget);
					PanelEntityKey newId = new PanelEntityKey();
					newId.setId(key != null ? key.getId() : autoincrementSeed++);
					newId.setSimulationTime(SimulationEngine.getInstance().getTime());
					newId.setSimulationRun(SimulationEngine.getInstance().getCurrentExperiment().id);
					idField.set(panelTarget, newId);
					em.merge(panelTarget);
					idField.set(panelTarget, key);
				} catch (Exception e) {
					if (transaction.isActive())
						transaction.rollback();
					throw e;
				}
			}
			transaction.commit();
		}
	}

	public static void copy(EntityManager em, Long run, Double time, Object target) throws Exception {
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

	public static void copy(EntityManager em, Long run, Double time, Collection<?> targetCollection) throws Exception {
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
	public static @Nullable EntityManager getEntityManger(boolean autoUpdate) {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return null;
		
		if (emf == null) {
			try {
				val configOverrides = new Properties();
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
				val configOverrides = new Properties();
				configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseInputUrl != null) configOverrides.put("hibernate.connection.url", databaseInputUrl);
				val serviceRegistry = new StandardServiceRegistryBuilder()
																					.applySettings(configOverrides)
																					.build();
				val metadata = new MetadataSources(serviceRegistry);

				val smt = new HibernateSchemaManagementTool();
				smt.injectServices((ServiceRegistryImplementor) serviceRegistry);
				val sc = smt.getSchemaCreator(null);
				val md = metadata.buildMetadata();
				sc.doCreation(md, null, null, null,
						new TargetDescriptor() {
							@Override
							public EnumSet<TargetType> getTargetTypes() {
								return EnumSet.of(TargetType.DATABASE);
							}

							@Override
							public ScriptTargetOutput getScriptTargetOutput() {
								return null;
							}
						});

				val localEmf = Persistence.createEntityManagerFactory("sim-model", configOverrides);
				val em = localEmf.createEntityManager();

				val tx = em.getTransaction();
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

	public static @Nullable EntityManager getOutEntityManger(String persistenceUnitName) {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection())
			return null;
		
		if (outEntityManagerFactory == null) {
			try {
				val configOverrides = new Properties();// fixme duplicating code
				configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseOutputUrl != null) configOverrides.put("hibernate.connection.url", databaseOutputUrl);
				val serviceRegistry = new StandardServiceRegistryBuilder()
																					.applySettings(configOverrides)
																					.build();

				val metadata = new MetadataSources(serviceRegistry)
												.addAnnotatedClass(Experiment.class)
												.addAnnotatedClass(ExperimentParameter.class);

				val smt = new HibernateSchemaManagementTool();// fixme the same code as in inputSchemaUpdateEntityManger as a temporary measure
				smt.injectServices((ServiceRegistryImplementor) serviceRegistry);

				val sc = smt.getSchemaCreator(null);
				val md = metadata.buildMetadata();
				sc.doCreation(md, null, null, null,
						new TargetDescriptor() {
							@Override
							public EnumSet<TargetType> getTargetTypes() {
								return EnumSet.of(TargetType.DATABASE);
							}

							@Override
							public ScriptTargetOutput getScriptTargetOutput() {
								return null;
							}
						});


				//SchemaUpdate schemaUpdate = new SchemaUpdate();
				//schemaUpdate.execute(enumSet, metadata.buildMetadata());

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

	public static List<?> loadTable(@NotNull EntityManager entityManager, @NotNull Class<?> clazz) {
		return entityManager.createQuery("from %s rec ".formatted(clazz.getSimpleName())).getResultList();
	}

	public static @NotNull MultiKeyCoefficientMap loadCoefficientMap(Class<?> clazz)
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {
		return loadCoefficientMap(getEntityManger(), clazz);
	}

	public static @NotNull MultiKeyCoefficientMap loadCoefficientMap(@NotNull EntityManager entityManager,
																	 Class<?> clazz)
			throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException {

		val transaction = entityManager.getTransaction();
		transaction.begin();
		val res = loadTable(entityManager, clazz);
		transaction.commit();

		return MultiKeyCoefficientMapFactory.createMapFromAnnotatedList(res);
	}
}

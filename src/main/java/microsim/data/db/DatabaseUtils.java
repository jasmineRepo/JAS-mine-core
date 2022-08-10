package microsim.data.db;

import jakarta.persistence.*;
import lombok.extern.java.Log;
import lombok.val;
import microsim.annotation.GUIparameter;
import microsim.engine.SimulationEngine;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.spi.ServiceRegistryImplementor;
import org.hibernate.tool.schema.TargetType;
import org.hibernate.tool.schema.internal.HibernateSchemaManagementTool;
import org.hibernate.tool.schema.spi.ScriptTargetOutput;
import org.hibernate.tool.schema.spi.TargetDescriptor;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.*;
import java.util.logging.Level;

import static java.util.Arrays.stream;

@Log public class DatabaseUtils {
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

	public static void snap(EntityManager em, @Nullable Object target) throws Exception {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection()) return;
		var isCollection = false;

		if (target != null) {
			if (target instanceof Collection<?>)
				if (((Collection<?>) target).size() > 0) isCollection = true;
				else return;
		}
		else return;

    	Field idField;
		final Field[] fieldArray;
		if (isCollection) fieldArray = ((Collection<?>) target).iterator().next().getClass().getDeclaredFields();
		else fieldArray = target.getClass().getDeclaredFields();

		idField = stream(fieldArray).filter(fld -> fld.getType().equals(PanelEntityKey.class)).findFirst().orElse(null);

    	if (idField != null) idField.setAccessible(true);
		else {
			String m = "Object of type %s cannot be exported to database as it does not have a field " +
					   "of type PanelEntityKey.class or it is null!";
			throw new IllegalArgumentException(String.format(m, target.getClass()));
		}

		val transaction = em.getTransaction();
		transaction.begin();

		val scratchCollection = isCollection ? ((Collection<?>) target) : new ArrayList<>(Set.of(target));

		for (var panelTarget : scratchCollection) {
			try {
				em.detach(panelTarget);
				val key = (PanelEntityKey) idField.get(panelTarget);
				val newId = new PanelEntityKey();
				newId.setId(key != null ? key.getId() : autoincrementSeed++);
				newId.setSimulationTime(SimulationEngine.getInstance().getTime());
				newId.setSimulationRun(SimulationEngine.getInstance().getCurrentExperiment().id);
				idField.set(panelTarget, newId);
				em.merge(panelTarget);
				idField.set(panelTarget, key);
			} catch (Exception e) {
				if (!isCollection || transaction.isActive())
					transaction.rollback();
				throw e;
			}
		}


		transaction.commit();
	}

	public static void snap(Object target) throws Exception {
		snap(DatabaseUtils.getOutEntityManger(), target);
	}

	public static @Nullable EntityManager getOutEntityManger() {
		if (SimulationEngine.getInstance().isTurnOffDatabaseConnection()) return null;

		if (outEntityManagerFactory == null) {
			try {
				val configOverrides = new Properties();
				configOverrides.put("hibernate.hbm2ddl.auto", "update");
				configOverrides.put("hibernate.archive.autodetection", "class");
				if (databaseOutputUrl != null) configOverrides.put("hibernate.connection.url", databaseOutputUrl);
				val serviceRegistry = new StandardServiceRegistryBuilder()
																					.applySettings(configOverrides)
																					.build();

				val metadata = new MetadataSources(serviceRegistry)
												.addAnnotatedClass(Experiment.class)
												.addAnnotatedClass(ExperimentParameter.class);

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

				outEntityManagerFactory = Persistence.createEntityManagerFactory("sim-model-out", configOverrides);

			} catch (Throwable ex) {
				log.log(Level.SEVERE, "Initial EntityManagerFactory creation failed." + ex);
				if (ex instanceof PersistenceException)
					log.log(Level.SEVERE, ex.getCause().toString());
				throw new ExceptionInInitializerError(ex);
			}
		}

		return outEntityManagerFactory.createEntityManager();
	}
}

package microsim.engine;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.val;
import microsim.data.ExperimentManager;
import microsim.data.ParameterDomain;
import microsim.event.SystemEventType;

import java.util.*;

/**
 * MultiRun is a template abstract class useful to guide the modeller to build an automatic simulation launcher, able
 * to change interactively parameters on the basis of the last run.<br>
 */

public abstract class MultiRun extends Thread implements EngineListener, ExperimentBuilder {

    /**
     * Sets whether to copy the input files into a new input folder within a new output folder for each simulation run
     */
    @Setter
    protected static boolean copyInputFolderStructure = false;
    @Getter
    protected SimulationEngine engine;
    @Getter
    protected int counter;
    protected boolean executionActive, toBeContinued;
    @Setter
    @Getter
    private ExperimentBuilder experimentBuilder;

    @Setter
    @Getter
    private List<MultiRunListener> multiRunListeners;

    @Setter
    @Getter
    private List<EngineListener> engineListeners;

    @Setter
    @Getter
    private String multiRunId;

    @Setter
    @Getter
    private List<ParameterDomain> parameterDomains;

    /**
     * Create a new multi run session.
     */
    public MultiRun() {
        counter = 0;

        parameterDomains = new ArrayList<>();

        executionActive = false;
        toBeContinued = true;

        multiRunListeners = new ArrayList<>();
        engineListeners = new ArrayList<>();
        multiRunId = "Run " + counter;
    }

    /**
     * When a SIMULATION_END signal is sent to JAS by one of the running models,
     * simulation is stopped and this method is called. If it returns true the
     * multi run will continue with the next run, otherwise the program will
     * exit.
     *
     * @return a value deciding if simulation is to be continued.
     */
    public abstract boolean nextModel();

    public abstract String setupRunLabel();

    /**
     * MultiRun is an independent thread. The run method controls the sequence of simulations.
     */
    public synchronized void run() {
        while (toBeContinued) {
            executionActive = true;
            go();
            while (executionActive)
                try {
                    sleep(300);
                } catch (Exception e) {
                    System.out.println("Interrupt: " + e.getMessage());
                }
        }

        System.exit(0);
    }

    /**
     * The go method starts the multi-run simulation.
     */
    public synchronized void go() {
        counter++;

        engine = new SimulationEngine();

        if (counter == 2) {
            //After the first simulation (which by default copies the input files to the output folder),
            // check the settings on whether to copy input files to new folder for each simulation run
            ExperimentManager.getInstance().copyInputFolderStructure = copyInputFolderStructure;
            //By default do not copy the input folder for each simulation run after the first simulation.
        }
        engine.setCurrentRunNumber(counter);
        engine.setMultiRunId(setupRunLabel());
        engine.addEngineListener(this);
        engine.setExperimentBuilder(Objects.requireNonNullElse(experimentBuilder, this));

        for (EngineListener engineListener : engineListeners)
            engine.addEngineListener(engineListener);

        engine.setup();

        if (multiRunListeners != null)
            for (MultiRunListener listener : multiRunListeners)
                listener.beforeSimulationStart(engine);

        engine.startSimulation();
    }

    /**
     * This method monitors the{@code SystemEventType.End} signal. When it is raised the MultiRun class stops current
     * run and invokes the {@link #nextModel()} method.
     *
     * @param event A {@link SystemEventType} event.
     */
    public void onEngineEvent(@NonNull SystemEventType event) {
        if (event.equals(SystemEventType.End)) {
            if (multiRunListeners != null)
                for (MultiRunListener listener : multiRunListeners)
                    listener.afterSimulationCompleted(engine);

            this.yield();
            toBeContinued = nextModel();
            engine.disposeModels();
            executionActive = false;
            engine = null;
        }
    }

    public MultiRun addParameterDomain(final @NonNull ParameterDomain parameterDomain) {
        parameterDomains.add(parameterDomain);
        return this;
    }

    public int getMaxConfigurations() {
        int acc = 1;
        for (var parameterDomain : parameterDomains) {
            int length = parameterDomain.getValues().length;
            acc = acc * length;
        }
        return acc;
    }

    public @NonNull Map<String, Object> getConfiguration(int counter) {
        val current = new HashMap<String, Object>();

        for (int i = 0; i < parameterDomains.size(); i++) {
            val parameterDomain = parameterDomains.get(i);

            int residual = 1;
            int bound = parameterDomains.size();
            for (int j = i + 1; j < bound; j++) {
                int length = parameterDomains.get(j).getValues().length;
                residual = residual * length;
            }

            int idx = counter / residual;
            current.put(parameterDomain.getName(), parameterDomain.getValues()[idx]);
            counter -= residual * idx;
        }

        return current;
    }
}

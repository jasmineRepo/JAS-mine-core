package microsim.integration.demo07.experiment;

import microsim.engine.ExperimentBuilder;
import microsim.engine.SimulationEngine;
import microsim.integration.demo07.model.PersonsModel;

public class StartPersons implements ExperimentBuilder {

    public static void main(String[] args) {
        StartPersons experimentBuilder = new StartPersons();
        final SimulationEngine engine = SimulationEngine.getInstance();
        engine.setExperimentBuilder(experimentBuilder);
        engine.setup();
    }

    @Override
    public void buildExperiment(SimulationEngine engine) {
        PersonsModel model = new PersonsModel();
        PersonsCollector collector = new PersonsCollector(model);
        PersonsObserver observer = new PersonsObserver(model, collector);

        engine.addSimulationManager(model);
        engine.addSimulationManager(collector);
        engine.addSimulationManager(observer);
    }

}

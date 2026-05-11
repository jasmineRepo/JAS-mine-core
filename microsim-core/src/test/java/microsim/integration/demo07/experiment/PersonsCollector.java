package microsim.integration.demo07.experiment;

import microsim.annotation.GUIparameter;
import microsim.data.DataExport;
import microsim.engine.AbstractSimulationCollectorManager;
import microsim.engine.SimulationManager;
import microsim.event.EventListener;
import microsim.event.Order;
import microsim.event.SingleTargetEvent;
import microsim.integration.demo07.model.PersonsModel;

public class PersonsCollector extends AbstractSimulationCollectorManager implements EventListener {

    @GUIparameter(description = "Toggle to persist data to database")
    private Boolean exportToDatabase = false;

    @GUIparameter(description = "Toggle to export data to CSV files")
    private Boolean exportToCSV = true;

    @GUIparameter(description = "number of timesteps to wait before persisting database")
    private Integer recordAfterTimestep = 0; // Allows the user to control when the simulation starts exporting to the
                                             // database, in case they want to delay exporting until after an initial
                                             // 'burn-in' period.

    @GUIparameter(description = "number of timesteps between database dumps")
    private Integer timestepsBetweenRecordings = 1;

    final PersonsModel model = (PersonsModel) getManager();

    DataExport personsData;
    DataExport householdsData;

    public PersonsCollector(SimulationManager manager) {
        super(manager);
    }

    // ---------------------------------------------------------------------
    // Event Listener
    // ---------------------------------------------------------------------

    public enum Processes {
        DumpInfo;
    }

    @Override
    public void onEvent(Enum<?> type) {
        switch ((Processes) type) {

            case DumpInfo:
                personsData.export();
                householdsData.export();
                break;
        }
    }

    // ---------------------------------------------------------------------
    // Manager
    // ---------------------------------------------------------------------

    @Override
    public void buildObjects() {
        personsData = new DataExport(((PersonsModel) getManager()).getPersons(), exportToDatabase, exportToCSV);
        householdsData = new DataExport(((PersonsModel) getManager()).getHouseholds(), exportToDatabase, exportToCSV);

    }

    @Override
    public void buildSchedule() {

        // Schedule periodic dumps of data to database and/or .csv files during the
        // simulation
        getEngine().getEventQueue().scheduleRepeat(new SingleTargetEvent(this, Processes.DumpInfo),
                model.getStartYear() + recordAfterTimestep, Order.AFTER_ALL.getOrdering() - 1,
                timestepsBetweenRecordings);

    }

    // ---------------------------------------------------------------------
    // getters and setters
    // ---------------------------------------------------------------------

    public Integer getRecordAfterTimestep() {
        return recordAfterTimestep;
    }

    public void setRecordAfterTimestep(Integer recordAfterTimestep) {
        this.recordAfterTimestep = recordAfterTimestep;
    }

    public Integer getTimestepsBetweenRecordings() {
        return timestepsBetweenRecordings;
    }

    public void setTimestepsBetweenRecordings(Integer timestepsBetweenRecordings) {
        this.timestepsBetweenRecordings = timestepsBetweenRecordings;
    }

    public Boolean getExportToDatabase() {
        return exportToDatabase;
    }

    public void setExportToDatabase(Boolean exportToDatabase) {
        this.exportToDatabase = exportToDatabase;
    }

    public Boolean getExportToCSV() {
        return exportToCSV;
    }

    public void setExportToCSV(Boolean exportToCSV) {
        this.exportToCSV = exportToCSV;
    }

}

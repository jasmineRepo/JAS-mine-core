package microsim.integration.demo07.experiment;

import microsim.engine.AbstractSimulationObserverManager;
import microsim.engine.SimulationCollectorManager;
import microsim.engine.SimulationManager;
import microsim.event.EventGroup;
import microsim.event.EventListener;
import microsim.event.Order;
import microsim.integration.demo07.model.Person;
import microsim.integration.demo07.model.PersonsModel;
import microsim.statistics.CrossSection;

public class PersonsObserver extends AbstractSimulationObserverManager implements EventListener {

    private Boolean observerOn = true;
    private Integer displayFrequency = 1;

    private CrossSection.Integer ageCS;
    private CrossSection.Integer nonEmploymentCS;
    private CrossSection.Integer employmentCS;
    private CrossSection.Integer retiredCS;
    private CrossSection.Integer inEducationCS;
    private CrossSection.Integer lowEducationCS;
    private CrossSection.Integer midEducationCS;
    private CrossSection.Integer highEducationCS;

    public Integer getDisplayFrequency() {
        return displayFrequency;
    }

    public void setDisplayFrequency(Integer displayFrequency) {
        this.displayFrequency = displayFrequency;
    }

    final PersonsModel model = (PersonsModel) getManager();

    public PersonsObserver(SimulationManager manager, SimulationCollectorManager simulationCollectionManager) {
        super(manager, simulationCollectionManager);
    }

    // ---------------------------------------------------------------------
    // EventListener
    // ---------------------------------------------------------------------

    public enum Processes {
        Update,
    }

    @Override
    public void onEvent(Enum<?> type) {
        switch ((Processes) type) {
            case Update:
                ageCS.updateSource();
                nonEmploymentCS.updateSource();
                employmentCS.updateSource();
                retiredCS.updateSource();
                inEducationCS.updateSource();
                lowEducationCS.updateSource();
                midEducationCS.updateSource();
                highEducationCS.updateSource();
                break;

        }
    }

    // ---------------------------------------------------------------------
    // Manager
    // ---------------------------------------------------------------------

    @Override
    public void buildObjects() {
        if (observerOn) {
            ageCS = new CrossSection.Integer(model.getPersons(), Person.class, "age", false);
            nonEmploymentCS = new CrossSection.Integer(model.getPersons(), Person.class, "getNonEmployed", true);
            employmentCS = new CrossSection.Integer(model.getPersons(), Person.class, "getEmployed", true);
            retiredCS = new CrossSection.Integer(model.getPersons(), Person.class, "getRetired", true);
            inEducationCS = new CrossSection.Integer(model.getPersons(), Person.class, "getStudent", true);
            lowEducationCS = new CrossSection.Integer(model.getPersons(), Person.class, "getLowEducation", true);
            midEducationCS = new CrossSection.Integer(model.getPersons(), Person.class, "getMidEducation", true);
            highEducationCS = new CrossSection.Integer(model.getPersons(), Person.class, "getHighEducation", true);
            // FIXME: output those somehow?
        }
    }

    @Override
    public void buildSchedule() {
        if (observerOn) {
            EventGroup observerSchedule = new EventGroup();

            observerSchedule.addEvent(this, Processes.Update);
            getEngine().getEventQueue().scheduleRepeat(observerSchedule, model.getStartYear(),
                    Order.AFTER_ALL.getOrdering() - 1, displayFrequency);

        }
    }

    public Boolean getObserverOn() {
        return observerOn;
    }

    public void setObserverOn(Boolean observerOn) {
        this.observerOn = observerOn;
    }

}

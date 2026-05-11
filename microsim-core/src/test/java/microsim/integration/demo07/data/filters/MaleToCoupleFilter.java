package microsim.integration.demo07.data.filters;

import org.apache.commons.collections4.Predicate;

import microsim.integration.demo07.model.Person;
import microsim.integration.demo07.model.enums.Gender;

public class MaleToCoupleFilter<T extends Person> implements Predicate<T> {

    @Override
    public boolean evaluate(T agent) {
        return (agent.getGender().equals(Gender.Male) && agent.getToCouple());
    }

}

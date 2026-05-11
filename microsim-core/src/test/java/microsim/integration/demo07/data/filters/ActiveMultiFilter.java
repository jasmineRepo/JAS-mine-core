package microsim.integration.demo07.data.filters;

import org.apache.commons.collections4.Predicate;

import microsim.integration.demo07.model.Person;
import microsim.integration.demo07.model.enums.Gender;

public class ActiveMultiFilter<T extends Person> implements Predicate<T> {

    private int ageFrom;
    private int ageTo;
    private Gender gender;

    public ActiveMultiFilter(int ageFrom, int ageTo, Gender gender) {
        super();
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;
        this.gender = gender;
    }

    @Override
    public boolean evaluate(T agent) {

        return (agent.atRiskOfWork() &&
                agent.getGender().equals(gender) &&
                agent.getAge() >= ageFrom &&
                agent.getAge() <= ageTo);
    }

}

package microsim.integration.demo07.data.filters;

import org.apache.commons.collections4.Predicate;

import microsim.integration.demo07.model.Person;
import microsim.integration.demo07.model.enums.CivilState;
import microsim.integration.demo07.model.enums.Gender;

public class FemaleToDivorce<T extends Person> implements Predicate<T> {

    private int ageFrom;
    private int ageTo;

    public FemaleToDivorce(int ageFrom, int ageTo) {
        super();
        this.ageFrom = ageFrom;
        this.ageTo = ageTo;
    }

    @Override
    public boolean evaluate(T agent) {

        return (agent.getGender().equals(Gender.Female) &&
                agent.getCivilState().equals(CivilState.Married) &&
                agent.getDurationInCouple() > 0 &&
                agent.getAge() >= ageFrom &&
                agent.getAge() <= ageTo);
    }

}

package microsim.integration.demo07.model;

import microsim.data.MultiKeyCoefficientMap;
import microsim.data.db.PanelEntityKey;
import microsim.engine.SimulationEngine;
import microsim.event.EventListener;
import microsim.integration.demo07.algorithms.MapAgeSearch;
import microsim.integration.demo07.data.Parameters;
import microsim.integration.demo07.model.enums.CivilState;
import microsim.integration.demo07.model.enums.Education;
import microsim.integration.demo07.model.enums.Gender;
import microsim.integration.demo07.model.enums.WorkState;
import microsim.statistics.IDoubleSource;
import microsim.statistics.IObjectSource;
import microsim.statistics.regression.RegressionUtils;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;

@Entity
public class Person implements Comparable<Person>, EventListener, IDoubleSource, IObjectSource {
    @Transient
    public static long personIdCounter = 100000;

    @Transient
    private PersonsModel model;

    @Id
    private PanelEntityKey key;

    private int age;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    @Column(name = "workstate")
    @Enumerated(EnumType.STRING)
    private WorkState workState;

    @Enumerated(EnumType.STRING)
    private CivilState civilState;

    @Column(name = "dur_in_couple")
    private Integer durationInCouple;

    @Column(name = "mother_id")
    private Long motherId;

    @Transient
    private Person mother;

    @Column(name = "partner_id")
    private Long partnerId;

    @Transient
    private Person partner;

    @Column(name = "hh_id")
    private long householdId;

    @Transient
    private Household household;

    @Column(name = "alone")
    private Boolean alone;

    @Enumerated(EnumType.STRING)
    private Education educationlevel;

    @Transient
    private boolean toCouple;

    @Transient
    private boolean toDivorce;

    @Transient
    private double divorceProb;

    @Transient
    private double workProb;

    @Transient
    private Person potentialPartner;

    // ---------------------------------------------------------------------
    // Constructor
    // ---------------------------------------------------------------------

    // Used when loading the initial population from the input database
    public Person() {
        super();
        model = (PersonsModel) SimulationEngine.getInstance().getManager(PersonsModel.class.getCanonicalName());
    }

    public Person(long idNumber) {
        this();

        key = new PanelEntityKey(idNumber);

    }

    // Used when creating new people during the birth process
    public Person(Person mother) {
        this();

        key = new PanelEntityKey((Person.personIdCounter)++);
        setAge(0);
        setMother(mother);
        setHousehold(mother.getHousehold());
        setGender(RegressionUtils.event(Gender.class, new double[] { 0.49, 0.51 })); // 0.49 for females, 0.51 for
                                                                                     // males. As I swapped enum
                                                                                     // definition of gender around to
                                                                                     // be consistent with input data,
                                                                                     // this also needs to be swapped.
        setEducationlevel(RegressionUtils.event(Education.class, new double[] { 0.25, 0.39, 0.36 })); // RMK: education
                                                                                                      // is
                                                                                                      // predetermined
                                                                                                      // at birth
        setCivilState(CivilState.Single);
        setWorkState(WorkState.Student);

        // Add person to the model's list of persons
        model.getPersons().add(this);

    }

    // ---------------------------------------------------------------------
    // Event Listener
    // ---------------------------------------------------------------------

    public enum Processes {
        Ageing,
        Death,
        Birth,
        ToCouple,
        Divorce,
        GetALife,
        InEducation;
    }

    @Override
    public void onEvent(Enum<?> type) {
        switch ((Processes) type) {
            case Ageing:
                // System.out.println("Ageing");
                ageing();
                break;
            case Death:
                // System.out.println("Death");
                death();
                break;
            case Birth:
                // System.out.println("Birth");
                birth();
                break;
            case ToCouple:
                // System.out.println("ToCouple");
                toCouple();
                break;
            case Divorce:
                // System.out.println("Divorce");
                divorce();
                break;
            case GetALife:
                // System.out.println("GetALife");
                getALife();
                break;
            case InEducation:
                // System.out.println("InEducation");
                inEducation();
                break;
        }
    }

    // ---------------------------------------------------------------------
    // implements IObjectSource for use with Regression classes
    // ---------------------------------------------------------------------

    public enum RegressionKeys {
        gender,
        workState,
    }

    public Object getObjectValue(Enum<?> variableID) {
        switch ((RegressionKeys) variableID) {

            // For marriage regression
            case gender:
                return gender;
            case workState:
                return workState;
            default:
                throw new IllegalArgumentException(
                        "Unsupported regressor " + variableID.name() + " in Person#getObjectValue");
        }
    }

    // ---------------------------------------------------------------------
    // implements IDoubleSource for use with Regression classes
    // ---------------------------------------------------------------------

    public enum Regressors {
        // For marriage regression, check with potential partner's properties
        potentialPartnerAge, potentialPartnerAgeSq, potentialPartnerAgeCub,
        potentialAgeDiff, potentialAgeDiffSq, potentialAgeDiffCub,
        inWorkAndPotentialPartnerInWork, notInWorkAndPotentialPartnerInWork, inWorkAndPotentialPartnerNotInWork,
        // For divorce regression
        nChildren,
        durationInCouple, // Is it necessary to include this here, as it is already an attribute of
                          // person? Should we include for consistency?
        ageDiff, ageDiffSq, ageDiffCub,
        bothWork,
        divorceIntercept,
        // For in work regression
        age, ageSq, ageCub,
        isMarried,
        workIntercept;
    }

    public double getDoubleValue(Enum<?> variableID) {

        switch ((Regressors) variableID) {

            // For marriage regression
            case potentialPartnerAge:
                return getPotentialPartnerAge();
            case potentialPartnerAgeSq:
                return getPotentialPartnerAge() * getPotentialPartnerAge();
            case potentialPartnerAgeCub:
                return getPotentialPartnerAge() * getPotentialPartnerAge() * getPotentialPartnerAge();
            case potentialAgeDiff:
                return getPotentialAgeDiff();
            case potentialAgeDiffSq:
                return getPotentialAgeDiff() * getPotentialAgeDiff();
            case potentialAgeDiffCub:
                return getPotentialAgeDiff() * getPotentialAgeDiff() * getPotentialAgeDiff();
            case inWorkAndPotentialPartnerInWork:
                return getInWorkAndPotentialPartnerInWork();
            case notInWorkAndPotentialPartnerInWork:
                return getNotInWorkAndPotentialPartnerInWork();
            case inWorkAndPotentialPartnerNotInWork:
                return getInWorkAndPotentialPartnerNotInWork();

            // For divorce regression
            case nChildren:
                return getNbChildren();
            case durationInCouple:
                return (double) getDurationInCouple();
            case ageDiff:
                return getAgeDiff();
            case ageDiffSq:
                return getAgeDiff() * getAgeDiff();
            case ageDiffCub:
                return getAgeDiff() * getAgeDiff() * getAgeDiff();
            case bothWork:
                return getBothWork();
            case divorceIntercept:
                return 1.; // Is the constant intercept, so regression coefficient is multiplied by 1

            // For work regression
            case age:
                return (double) age;
            case ageSq:
                return (double) age * age;
            case ageCub:
                return (double) age * age * age;
            case isMarried:
                return civilState.equals(CivilState.Married) ? 1. : 0.;
            case workIntercept:
                return 1.; // Is the constant intercept, so regression coefficient is multiplied by 1

            default:
                throw new IllegalArgumentException(
                        "Unsupported regressor " + variableID.name() + " in Person#getDoubleValue");
        }
    }

    // ---------------------------------------------------------------------
    // own methods
    // ---------------------------------------------------------------------

    protected void ageing() {

        age++;

        // Retire person if age equals retirement age (or older for initial population)
        if (!this.getWorkState().equals(WorkState.Retired)) {
            if ((this.getGender().equals(Gender.Male) && (this.getAge() >= 65))
                    || (this.getGender().equals(Gender.Female) && (this.getAge() >= model.getWemra()))) {
                setWorkState(WorkState.Retired);
            }
        }

        if (civilState.equals(CivilState.Married)) {
            setDurationInCouple(getDurationInCouple() + 1);
        }

    }

    protected void death() {

        MultiKeyCoefficientMap map = (gender.equals(Gender.Male) ? Parameters.getpDeathM() : Parameters.getpDeathF());

        double deathProbability = ((Number) map.getValue(this.age, (int) SimulationEngine.getInstance().getTime()))
                .doubleValue();

        if (RegressionUtils.event(deathProbability)) {
            // update partner's status
            if (this.getCivilState().equals(CivilState.Married)) {
                partner.setCivilState(CivilState.Widow);
                partner.setPartner(null);
            }
            // remove person from household (this removes household from model if it no
            // longer has any residents)
            household.removePerson(this);
            household = null;

            mother = null;
            // remove from model
            model.removePerson(this);
        }

    }

    // Setup own household if aged 24 or over and not married (and still living with
    // others)
    protected void getALife() {

        setAlone(household.getHouseholdMembers().size() == 1);

        if (!(civilState.equals(CivilState.Married)) && !(alone) && (age >= 24)) {

            resetHousehold(new Household((Household.householdIdCounter)++));
            model.getHouseholds().add(household);

        }

    }

    protected void birth() {
        if ((this.gender.equals(Gender.Female)) && (this.age >= 15) && (this.age <= 50)) {
            double birthProbability;
            try {
                birthProbability = ((Number) Parameters.getpBirth().getValue(this.age,
                        (int) SimulationEngine.getInstance().getTime())).doubleValue();
                if (RegressionUtils.event(birthProbability)) {

                    @SuppressWarnings("unused")
                    Person newborn = new Person(this);

                }
            } catch (Exception e) {
                System.err.println("birth exception " + this.age); // FIXME: should error out?
            }

        }

    }

    // Rmk: this method is applied to both males and females as both have to "break
    // free"
    protected void divorce() {

        if (getPartnerId() != null) {
            if (this.getToDivorce() || partner.getToDivorce()) {

                // # break link to partner
                setPartnerId(null);
                setCivilState(CivilState.Divorced);
                setDurationInCouple(0);

                // # move out males (females retain their own household id)
                if (getGender().equals(Gender.Male) && this.getToDivorce()) {

                    // create new household
                    resetHousehold(new Household((Household.householdIdCounter)++));
                    model.getHouseholds().add(household);
                }
            }
        }
    }

    protected void toCouple() {

        toCouple = false;
        if (age >= 18 && age <= 90 && !civilState.equals(CivilState.Married)) {

            MultiKeyCoefficientMap map = Parameters.getpMarriage();
            double coupleProbability = MapAgeSearch.getValue(map, getAge(), getGender(), getCivilState().toString());
            toCouple = (RegressionUtils.event(coupleProbability));
        }

    }

    protected void inEducation() {
        // unemployed if left education
        if (workState != null)
            if (workState.equals(WorkState.Student)) {
                if ((age >= 16 && educationlevel.equals(Education.LowerSecondary)) ||
                        (age >= 19 && educationlevel.equals(Education.UpperSecondary)) ||
                        (age >= 24 && educationlevel.equals(Education.Tertiary))) {
                    setWorkState(WorkState.NotEmployed);
                }
            }
    }

    public double getMarriageScore(Person potentialPartner) {

        this.setPotentialPartner(potentialPartner); // Set Person#potentialPartner field, to calculate regression score
                                                    // for potential match between this person and potential partner.
        double marriageScore = Parameters.getRegMarriageFit().getScore(this, Person.Regressors.class);

        return marriageScore;
    }

    public void marry(Person partner) {

        this.partner = partner;
        setPartnerId(partner.getKey().getId());

        if (gender.equals(Gender.Female)) {
            resetHousehold(new Household((Household.householdIdCounter)++));
            model.getHouseholds().add(household);
        } else {
            resetHousehold(partner.getHousehold());
        }

        setCivilState(CivilState.Married);
        setDurationInCouple(0);
    }

    // this method returns a double in order to allow invocation by the model in the
    // alignment closure
    public double computeDivorceProb() {

        double divorceProb = Parameters.getRegDivorce().getProbability(this, Person.Regressors.class);
        if (divorceProb < 0 || divorceProb > 1) {
            System.err.println("divorce prob. not in range [0,1]");
        }

        return divorceProb;
    }

    public boolean atRiskOfWork() {
        return (age > 15 && age < (gender.equals(Gender.Male) ? 65 : model.getWemra())
                && !workState.equals(WorkState.Student)
                && !workState.equals(WorkState.Retired));
    }

    // this method returns a double in order to allow invocation by the model in the
    // alignment closure
    protected double computeWorkProb() {

        double workProb = -1;

        if (atRiskOfWork()) {
            workProb = Parameters.getRegInWork().getProbability(this, Person.Regressors.class);
        }
        if (workProb < 0 || workProb > 1) {
            System.err.println("work prob. not in range [0,1]");
        }
        return workProb;

    }

    // ---------------------------------------------------------------------
    // Comparator
    // ---------------------------------------------------------------------
    // Person comparators are defined "on the fly" with closures in
    // Model.marriageMatching(),
    // but a compareTo method has to be defined as the class implements the
    // Comparable interface.

    @Override
    public int compareTo(Person p) {

        return -1;
    }

    // ---------------------------------------------------------------------
    // Access methods
    // ---------------------------------------------------------------------

    public PanelEntityKey getKey() {
        return key;
    }

    public void setKey(PanelEntityKey key) {
        this.key = key;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public WorkState getWorkState() {
        return workState;
    }

    public int getEmployed() {
        if (workState.equals(WorkState.Employed))
            return 1;
        else
            return 0;
    }

    public int getNonEmployed() {
        if (workState.equals(WorkState.NotEmployed))
            return 1;
        else
            return 0;
    }

    public int getRetired() {
        if (workState.equals(WorkState.Retired))
            return 1;
        else
            return 0;
    }

    public int getStudent() {
        if (workState.equals(WorkState.Student))
            return 1;
        else
            return 0;
    }

    public void setWorkState(WorkState workState) {
        this.workState = workState;
    }

    public CivilState getCivilState() {
        return civilState;
    }

    public void setCivilState(CivilState civilState) {
        this.civilState = civilState;
    }

    public Integer getDurationInCouple() {
        return (durationInCouple != null ? durationInCouple : 0);
    }

    public void setDurationInCouple(Integer durationInCouple) {
        this.durationInCouple = durationInCouple;
    }

    public Long getMotherId() {
        return motherId;
    }

    public void setMotherId(Long motherId) {
        this.motherId = motherId;
    }

    public Long getPartnerId() {
        return partnerId;
    }

    public void setPartnerId(Long partnerId) {
        this.partnerId = partnerId;
    }

    public long getHouseholdId() {
        return householdId;
    }

    public void setHouseholdId(long householdId) {
        this.householdId = householdId;
    }

    public Education getEducationlevel() {
        return educationlevel;
    }

    public int getLowEducation() {
        if (educationlevel.equals(Education.LowerSecondary))
            return 1;
        else
            return 0;
    }

    public int getMidEducation() {
        if (educationlevel.equals(Education.UpperSecondary))
            return 1;
        else
            return 0;
    }

    public int getHighEducation() {
        if (educationlevel.equals(Education.Tertiary))
            return 1;
        else
            return 0;
    }

    public void setEducationlevel(Education educationlevel) {
        this.educationlevel = educationlevel;
    }

    public Boolean getAlone() {
        return (alone != null ? alone : false);
    }

    public void setAlone(Boolean alone) {
        this.alone = alone;
    }

    public boolean getToDivorce() {
        return toDivorce;
    }

    public void setToDivorce(boolean toDivorce) {
        this.toDivorce = toDivorce;
    }

    public boolean getToCouple() {
        return toCouple;
    }

    public void setToCouple(boolean toCouple) {
        this.toCouple = toCouple;
    }

    public double getWorkProb() {
        return workProb;
    }

    public double getDivorceProb() {
        return divorceProb;
    }

    public void setDivorceProb(double p) {
        divorceProb = p;
    }

    private double getBothWork() {
        return (getWorkState().equals(WorkState.Employed) && partner.getWorkState().equals(WorkState.Employed) ? 1.0
                : 0.0);
    }

    private double getNotInWorkAndPotentialPartnerInWork() {
        return (!getWorkState().equals(WorkState.Employed) && potentialPartner.getWorkState().equals(WorkState.Employed)
                ? 1.0
                : 0.0);
    }

    private double getInWorkAndPotentialPartnerNotInWork() {
        return (getWorkState().equals(WorkState.Employed) && !potentialPartner.getWorkState().equals(WorkState.Employed)
                ? 1.0
                : 0.0);
    }

    private double getInWorkAndPotentialPartnerInWork() {
        return (getWorkState().equals(WorkState.Employed) && potentialPartner.getWorkState().equals(WorkState.Employed)
                ? 1.0
                : 0.0);
    }

    public double getAgeDiff() {
        return (double) (age - partner.getAge());
    }

    public double getNbChildren() {
        if (this.gender == Gender.Female) {
            return (double) household.getNbChildren();
        } else
            return 0.;
    }

    public Person getMother() {
        return mother;
    }

    public void setMother(Person mother) {
        this.setMotherId(mother.getKey().getId());
        this.mother = mother;
    }

    public Person getPartner() {
        return partner;
    }

    public void setPartner(Person partner) {
        if (partner != null) {
            this.setPartnerId(partner.getKey().getId());
        } else {
            partnerId = null;
        }
        this.partner = partner;
    }

    public Household getHousehold() {
        return household;
    }

    public void setHousehold(Household household) {
        this.household = household;
        household.addPerson(this);
        this.householdId = household.getKey().getId();
    }

    public void resetHousehold(Household household) {
        this.household.removePerson(this);
        this.household = household;
        household.addPerson(this);
        this.householdId = household.getKey().getId();
    }

    public Person getPotentialPartner() {
        return potentialPartner;
    }

    public void setPotentialPartner(Person potentialPartner) {
        this.potentialPartner = potentialPartner;
    }

    public double getPartnerAge() {
        return partner.getAge();
    }

    public double getPotentialPartnerAge() {
        return potentialPartner.getAge();
    }

    public double getPotentialAgeDiff() {
        return (double) (age - potentialPartner.getAge());
    }

}

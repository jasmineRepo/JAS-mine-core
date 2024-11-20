package microsim.statistics.regression;

public enum RegressionType implements IntegerValuedEnum {

    Logit(0),
    Probit(1),
    OrderedLogit(0),
    OrderedProbit(1),
    GenOrderedLogit(0),
    GenOrderedProbit(1),
    MultinomialLogit(0);

    // set value to 0 for logit and 1 for probit
    private final int value;
    RegressionType(int val) {value=val;}

    @Override
    public int getValue() {return value;}
}

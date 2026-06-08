package microsim.matching;

public interface MatchingScoreClosure<T> {

	public Double getValue(T item1, T item2);
	
}

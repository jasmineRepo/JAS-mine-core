package microsim;

/// Represent a computation with an interesting side effect.
///
/// This is very similar to [Runnable], but the latter is historically often
/// associated with operations meant to be ran in a separate [Thread] for
/// concurrency purposes. To avoid confusion with this association, the
/// [SideEffect] interface is meant to represent what's essentially a
/// `Function<Void, Void>` whose side effect is interesting.
@FunctionalInterface
public interface SideEffect {
    /// Call the side effect.
    void call();
}

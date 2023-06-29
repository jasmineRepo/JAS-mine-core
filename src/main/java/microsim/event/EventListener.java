package microsim.event;


import lombok.NonNull;

public interface EventListener {

    void onEvent(final @NonNull Enum<?> type);
}

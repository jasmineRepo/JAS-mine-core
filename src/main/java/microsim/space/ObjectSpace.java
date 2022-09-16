package microsim.space;

import lombok.NonNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectSpace {

    int getXSize();

    int getYSize();

    int boundX(final int x);

    int boundY(final int y);

    int reflectX(final int x);

    int reflectY(final int y);

    int torusX(final int x);

    int torusY(final int y);

    int countObjectsAt(final int x, final int y);

    boolean addGridPosition(final @NonNull SpacePosition position);

    boolean removeGridPosition(final @NonNull SpacePosition position);

    boolean moveGridPosition(final @NonNull SpacePosition object, final int destinationX, final int destinationY);

    @Nullable Object get(final int x, final int y);

    void set(final int x, final int y, final @NonNull Object obj);
}

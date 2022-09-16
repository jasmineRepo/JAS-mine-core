package microsim.space;

import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.NonNull;
import org.jetbrains.annotations.Contract;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass
public class SpacePosition implements Serializable, Comparable<SpacePosition> {

    @Serial
    private static final long serialVersionUID = -6916883339210473046L;

    @Getter
    protected int x;
    @Getter
    protected int y;

    public SpacePosition() {
        this(0, 0);
    }

    public SpacePosition(final int x, final int y) {
        this.x = x;
        this.y = y;
    }

    @Contract(pure = true)
    public SpacePosition(final @NonNull SpacePosition p) {
        this.x = p.x;
        this.y = p.y;
    }

    public boolean equals(final @NonNull Object obj) {
        return obj instanceof SpacePosition pt ? (x == pt.x) && (y == pt.y) : super.equals(obj);
    }

    public @NonNull String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + "]";
    }

    public int compareTo(final @NonNull SpacePosition toPos) {
        return x == toPos.x && y == toPos.y ? 0 : x < toPos.x || y < toPos.y ? -1 : 1;
    }
}

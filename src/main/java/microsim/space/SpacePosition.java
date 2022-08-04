package microsim.space;

import jakarta.persistence.MappedSuperclass;

import java.io.Serial;
import java.io.Serializable;

@MappedSuperclass public class SpacePosition implements Serializable, Comparable<SpacePosition> {// replace with record?// see lombok @data

	@Serial private static final long serialVersionUID = 1L;

	protected int x;
	protected int y;
	
	public SpacePosition() {
		this (0, 0);
	}

	public SpacePosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public SpacePosition(SpacePosition p) {
		this.x = p.x;
		this.y = p.y;
	}
	
	public int getX() {
        return x;
    }
	
	public int getY() {
        return y;
    }
	
	public boolean equals(Object obj) {
        if (obj instanceof  SpacePosition) {
        	final SpacePosition pt = (SpacePosition) obj;
            return (x == pt.x) && (y == pt.y);
        }
        return super.equals(obj);
    }
	
	public String toString() {
        return getClass().getName() + "[x=" + x + ",y=" + y + "]";
    }

	public int compareTo(SpacePosition toPos) {
		if (x == toPos.x && y == toPos.y)
			return 0;
		else if (x < toPos.x || y < toPos.y)
			return -1;
		else
			return 1;
	}
}

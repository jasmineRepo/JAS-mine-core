package microsim.engine;

import microsim.event.SystemEventType;

public interface EngineListener {

	public void onEngineEvent(SystemEventType event);
	
}

package microsim.engine;

import microsim.event.SystemEventType;

public interface EngineListener {

	void onEngineEvent(SystemEventType event);
	
}

package microsim.event;

import microsim.engine.SimulationEngine;
import microsim.exception.SimulationException;


/**
 * System events are directly processed by the simulation engine.
 * There are some special events that engine is able to understand.
 * For instance, you can schedule the end of simulation using a system event.
 * <br><i> SystemEvent e = new SystemEvent(Sim.EVENT_SIMULATION_END);</i><br>
 * eventQueue.schedule(100, e);
 * <i>
 * The above code make the engine stop at 100. When this event happens
 * the simulationEnd() method of the current running models is called.
 *
 * <p>Title: JAS</p>
 * <p>Description: Java Agent-based Simulation library</p>
 * <p>Copyright (C) 2002 Michele Sonnessa</p>
 *
 * This library is free software; you can redistribute it and/or modify it under the terms
 * of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * library; if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307, USA.
 *
 * @author Michele Sonnessa
 * <p>
 */
public class SystemEvent extends Event
{

  SystemEventType type;
  SimulationEngine engine;

  public SystemEvent(SimulationEngine engine, SystemEventType type)
  {
    this.type = type;
    this.engine = engine;
  }

  public void fireEvent() throws SimulationException
  {
	  switch (type) {
	  case Start:
		  engine.startSimulation();
		  break;
	  case Restart:
		  engine.rebuildModels();
		  break;
	  case Stop:
		  engine.pause();
		  break;
	  case Shutdown:
		  engine.quit();
		  break;
	  case Build:
		  engine.buildModels();
		  break;
	  case Step:
		  engine.step();
		  break;
	  case End:
		  engine.end();
		  break;
	  case Setup:
		  engine.setup();
		  break;
	  }
  }

  /** Return a string describing event.*/
  public String toString()
  {
    String s = "SystemEvent(@" + getTime() + " " + type;

    return  s;
  }

}

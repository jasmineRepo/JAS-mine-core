package microsim.engine;

import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

public class SimulationTimeManager {

	@Setter	@Getter private int dayTickUnit;
	private final Calendar calendar;
	private final Date initOfTime;
	
	public SimulationTimeManager(Date initOfTime, int dayTickUnit) {
		this.dayTickUnit = dayTickUnit;
		this.initOfTime = initOfTime;
		this.calendar = Calendar.getInstance();
	}

	public SimulationTimeManager(int year, int month, int day, int dayTickUnit) {
		this.dayTickUnit = dayTickUnit;
		this.calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);
		this.initOfTime = calendar.getTime();		
	}
	
	public Date getRealDate(long simulatedTime) {
		calendar.setTime(initOfTime);
		final int days = (int) ((double) simulatedTime / (double) dayTickUnit);
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTime();
	}
}

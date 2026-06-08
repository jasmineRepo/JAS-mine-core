package microsim.engine;

import java.util.Calendar;
import java.util.Date;

public class SimulationTimeManager {

	private int dayTickUnit;
	private Calendar calendar;
	private Date initOfTime;
	
	public SimulationTimeManager(Date initOfTime, int dayTickUnit) {
		super();
		this.dayTickUnit = dayTickUnit;
		this.initOfTime = initOfTime;
		this.calendar = Calendar.getInstance();
	}

	public SimulationTimeManager(int year, int month, int day, int dayTickUnit) {
		super();
		this.dayTickUnit = dayTickUnit;
		this.calendar = Calendar.getInstance();
		calendar.set(year, month - 1, day);
		this.initOfTime = calendar.getTime();		
	}
	
	public int getDayTickUnit() {
		return dayTickUnit;
	}

	public void setDayTickUnit(int dayTickUnit) {
		this.dayTickUnit = dayTickUnit;
	}
	
	public Date getRealDate(long simulatedTime) {
		calendar.setTime(initOfTime);
		final int days = (int) ((double) simulatedTime / (double) dayTickUnit);
		calendar.add(Calendar.DATE, days);
		
		return calendar.getTime();
	}
}

package test.datagathering;

import java.util.Timer;
import java.util.TimerTask;

public class DataGatheringManager implements TestReset{

	private static long TIME_OUT_LONG = 240000L;
	private static long TIME_OUT_SHORT = 120000L;
	
	private Timer clockTimer;
	private TestTimer clock;
	private TestRunner thread;
	
	private volatile long timeOutPeriod;
	
	public static volatile boolean check;
	
	public void runTest() throws Exception{
		clockTimer = new Timer();
		setTimeOutShort();
		Timer timer = new Timer();
		DataGathering data = null;
		while(data == null || !data.getFinished()) {
			data = new DataGathering(this);
			thread = new TestRunner(data);
			resetClock();
			timer.schedule(thread, 0);
			while(!check) {
				
			}
			if(!data.getFinished()) {
				thread.cancel();
				
				data.markTestUnfinished();
				System.gc();
				Runtime.getRuntime().gc();
			}
		}
	}
	
	public void resetTests() {
		check = false;
	}
	
	public void resetClock() {
		if(clock != null)
			clock.cancel();
		clock = new TestTimer();
		clockTimer.schedule(clock,  timeOutPeriod);
	}
	
	public void setTimeOutShort() {
		timeOutPeriod = TIME_OUT_SHORT;
	}
	
	public void setTimeOutLong() {
		timeOutPeriod = TIME_OUT_LONG;
	}
	
	class TestRunner extends TimerTask{

		private DataGathering data;
		
		public TestRunner(DataGathering in) {
			data = in;
		}
		
		@Override
		public void run() {
			try {
				data.allInOneRunTests();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		
	}
	
	class TestTimer extends TimerTask {

		public TestTimer() {
			super();
			check = false;
		}
		
		@Override
		public void run() {
			check = true;
			System.out.println("Time Out");
		}
		
		
		
	}
	
}

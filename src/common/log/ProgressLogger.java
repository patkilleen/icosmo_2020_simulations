package common.log;

public  class ProgressLogger{
	private int desiredLoggingInterval;
	private int targetProgress;
	private int progressTicks;
	
	private Logger log;
	
	private long lastTimeTick;
	
	private long sumProgressTimeTicks;
	
	public ProgressLogger(int desiredLoggingInterval, int targetProgress){
		
		this.targetProgress=targetProgress;
		progressTicks=0;
		log = LoggerFactory.getInstance();
		
		//make sure logging intervale at least 1
		if(desiredLoggingInterval<1){
			this.desiredLoggingInterval = 1;
		}else{
			this.desiredLoggingInterval=desiredLoggingInterval;
		}
	}
	
	public void logProgress(String msg){
		
		if(progressTicks == 0){
			lastTimeTick = System.currentTimeMillis();
		}
		progressTicks++;
		
		if((progressTicks % desiredLoggingInterval) == 0){

			int percentage = (int)(((double)progressTicks/ (double)targetProgress)*100);
			
			long currentTime =System.currentTimeMillis();
			//time since last progress tick
			long progressTimeTick =  currentTime - lastTimeTick;
			sumProgressTimeTicks += progressTimeTick;
			
			//avgProgressTimeTicks = avgProgressTimeTicks / (double)intervalTicks;
			lastTimeTick = currentTime;
			
			double timePerPercent = (double)sumProgressTimeTicks / (double)percentage;
			long percentRemaining = (100 - percentage ); 
			
			//time remaining
			long msRemaining =  (long)(percentRemaining * timePerPercent);
			long secondsRemaining = Math.floorDiv(msRemaining,1000);//conver to seconds
			long minutesRemaining = Math.floorDiv(secondsRemaining,60);//conver to minutes
			
			String remainingTimeStr =  minutesRemaining+"m:"+(secondsRemaining%60)+"s";
			
			long msEllapses = sumProgressTimeTicks;
			long secondsEllapsed = Math.floorDiv(msEllapses,1000);//conver to seconds
			long minutesEllapsed = Math.floorDiv(secondsEllapsed,60);//conver to minutes
			
			String ellapsedTimeStr =  minutesEllapsed+"m:"+(secondsEllapsed%60)+"s";
			//time remaininng
			log.log_debug(msg+". ["+percentage+"% complete], time ellapsed: ("+ellapsedTimeStr+"), estimated time remaining: ("+remainingTimeStr+")");
		}
	}
}
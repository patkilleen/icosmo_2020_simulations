package core;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import common.Noise;
import common.event.PhaseBeginEvent;
import common.event.PhaseCompleteEvent;
import common.event.TimeStampedPhaseCompleteEvent;
import common.event.TimerEvent;
import common.log.Logger;
import common.log.LoggerFactory;
import common.log.ProgressLogger;
import phase.configuration.input.SimulationLoader;

public class Main {

	public Main() {
		// TODO Auto-generated constructor stub
	}

	public static void runPhase1(GlobalTimer timer) throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		log.log_info("Starting fault and data generation phase...");


		//start 1st phase
		timer.phaseStarted(new PhaseBeginEvent());

		int x = timer.getMaxTimeTicks();

		log.log_info("Running the simulation for ("+x+") time ticks(days).");

		//log 5% intervals of completion
		ProgressLogger	progressLogger = new ProgressLogger((int)Math.floor(0.01 * (double)x),x);
		int time =0;
		//run simulation for X number of days
		for( time = 0; time < x;time++){
			TimerEvent e = new TimerEvent(time);
			//log.log_debug("time: "+time);
			timer.messageArrived(e);
			progressLogger.logProgress("phase progress: ");
		}

		//end 1st phase

		timer.phaseEnded(new TimeStampedPhaseCompleteEvent(time));

	}

	public static void runPhase2(GlobalTimer timer) throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		
		//start the analysis phase
		log.log_info("Starting data analysis phase...");
		timer.phaseStarted(new PhaseBeginEvent());

		log.log_debug("computing roc curve points for icosmo...");
		//run simulation for X number of days
		for( int time = 0; !timer.isFinishedAnalysisPhase();time++){
			TimerEvent e = new TimerEvent(time);
			timer.messageArrived(e);
		}

		//end broad cast end of simulation,
		timer.phaseEnded(new PhaseCompleteEvent());
	}

	public static void runPhase3(GlobalTimer timer) throws InterruptedException{
		Logger log = LoggerFactory.getInstance();
		//this will output everything
		log.log_info("Starting result output phase...");
		timer.phaseStarted(new PhaseBeginEvent());
		timer.phaseEnded(new PhaseCompleteEvent());
	}

	public static void main(String[] args) throws Exception{
		
		if(args.length < 1){
			System.out.println("expected at least one <config file path>... exiting");
			System.exit(1);
		}

		Noise.SLEEP_INTERVAL = 100;//make sure to switch from testing time to actual time for randomness (ie, the time required to generate a noise object)
		for(String arg : args){
			System.out.println("In Main");
	
			SimulationLoader loader = new SimulationLoader(arg);
			Logger log = LoggerFactory.getInstance();
			
			logBeginSimulation();
	
			try{
				
				
				
				int numIterations = loader.getNumberOfAnalysisReplayIterations();
				for(int i = 0;i<numIterations;i++){
					
					//reads all parameters from config files
					GlobalTimer timer = loader.load();
		
					//this configuration run doesn't run a simulation?
					if(timer == null) {
						break;
					}
					log.log_info("\n**Iteration "+i+"**\n");
					//skip the first phase if were reading history from file
					if(loader.getHistoryMode() == SimulationLoader.HistoryMode.CREATE){
						runPhase1(timer);
		
					}
					runPhase2(timer);
					runPhase3(timer);
				
				}
	
			}catch(Exception e){
	
				log.log_error(e.getMessage());
				e.printStackTrace();
	
				StringWriter writer = new StringWriter();
				PrintWriter printWriter= new PrintWriter(writer);
				e.printStackTrace(printWriter);
	
				log.log_error(writer.toString());
				
				continue;
			}
	
			logEndSimulation();
		}
		System.exit(0);
	}

	public static void logBeginSimulation(){
		Logger log = LoggerFactory.getInstance();


		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");


		String time =  dateFormat.format(date);

		log.log_info("Simulation started: "+time);
	}

	public static void logEndSimulation(){
		Logger log = LoggerFactory.getInstance();


		Date date = new Date() ;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss");


		String time =  dateFormat.format(date);

		log.log_info("Simulation ended: "+time);
	}
}

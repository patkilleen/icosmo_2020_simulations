package test.phase.analysis.output;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import common.Algorithm;
import common.event.PerformanceMetricEvent;
import common.event.PhaseCompleteEvent;
import common.event.stream.PerformanceMetricInputStream;
import common.event.stream.PerformanceMetricOutputStream;
import common.event.stream.PerformanceMetricStreamManager;
import phase.analysis.PerformanceAnalysisTimer;
import phase.analysis.output.OutputTimer;

public class TestOutputTimer extends OutputTimer {

	@Test
	public void testPhaseStarted() throws InterruptedException {


		/* List<Algorithm>algs = new ArrayList<Algorithm>(4);
			algs.add(new Algorithm(0));
			algs.add(new Algorithm(1));
			algs.add(new Algorithm(0).toICOSMO());
			algs.add(new Algorithm(1).toICOSMO());
			
		PerformanceMetricOutputStream performanceMetricOutputStream = new PerformanceMetricOutputStream(algs);
		PerformanceMetricInputStream performanceMetricInputStream = new PerformanceMetricInputStream(algs);
		PerformanceMetricStreamManager performanceMetricManager = new PerformanceMetricStreamManager(performanceMetricInputStream,performanceMetricOutputStream);
		
		
		
		TestOutputTimer t = new TestOutputTimer();
		String inputConfigFilePath = "C:\\Temp\\icosmo\\input\\config.xml";
		String inputLogFilePath = "C:\\Temp\\icosmo\\input\\log.txt";
		String outputFileDirectory =  "C:\\Temp\\icosmo\\test-output";
		String outputLogFileName ="test-log.txt";
		String outputConfigFileName="test-config.xml";
		String outputRocCSVFileName="simulation-output.csv";
		String inputRocRScriptFilePath="C:\\Temp\\icosmo\\input\\run.r";
		String outputRocCurveImageFileName="roc.png";
		String inputDir = "C:\\Temp\\icosmo\\input";
		String inputBatchScript = "C:\\Temp\\icosmo\\input\\run.bat";
		String histOuputFileName = "hist.ser";
		t.init(performanceMetricInputStream, 0, inputConfigFilePath, inputLogFilePath, outputFileDirectory, 
				outputLogFileName, outputConfigFileName, outputRocCSVFileName, inputRocRScriptFilePath, outputRocCurveImageFileName, inputDir,histOuputFileName,inputBatchScript,algs);

		for(Algorithm alg: algs){
			for(int i = 0; i < 100; i ++){
				PerformanceMetricEvent e = PerformanceAnalysisTimer.createPerformanceMetricEvent(alg, 10+i, 15+i*2, 20+i*3, 30+i*3, (double)i*0.01);
				performanceMetricOutputStream.write(alg, e);
			}
		}
		
		performanceMetricManager.flush();
		
		t.phaseEnded(new PhaseCompleteEvent());*/
	}


}

package common;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.DoubleStream;

import common.log.Logger;
import common.log.LoggerFactory;

public class RandomNumberStream {

	private Random randomizer;
	private DoubleStream randomNumbers;
	private Iterator<Double> randomNumberIterator;
	private long randomNumbersSize;
	/**
	 * Constructor creates the stream of random numbers between 0 and 1, uniformly distributed
	 * @param randomNumberSize number of random numbers to populate in stream
	 */
	public RandomNumberStream(long randomNumbersSize) {
		//randomizer = new DRand();
		randomizer = new Random();
		randomNumbers = randomizer.doubles(randomNumbersSize);
		randomNumberIterator = randomNumbers.iterator();
		this.randomNumbersSize = randomNumbersSize;
		//randomizer.
		//generateRandomNumberStream(randomNumbersSize);
	}

	public RandomNumberStream() {
		//randomizer = new DRand();
		randomizer = new Random();
		randomNumbers = null;
		randomNumberIterator = null;
		randomNumbersSize = -1;
		//randomizer.
		//generateRandomNumberStream(randomNumbersSize);
	}
	
	/*
	private void generateRandomNumberStream(int randomNumbersSize) {
		double randomNum = 0;
		for(int i = 0;i <randomNumbersSize;i++){
			randomNum = randomizer.generateNoise();
			randomNumbers.add(randomNum);
		}
		
		randomNumberIterator = randomNumbers.iterator();
		
	}*/
	

	
	/**
	 * Returns the next random number from the stream
	 * Note that when the stream no longer has random numbers, it recreates them (a lengthy operation)
	 * @return  a number between 0 and 1
	 */
	public double nextRandomNumber(){

		if(randomNumberIterator == null){
			return randomizer.nextDouble();
		}else{
			
			//ran out of numbers?
			if(!randomNumberIterator.hasNext()){
				
				Logger log = LoggerFactory.getInstance();
				log.log_warning("random number stream was too small ("+randomNumbersSize+"), a new stream will be created...");
				randomNumbers = randomizer.doubles(randomNumbersSize);
				randomNumberIterator = randomNumbers.iterator();
			}
			return randomNumberIterator.next();
		}
	}


}

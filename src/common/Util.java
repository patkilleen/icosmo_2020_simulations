package common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import common.log.Logger;
import common.log.LoggerFactory;

public class Util {

	public Util() {
		// TODO Auto-generated constructor stub
	}
	
	public static <E> List<E> unique(List<E> list){
		
		if(list == null || list.size() == 0){
			return list;
		}
		
		 List<E> uniqueRes = new ArrayList<E>(list.size());

		    for(E e: list){
		        if(!uniqueRes.contains(e)){
		        	uniqueRes.add(e);
		        }
		    }
		return uniqueRes;
	}
		
	public static <E> boolean allElementsUnique(List<E> list){
		if(list == null || list.size() == 0){
			return true;
		}
		
		return Util.unique(list).size() == list.size();
	}
	
	/**
	 * Returns true when atleast one of the objects is null, but not both.
	 * @param o1
	 * @param o2
	 * @return true when atleast one is null, but not both. false when both null, or both non null
	 */
	public static boolean oneObjectNullOtherIsNonNull(Object o1, Object o2){
		if(o1 == null){
			if(o2 != null){
				return true;//o2 is null, o1 isn't
			}else{
				return false;//both null
			}
		}else if(o2 == null){
			return true;// o1 isn't null, o2 is
		}else{
			return false;//o1 is not null, o2 is not null
		}
		//return true;
		
	}
	
	/**
	 * Converts a given list of doubles to a resulting list of the elements with desired presicion. the duplicates are removed
	 * @param tmpUniqueZScores
	 * @return
	 */
	public static List<Double> convertDecimalPrecision(List<Double> tmpUniqueZScores, int precision){
		
		List<Double> resultUniqueZScores = new ArrayList<Double>(tmpUniqueZScores.size());
		
		//iterate all the zscores and truncate the decimals
		for(Double d : tmpUniqueZScores){
			Double truncatedThreshold = BigDecimal.valueOf(d)
										.setScale(precision, RoundingMode.HALF_UP)
										.doubleValue();
			resultUniqueZScores.add(truncatedThreshold);
		}
		
		//now make sure to remove all duplicates
		return Util.unique(resultUniqueZScores);
		
	}
}

package structures;

import java.util.ArrayList;

/**
 * This class is a repository of sorting methods used by the interval tree.
 * It's a utility class - all methods are static, and the class cannot be instantiated
 * i.e. no objects can be created for this class.
 * 
 * @author runb-cs112
 */
public class Sorter {
	
	/**
	 * Switches values in sort method
	 * 
	 * @param intervals
	 * @param i
	 * @param j
	 * @param equal
	 * @return
	 */
	
	private static void switchValues(ArrayList<Interval> intervals, int i, int j, boolean equal){
		if (equal){
			Interval itvl1 = new Interval(intervals.get(j).leftEndPoint,intervals.get(j).rightEndPoint,intervals.get(j).info);
			Interval itvl2 = new Interval(intervals.get(i+1).leftEndPoint,intervals.get(i+1).rightEndPoint,intervals.get(i+1).info);
			intervals.set(j, itvl2);
			intervals.set(i+1, itvl1);
		} else {
			Interval itvl1 = new Interval(intervals.get(j).leftEndPoint,intervals.get(j).rightEndPoint,intervals.get(j).info);
			Interval itvl2 = new Interval(intervals.get(i).leftEndPoint,intervals.get(i).rightEndPoint,intervals.get(i).info);
			intervals.set(j, itvl2);
			intervals.set(i, itvl1);
		}
		return;
	}

	private Sorter() { }
	
	/**
	 * Sorts a set of intervals in place, according to left or right endpoints.  
	 * At the end of the method, the parameter array list is a sorted list. 
	 * 
	 * @param intervals Array list of intervals to be sorted.
	 * @param lr If 'l', then sort is on left endpoints; if 'r', sort is on right endpoints
	 */
	public static void sortIntervals(ArrayList<Interval> intervals, char lr) {
		if (lr == 'l'){
			for (int i = 0; i <= intervals.size()-2; i++){
				for (int j = i+1; j <= intervals.size()-1; j++){
					if (intervals.get(i).leftEndPoint == intervals.get(j).leftEndPoint){
						switchValues(intervals, i, j, true);
					} else if (intervals.get(i).leftEndPoint > intervals.get(j).leftEndPoint){
						switchValues(intervals, i, j, false);
					}
				}
			}
		} else {
			for (int i = 0; i <= intervals.size()-2; i++){
				for (int j = i+1; j <= intervals.size()-1; j++){
					if (intervals.get(i).rightEndPoint == intervals.get(j).rightEndPoint){
						switchValues(intervals, i, j, true);
					} else if (intervals.get(i).rightEndPoint > intervals.get(j).rightEndPoint){
						switchValues(intervals, i, j, false);
					}
				}
			}	
		}
		return;
	}
	/**
	 * 
	 * @param points
	 * @return points without duplicates
	 */
	public static ArrayList<Integer> removeDuplicates(ArrayList<Integer> points){
		
		for(int i = 0; i <= points.size()-1; i++){
			for (int j = i+1; j <= points.size()-1; j++){
				if (points.get(i) == points.get(j)){
					points.remove(j);
				}
			}
		}
		
		return points;
	}
	
	/**
	 * 
	 * @param points
	 * @return
	 */
	public static ArrayList<Integer> sortPoints(ArrayList<Integer> points){
		
		for (int i = 0; i <= points.size()-2; i++){
			for (int j = i+1; j <= points.size()-1; j++){
				if (points.get(i) > points.get(j)){
					int hold = points.get(j);
					points.remove(j);
					points.add(j, points.get(i));
					points.remove(i);
					points.add(i, hold);
				}
			}
		}
		
		return points;
	}
	
	/**
	 * Given a set of intervals (left sorted and right sorted), extracts the left and right end points,
	 * and returns a sorted list of the combined end points without duplicates.
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 * @return Sorted array list of all endpoints without duplicates
	 */
	public static ArrayList<Integer> getSortedEndPoints(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		ArrayList<Integer> points = new ArrayList<Integer>();
		for (int i = 0; i <= leftSortedIntervals.size()-1; i++){
			points.add(leftSortedIntervals.get(i).leftEndPoint);
		}
		for (int i = 0; i <= rightSortedIntervals.size()-1; i++){
			points.add(rightSortedIntervals.get(i).rightEndPoint);
		}
		points = removeDuplicates(removeDuplicates(points));
		points = sortPoints(points);
		return points;
	}
}

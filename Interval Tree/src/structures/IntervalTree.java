package structures;

import java.util.*;

/**
 * Encapsulates an interval tree.
 * 
 * @author runb-cs112
 */
public class IntervalTree {
	
	/**
	 * The root of the interval tree
	 */
	IntervalTreeNode root;
	
	/**
	 * Constructs entire interval tree from set of input intervals. Constructing the tree
	 * means building the interval tree structure and mapping the intervals to the nodes.
	 * 
	 * @param intervals Array list of intervals for which the tree is constructed
	 */
	public IntervalTree(ArrayList<Interval> intervals) {
		
		// make a copy of intervals to use for right sorting
		ArrayList<Interval> intervalsRight = new ArrayList<Interval>(intervals.size());
		for (Interval iv : intervals) {
			intervalsRight.add(iv);
		}
		
		// rename input intervals for left sorting
		ArrayList<Interval> intervalsLeft = intervals;
		
		// sort intervals on left and right end points
		Sorter.sortIntervals(intervalsLeft, 'l');
		Sorter.sortIntervals(intervalsRight,'r');
		
		// get sorted list of end points without duplicates
		ArrayList<Integer> sortedEndPoints = Sorter.getSortedEndPoints(intervalsLeft, intervalsRight);
		
		// build the tree nodes
		root = buildTreeNodes(sortedEndPoints);
		
		// map intervals to the tree nodes
		mapIntervalsToTree(intervalsLeft, intervalsRight);
	}
	
	/**
	 * Builds the interval tree structure given a sorted array list of end points.
	 * 
	 * @param endPoints Sorted array list of end points
	 * @return Root of the tree structure
	 */
	public static IntervalTreeNode buildTreeNodes(ArrayList<Integer> endPoints) {

		Queue<IntervalTreeNode> Q = new Queue<IntervalTreeNode>();
		
		for (int i = 0; i <= endPoints.size()-1; i++){
			IntervalTreeNode itn = new IntervalTreeNode(endPoints.get(i), endPoints.get(i), endPoints.get(i));
			Q.enqueue(itn);
		}
		while (Q.size() > 1){
			IntervalTreeNode itn1 = Q.dequeue();
			if (itn1.maxSplitValue > Q.peek().minSplitValue){
				Q.enqueue(itn1);
				continue;
			}
			IntervalTreeNode itn2 = Q.dequeue();
			IntervalTreeNode itn = new IntervalTreeNode((itn1.minSplitValue + 
					itn2.maxSplitValue)/2, itn1.minSplitValue, itn2.maxSplitValue);
			itn.leftChild = itn1;
			itn.rightChild = itn2;
			Q.enqueue(itn);
		}
		return Q.dequeue();
	}
	
	/**
	 * Maps a set of intervals to the nodes of this interval tree. 
	 * 
	 * @param leftSortedIntervals Array list of intervals sorted according to left endpoints
	 * @param rightSortedIntervals Array list of intervals sorted according to right endpoints
	 */
	public void mapIntervalsToTree(ArrayList<Interval> leftSortedIntervals, ArrayList<Interval> rightSortedIntervals) {
		addIntervalsToNode(this.root, leftSortedIntervals, rightSortedIntervals, 'l');
		addIntervalsToNode(this.root, leftSortedIntervals, rightSortedIntervals, 'r');
	}
	
	/**
	 * Uses recursion to add intervals to all interval tree nodes
	 * 
	 * @param itn
	 * @param leftSortedIntervals
	 * @param rightSortedIntervals
	 */
	private static void addIntervalsToNode(IntervalTreeNode itn, ArrayList<Interval> leftSortedIntervals, 
			ArrayList<Interval> rightSortedIntervals, char lr){
		if (itn.leftChild == null && itn.rightChild == null){
			return;
		}
		if (lr == 'l'){
			itn.leftIntervals = new ArrayList<Interval>();
			for (int i = 0; i <= leftSortedIntervals.size()-1; i++){
				if (itn.splitValue >= leftSortedIntervals.get(i).leftEndPoint && itn.splitValue <= leftSortedIntervals.get(i).rightEndPoint){
					itn.leftIntervals.add(leftSortedIntervals.get(i));
				}
			}
		} else {
			itn.rightIntervals = new ArrayList<Interval>();
			for (int i = 0; i <= rightSortedIntervals.size()-1; i++){
				if (itn.splitValue >= rightSortedIntervals.get(i).leftEndPoint && itn.splitValue <= rightSortedIntervals.get(i).rightEndPoint){
					itn.rightIntervals.add(rightSortedIntervals.get(i));
				}
			}
		}
		if (itn.leftChild != null){
			addIntervalsToNode(itn.leftChild, leftSortedIntervals, rightSortedIntervals, lr);
		}
		if (itn.rightChild != null){
			addIntervalsToNode(itn.rightChild, leftSortedIntervals, rightSortedIntervals, lr);
		}
		
	}
	
	/**
	 * Gets all intervals in this interval tree that intersect with a given interval.
	 * 
	 * @param q The query interval for which intersections are to be found
	 * @return Array list of all intersecting intervals; size is 0 if there are no intersections
	 */
	public ArrayList<Interval> findIntersectingIntervals(Interval q) {
		ArrayList<Interval> intersections = findIntersectAtNode(this.root, q);
		removeDuplicates(intersections);
		return intersections;
	}
	
	/** Removes duplicates of intersections Arraylist
	 * 
	 * @param intersections
	 */
	private static void removeDuplicates(ArrayList<Interval> intersections){
		for (int i = 0; i <= intersections.size()-2; i++){
			for (int j = i; j <= intersections.size()-1; j++){
				if (intersections.get(i).info == intersections.get(j).info){
					intersections.remove(j);
				}
			}
		}
	}
	
	/** Create one list of all intersecting intervals. Contains duplicates
	 * 
	 * @param itn
	 * @param q
	 * @return
	 */
	private static ArrayList<Interval> findIntersectAtNode(IntervalTreeNode itn, Interval q){
		if (itn.leftChild == null && itn.rightChild == null){
			return new ArrayList<Interval>();
		}
		ArrayList<Interval> inters = new ArrayList<Interval>();
		if (itn.splitValue <= q.rightEndPoint && itn.splitValue >= q.leftEndPoint){
			for (int i = 0; i <= itn.leftIntervals.size()-1; i++){
				inters.add(itn.leftIntervals.get(i));
			}
			ArrayList<Interval> intersL = findIntersectAtNode(itn.leftChild,q);
			ArrayList<Interval> intersR = findIntersectAtNode(itn.rightChild,q);
			for (int i = 0; i <= intersL.size()-1; i++){inters.add(intersL.get(i));}
			for (int i = 0; i <= intersR.size()-1; i++){inters.add(intersR.get(i));}
			return inters;
		} else if (itn.splitValue > q.rightEndPoint){
			for (int i = 0; i <= itn.rightIntervals.size()-1; i++){
				if ((itn.rightIntervals.get(i).rightEndPoint >= q.rightEndPoint && itn.rightIntervals.get(i).leftEndPoint <= q.leftEndPoint) ||
					(itn.rightIntervals.get(i).rightEndPoint <= q.rightEndPoint && itn.rightIntervals.get(i).leftEndPoint >= q.leftEndPoint) ||
					(itn.rightIntervals.get(i).rightEndPoint >= q.rightEndPoint && itn.rightIntervals.get(i).leftEndPoint <= q.rightEndPoint) ||
					(itn.rightIntervals.get(i).rightEndPoint >= q.leftEndPoint && itn.rightIntervals.get(i).leftEndPoint <= q.leftEndPoint)){
						inters.add(itn.rightIntervals.get(i));
				}
			}
			ArrayList<Interval> intersL = findIntersectAtNode(itn.leftChild,q);
			for (int i = 0; i <= intersL.size()-1; i++){inters.add(intersL.get(i));}
			return inters;
		} else if (itn.splitValue < q.leftEndPoint){
			for (int i = 0; i <= itn.leftIntervals.size()-1; i++){
				if ((itn.leftIntervals.get(i).rightEndPoint >= q.rightEndPoint && itn.leftIntervals.get(i).leftEndPoint <= q.leftEndPoint) ||
					(itn.leftIntervals.get(i).rightEndPoint <= q.rightEndPoint && itn.leftIntervals.get(i).leftEndPoint >= q.leftEndPoint) ||
					(itn.leftIntervals.get(i).rightEndPoint >= q.rightEndPoint && itn.leftIntervals.get(i).leftEndPoint <= q.rightEndPoint) ||
					(itn.leftIntervals.get(i).rightEndPoint >= q.leftEndPoint && itn.leftIntervals.get(i).leftEndPoint <= q.leftEndPoint)){
						inters.add(itn.leftIntervals.get(i));
				}
			}
			ArrayList<Interval> intersR = findIntersectAtNode(itn.rightChild,q);
			for (int i = 0; i <= intersR.size()-1; i++){inters.add(intersR.get(i));}
			return inters;
		}
		return new ArrayList<Interval>();
	}
	
	/**
	 * Returns the root of this interval tree.
	 * 
	 * @return Root of interval tree.
	 */
	public IntervalTreeNode getRoot() {
		return root;
	}
}
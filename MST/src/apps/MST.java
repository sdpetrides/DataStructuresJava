package apps;

import structures.*;

import java.util.ArrayList;

public class MST {
	
	/**
	 * Initializes the algorithm by building single-vertex partial trees
	 * 
	 * @param graph Graph for which the MST is to be found
	 * @return The initial partial tree list
	 */
	public static PartialTreeList initialize(Graph graph) {
	
		PartialTreeList ptl = new PartialTreeList();
		
		for (int i = 0; i < graph.vertices.length; i++){
			PartialTree pt = new PartialTree(graph.vertices[i]);
			MinHeap<PartialTree.Arc> arcs = pt.getArcs();
			for (structures.Vertex.Neighbor n = graph.vertices[i].neighbors; n != null ; n = n.next){
				arcs.insert(new apps.PartialTree.Arc(graph.vertices[i],n.vertex,n.weight));
			}
			ptl.append(pt);
		}
		return ptl;
	}

	/**
	 * Executes the algorithm on a graph, starting with the initial partial tree list
	 * 
	 * @param graph Graph for which MST is to be found
	 * @param ptlist Initial partial tree list
	 * @return Array list of all arcs that are in the MST - sequence of arcs is irrelevant
	 */
	public static ArrayList<PartialTree.Arc> execute(Graph graph, PartialTreeList ptlist) {
		
		ArrayList<PartialTree.Arc> mst = new ArrayList<PartialTree.Arc>();
		
		while (ptlist.size() > 1){
			
			// remove partial tree
			PartialTree pt = ptlist.remove();
			
			if (pt.getArcs().isEmpty()){
				continue;
			}
			
			PartialTree.Arc arc = pt.getArcs().deleteMin();
			
			// check to see if it is the root or one of its parents
			Vertex v = pt.getRoot();
			
			if (arc.v2.getRoot() == v){
				while (arc.v2.getRoot() == v){
					arc = pt.getArcs().deleteMin();
				}
			}

			// add arc to MST
			mst.add(arc);
			
			// find the other list
			PartialTree pt2 = ptlist.removeTreeContaining(arc.v2);
			
			// merge arc lists
			pt2.merge(pt);
			
			ptlist.append(pt2);
			
			ptlist = print(ptlist);
		}

		return mst;
	}
	
	private static PartialTreeList print(PartialTreeList ptlist){
		PartialTreeList ptlist2 = new PartialTreeList();
		while (ptlist.size() > 0){
			PartialTree pt = ptlist.remove();
			ptlist2.append(pt);
		}
		return ptlist2;
	}
}

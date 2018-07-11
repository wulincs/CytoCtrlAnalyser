package org.cytoscape.sample.internal.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//for state transition use only
public class EdgesManage {
	static // the edges in bipartite graph
	Map<Integer,Set<Integer>> edgeInGraph = new HashMap<Integer,Set<Integer>>();//edges with weight 1
	// number of Nodes
	static int nodeNumber;
	//chosen nodes
	static Set<Integer> chosenNodes;
	

	public EdgesManage(FindSubGraph findSub) {
		// TODO Auto-generated constructor stub
		chosenNodes = findSub.chosenNodesSub;
		nodeNumber = findSub.nodeNumberSub;
		edgeInGraph = findSub.edgesSub;
	}

	public static long getEdgeWeight(int start, int end){
		if(edgeInGraph.containsKey(start) && edgeInGraph.get(start).contains(end)){
			return 100000;
		}else if(chosenNodes.contains(end)){
			return 1;
		}else if(start == end){
			return 0;
		}else{
			return -nodeNumber*100000;
		}
	}
	public long getEdgeWeightStartChosen(int start, int end){
		if(edgeInGraph.get(start).contains(end)){
			return 100000;
		}else{
			return 1;
		}
	}
	public long getEdgeWeightStartChosenB(int start, int end){
		if(edgeInGraph.get(start).contains(end)){
			return 100000;
		}else if(start == end){
			return 0;
		}else{
			return -nodeNumber;
		}
	}
	public long getEdgeWeightStartChosenC(int start, int end) {
		if(start == end){
			return 0;
		}else{
			return -nodeNumber;
		}
	}
//	public EdgesManage(Map<Integer, ArrayList<Integer>> edgeList,
//			FileInput graph) {
//		// TODO Auto-generated constructor stub
//		nodeNumber = graph.countNode;
//		//edgeDeci
//		Set<Integer> startNodes = new HashSet<Integer>();
//		startNodes = edgeList.keySet();
//		Iterator<Integer> it = startNodes.iterator(); 
//		while(it.hasNext()){
//			int stN = it.next();
//			ArrayList<Integer> tempList = edgeList.get(stN);
//			Set<Integer> endNodes = new HashSet<Integer>();
//			for(int i = 0, len = tempList.size(); i < len; ++i){
//				endNodes.add(tempList.get(i));
//			}
//			edgeDeci.put(stN, endNodes);
//		}
//		//edgeDeci
//		startNodes = graph.adjList.keySet();
//		it = startNodes.iterator(); 
//		while(it.hasNext()){
//			int stN = it.next();
//			ArrayList<Integer> tempList = graph.adjList.get(stN);
//			Set<Integer> endNodes = new HashSet<Integer>();
//			for(int i = 0, len = tempList.size(); i < len; ++i){
//				endNodes.add(tempList.get(i));
//			}
//			edgeInte.put(stN, endNodes);
//		}		
//	}



	
}

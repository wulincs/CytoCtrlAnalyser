package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;

//for state transition use only
public class FindSubGraph {
	//input variables
	Map<Integer, ArrayList<Integer>> edges = new HashMap<Integer,ArrayList<Integer>>();//edges with weight 1
	Map<Integer, ArrayList<Integer>> revEdges = new HashMap<Integer,ArrayList<Integer>>();//edges with weight 1
	// number of Nodes
	int nodeNumber;
	//chosen nodes
	public Set<Integer> chosenNodes;
	Network network;
	
	//output subgraph
	// the edges in bipartite graph
	public Map<Integer,Set<Integer>> edgesSub = new HashMap<Integer,Set<Integer>>();//edges with weight 1
	public Map<Integer,Set<Integer>> revEdgesSub = new HashMap<Integer,Set<Integer>>();//reverse edges
	// number of Nodes
	public int nodeNumberSub;
	//chosen nodes
	public Set<Integer> chosenNodesSub = new HashSet<Integer>();
	
	//function parameters
	int[] flag;
	public Map<Integer, Integer> nameMapSubNet = new HashMap<Integer,Integer>();//map(subNetworkID, networkID)

	public FindSubGraph(Set<Integer> chosenN, Network graph) {
		// TODO Auto-generated constructor stub
		network = graph;
		chosenNodes = chosenN;
		nodeNumber = graph.nodeNum;
		edges = graph.adjList;
		revEdges = graph.revAdjList;
		flag = new int[nodeNumber];
		findReachableSubG();
		constructSubgraph();
	}

	private void constructSubgraph() {
		// TODO Auto-generated method stub
		//each node inference minus the offset to get new inference No in subgraph 
		int[] offset = new int[nodeNumber];
		int count = 0;
		for(int i = 0; i < nodeNumber; ++i){
			if(0 == flag[i]){
				++count;
			}
			offset[i] = count;
		}
		nodeNumberSub = nodeNumber - count;
		for(int chN : chosenNodes){
			chosenNodesSub.add(chN - offset[chN-1]);
		}
		//map node id in subnetwork to original id
		for(int nodeID : network.revNodeKeyMap.keySet()){
			if(1 == flag[nodeID-1]){
				nameMapSubNet.put(nodeID - offset[nodeID-1], nodeID);
			}
		}
		
		//create keyMap of subgraph
//		Set<Node> set = 
		for(Node nodeI : network.nodeKeyMap.keySet()){
			int nTmp = network.nodeKeyMap.get(nodeI);
			if(flag[nTmp-1] != 0){
				network.nodeKeyMapSub.put(nodeI, nTmp-offset[nTmp-1]);
			}
		}
//		Set<Integer> setR = network.revNodeKeyMap.keySet();
		for(int nTmp : network.revNodeKeyMap.keySet()){
			if(flag[nTmp-1] != 0){
				network.revNodeKeyMapSub.put(nTmp-offset[nTmp-1], network.revNodeKeyMap.get(nTmp)); 
			}
		}
		
		for(int stN : edges.keySet()){
			if(1 == flag[stN-1]){
				Set<Integer> endNodeSub = new HashSet<Integer>();
				for(int endN : edges.get(stN)){
					endNodeSub.add(endN - offset[endN-1]);
				}
				edgesSub.put(stN-offset[stN-1], endNodeSub);
			}
		}
//		Set<Integer> startNodes = new HashSet<Integer>();
//		startNodes = edges.keySet();
//		Iterator<Integer> ite = startNodes.iterator(); 
//		while(ite.hasNext()){
//			int stN = ite.next();
//			if(1 == flag[stN-1]){
//				Iterator<Integer> ite2 = edges.get(stN).iterator();
//				Set<Integer> endNodeSub = new HashSet<Integer>();
//				while(ite2.hasNext()){
//					int endN = ite2.next();
//					endNodeSub.add(endN - offset[endN-1]);
//				}
//				edgesSub.put(stN-offset[stN-1], endNodeSub);
//			}
//		}
		for(int stN : revEdges.keySet()){
			if(1 == flag[stN-1]){
				Set<Integer> startNodeSub = new HashSet<Integer>();
				for(int endN : revEdges.get(stN)){
					if(1 == flag[endN-1]){
						int st = endN - offset[endN-1];
						startNodeSub.add(st);
					}
				}
				if(!startNodeSub.isEmpty())
					revEdgesSub.put(stN-offset[stN-1], startNodeSub);
			}
		}
//		Set<Integer> endNodes = new HashSet<Integer>();
//		endNodes = revEdges.keySet();
//		Iterator<Integer> iter = endNodes.iterator(); 
//		while(iter.hasNext()){
//			int stN = iter.next();
//			if(1 == flag[stN-1]){
//				Iterator<Integer> ite2 = revEdges.get(stN).iterator();
//				Set<Integer> startNodeSub = new HashSet<Integer>();
//				while(ite2.hasNext()){
//					int endN = ite2.next();
//					if(1 == flag[endN-1]){
//						int st = endN - offset[endN-1];
//						startNodeSub.add(st);
//					}
//				}
//				revEdgesSub.put(stN-offset[stN-1], startNodeSub);
//			}
//		}
	}

	private void findReachableSubG() {
		// TODO Auto-generated method stub
		//DFS searching for each nodes in the column array
		for(int chN : chosenNodes){
			if(0 == flag[chN-1]){
				visitOne(chN);
			}
		}
//		Iterator<Integer> it = chosenNodes.iterator(); 
//		while(it.hasNext()){
//			int chN = it.next();
//			if(0 == flag[chN-1]){
//				visitOne(chN);
//			}
//		}
	}
	
	//DFS for one node
	private void visitOne(int cur){
	    flag[cur-1] = 1;
	    ArrayList<Integer> outPutNodes = new ArrayList<Integer>();
	    for(int i = 1; i <= nodeNumber; ++i){
	    	if(flag[i-1] == 0 && edges.containsKey(cur) && edges.get(cur).contains(i)){
	    		outPutNodes.add(i);
	    	}
	    }
	    for(int i = 0; i < outPutNodes.size(); ++i){
	    	int adjCur = outPutNodes.get(i);
	    	if(0 == flag[adjCur-1])
	    		visitOne(adjCur);
	    }
	}
	
	
	
}

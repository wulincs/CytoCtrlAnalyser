package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.graph.MinCostMaxFlow;

public class ProbabilityInMSS extends Algorithm {

	CyNetwork cyNetwork;
	Network network;
	ArrayList<Node> nodes;

//	Set<Integer> MDS = new HashSet<Integer>();
//	Set<Integer> MSS = new HashSet<Integer>();
	
	
	public ProbabilityInMSS(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
	}
	
	
	public void run() {		
		//create a column in table for nodes' properties
		CyTable c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		try{
				c.createColumn("ProbabilityInMSS", Double.class, false);
			}catch(IllegalArgumentException e){				
		} 
		
		calculation();
				
	}


	private void calculation() {
		// TODO Auto-generated method stub
		
		
		//number of iterations
		int timeIterations;
		int th = network.nodeNum * (int)Math.log(network.nodeNum);//a threshold suggested by paper of control capacity
		timeIterations = Math.max(th, 1000);		
		timeIterations = Math.min(timeIterations, 10000); // run for at most 10,000 times, because of running time
		//count the times of nodes that act as driver nodes
		int[] countNumber = new int[network.nodeNum+1];
		
		for(int i = 0; i < timeIterations; ++i){
			//re order the numbers from 1 to N
			ArrayList<Integer> tmpNodeKeys = new ArrayList<Integer>();
			for(int nodei : network.nodeKeys){
				tmpNodeKeys.add(nodei);
			}
			//random swap node label
			int[] nodeKeyMap = new int[network.nodeNum+1];//key map for relabeled network
			int[] nodeKeyMapRev = new int[network.nodeNum+1];//reverse key map for relabeled network
			for(int nodei : network.nodeKeys){
				//relabel for right part
				int randNum = (int) (Math.random()*tmpNodeKeys.size());
				int nodeB = tmpNodeKeys.get(randNum);
				tmpNodeKeys.remove(randNum);
				nodeKeyMap[nodei] = nodeB;
				nodeKeyMapRev[nodeB] =  nodei;
			}
			
			//-------------------------------------------------------
			// create a network with relabeled nodes
			// adjList, revAdjList, nodeKeys, edgeNum, nodeNum and maxKey are needed
			Network relabeledNetwork = new Network();
			Map<Integer,ArrayList<Integer>> newAdjList = new HashMap<Integer,ArrayList<Integer>>();
			for(int nodei : network.adjList.keySet()){
				ArrayList<Integer> relabeledNodes = new ArrayList<Integer>();
				for(int nodeO : network.adjList.get(nodei)){
					relabeledNodes.add(nodeKeyMap[nodeO]);
				}
				newAdjList.put(nodeKeyMap[nodei], relabeledNodes);
			}
			Map<Integer,ArrayList<Integer>> newRevAdjList = new HashMap<Integer,ArrayList<Integer>>();
			for(int nodei : network.revAdjList.keySet()){
				ArrayList<Integer> relabeledNodes = new ArrayList<Integer>();
				for(int nodeO : network.revAdjList.get(nodei)){
					relabeledNodes.add(nodeKeyMap[nodeO]);
				}
				newRevAdjList.put(nodeKeyMap[nodei], relabeledNodes);
			}
			relabeledNetwork.adjList = newAdjList;
			relabeledNetwork.revAdjList = newRevAdjList;
			relabeledNetwork.nodeKeys = network.nodeKeys;
			relabeledNetwork.edgeNum = network.edgeNum;
			relabeledNetwork.nodeNum = network.nodeNum;
			relabeledNetwork.maxKey = network.maxKey;
			//end create relabeled network-------------------------------------------------------------
			//run MSS algorithm
			MinCostMaxFlow mcmf = new MinCostMaxFlow(relabeledNetwork);
			mcmf.getMaxFlowMM();
			
			// analyze result
			for(Node nodeI : nodes){
				if(mcmf.MSS.contains(nodeKeyMap[network.nodeKeyMap.get(nodeI)])){//unmatched node 
					++countNumber[network.nodeKeyMap.get(nodeI)];
				}else{
				}
			}
			
			
		}//end for
		//display results in node property table
		for(Node nodeI : nodes){
			double pInMSS = (double) countNumber[network.nodeKeyMap.get(nodeI)] / timeIterations;
			try{
				nodeI.setpPInMSS(pInMSS);
				cyNetwork.getRow(nodeI.getN()).set("ProbabilityInMSS", pInMSS);
			}catch(IllegalArgumentException e){	
				
			} 
		}
		
		
		
	}
	
	
	
	
	
	
	
	
	
}

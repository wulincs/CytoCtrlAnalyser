package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.graph.MaxMatching;

public class ControlCapacity extends Algorithm {
	CyNetwork cyNetwork;
	Network network;//created network for running algorithms
	ArrayList<Node> nodes;
	CyTable c;
	int[] xN;
	int[] yN;
	boolean[] check;
	public int matchedNum = 0;
	
	public ControlCapacity(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		xN = new int[allNodes.size()+1];
		yN = new int[allNodes.size()+1];
		check = new boolean[allNodes.size()+1];
	}

	public void run() {	
		try{
			c.createColumn("ControlCapacity", Double.class, false);
		}catch(IllegalArgumentException e){	
			
		} 
		caculation();
	}

	private void caculation() {
		//algorithm developed by paper "Control Capacity and A Random Sampling Method in Exploring Controllability of Complex Networks" 
		// TODO Auto-generated method stub
		maxMatching();
//		int matchedN = matchedNum;
		//number of iterations
		int timeIterations;
		int th = network.nodeNum * (int)Math.log(network.nodeNum);//a threshold suggested by paper of control capacity
		timeIterations = Math.max(th, 1000);		
		timeIterations = Math.min(timeIterations, 10000); // run for at most 10,000 times, because of running time
		//count the times of nodes that act as driver nodes
		int[] countNumber = new int[network.nodeNum+1];
		
		Set<Integer> matchWaitingNodes = null; // matched nodes within which a random node will be picked 		
		
		
		boolean isPickedReplacible = true; // measure if the picked node can be replaced by another node 
		
		
		for(int i = 0; i < timeIterations; ++i){
			// save current xN
			int[] xNOri = xN.clone();
			//if it is a new iteration with new max matching
			if(isPickedReplacible){
				//update the counterNumber
				for(int ni = 1; ni < xN.length; ni++){
					if(xN[ni] == -1){
						++countNumber[ni];
					}
				}
				// matched nodes within which a random node will be picked 		
				matchWaitingNodes = new HashSet<Integer>();
				for(int ni = 1; ni < xN.length; ni++){
					if(xN[ni] != -1){
						matchWaitingNodes.add(ni);	
					}
				}
			}
			//randomly pick a matched node
			int randN = (int) Math.floor(Math.random() * matchWaitingNodes.size()) + 1; // 1 to matchWaitingNodes.size
			int pickedNode = 0; //the picked node
			int currentMatched = 0;
			if(!matchWaitingNodes.isEmpty()){//if no node in the current max matching can be replaced
				for(int nodei : matchWaitingNodes){
					++currentMatched;
					if(currentMatched == randN){
						pickedNode = nodei;
						matchWaitingNodes.remove(nodei);
						break;
					}
				}
			}else{
				xN = caculation2();
				--i;
				continue;
			}
			// all matched nodes except the picked node
			Set<Integer> matchNodes = new HashSet<Integer>();
			for(int ni = 1; ni < xN.length; ni++){
				if(xN[ni] != -1 && ni != pickedNode){
					matchNodes.add(ni);
				}
			}
			//enumerate all max matching with only pickedNode replaced
			Set<Integer> removedNodes = new HashSet<Integer>();
			removedNodes.add(pickedNode);
			Map<Integer, int[]> replNodes = new HashMap<Integer, int[]>();// node can replace the picked node. Equals to ( removedNode - {pickedNode} )
			
			int tempRemoved = pickedNode;
			while(true){
				boolean flag = true;
				Arrays.fill(check, false);
				int matchedTempRemoved = xN[tempRemoved];
				xN[tempRemoved] = -1;
				if(DFS(matchedTempRemoved, removedNodes)){
					flag = false;
					for(int ni = 1; ni < xN.length; ni++){
						if(xN[ni] != -1 && !matchNodes.contains(ni)){//find the new matched node
							//the new matched node is ni, and remove ni to enumerate all possible replacement nodes
							tempRemoved = ni;
							removedNodes.add(ni);
							replNodes.put(ni, xN.clone());//save the xN corresponding to the current matching
							break;
						}
					}
				}				
				//if no more nodes added, then all nodes are enumerated
				if(flag){
					break;
				}
			}
			//randomly pick one from replace nodes, then get the current max matching together with the matchedNodes
			if(replNodes.size() > 0){
				int randN2 = (int) Math.floor(Math.random() * replNodes.size()) + 1;
				int index = 0;
				int newReplaceNode = 0;
				for(int tNode : replNodes.keySet()){
					++index;
					if(index == randN2){
						newReplaceNode = tNode;
						break;
					}
				}
				xN = replNodes.get(newReplaceNode);
				isPickedReplacible = true;
			}else{//if there is no eligible replace node
				xN = xNOri;
				--i;
				isPickedReplacible = false;
			}
			
			
		}
		
		
		for(Node nodeI : nodes){
			double cCapI = (double) countNumber[network.nodeKeyMap.get(nodeI)] / timeIterations;
			try{
				nodeI.setCCap(cCapI);
				cyNetwork.getRow(nodeI.getN()).set("ControlCapacity", cCapI);
			}catch(IllegalArgumentException e){	
				
			} 
		}
	}

	private boolean DFS(int node, Set<Integer> removedNodes) {
		// TODO Auto-generated method stub
		if(network.adjList.containsKey(node)){
			for(int uOut : network.adjList.get(node)){
				if(!check[uOut] && !removedNodes.contains(uOut)){
					check[uOut] = true;
					if(-1 == xN[uOut] || DFS(xN[uOut], removedNodes)){
						xN[uOut] = node;
						yN[node] = uOut;
						return true;
					}
				}
			}
		}	
	
		return false;
	}

	private void maxMatching() {
	// TODO Auto-generated method stub
		Arrays.fill(xN, -1); 
		Arrays.fill(yN, -1); 
		for(int i = 1; i <= nodes.size(); i++){
			if(-1 == xN[i]){
				Arrays.fill(check, false);
	            if(DFS(i)){
	                matchedNum++;
	//                System.out.println(matchNum);
	            }
			}
		}
	}

	private boolean DFS(int node) {
		// TODO Auto-generated method stub
		if(network.revAdjList.containsKey(node)){
			for(int uOut : network.revAdjList.get(node)){
				if(!check[uOut]){
					check[uOut] = true;
					if(-1 == yN[uOut] || DFS(yN[uOut])){
						yN[uOut] = node;
						xN[node] = uOut;
						return true;
					}
				}
			}
		}	
	
		return false;
	}
	
	
	private int[] caculation2() {
		// TODO Auto-generated method stub
		// run maximum matching algorithm
		
		//for maximum matching in bipartite graph, relabel nodes in the right part (or y), the unmatched nodes in left (or x) will changed and the MDS changed
		
		
//		//number of iterations
//		int timeIterations;
//		int th = network.nodeNum * (int)Math.log(network.nodeNum);//a threshold suggested by paper of control capacity
//		timeIterations = Math.max(th, 1000);		
//		timeIterations = Math.min(timeIterations, 10000); // run for at most 10,000 times, because of running time
//		//count the times of nodes that act as driver nodes
//		int[] countNumber = new int[network.nodeNum+1];
		
//		for(int i = 0; i < timeIterations; ++i){
			//re order the numbers from 1 to N
			ArrayList<Integer> tmpNodeKeys = new ArrayList<Integer>();
			ArrayList<Integer> tmpNodeKeys2 = new ArrayList<Integer>();
			for(int nodei : network.nodeKeys){
				tmpNodeKeys.add(nodei);
				tmpNodeKeys2.add(nodei);
			}
			//random swap node label
			int[] nodeKeyMapR = new int[network.nodeNum+1];//key map for relabeled network
			int[] nodeKeyMapRevR = new int[network.nodeNum+1];//reverse key map for relabeled network
			int[] nodeKeyMapL = new int[network.nodeNum+1];//key map for relabeled network
			int[] nodeKeyMapRevL = new int[network.nodeNum+1];//reverse key map for relabeled network
			for(int nodei : network.nodeKeys){
				//relabel for right part
				int randNum = (int) (Math.random()*tmpNodeKeys.size());
				int nodeB = tmpNodeKeys.get(randNum);
				tmpNodeKeys.remove(randNum);
				nodeKeyMapR[nodei] = nodeB;
				nodeKeyMapRevR[nodeB] =  nodei;
				//relabel for left part
				int randNum2 = (int) (Math.random()*tmpNodeKeys2.size());
				int nodeB2 = tmpNodeKeys2.get(randNum2);
				tmpNodeKeys2.remove(randNum2);
				nodeKeyMapL[nodei] = nodeB2;
				nodeKeyMapRevL[nodeB2] =  nodei;
			}
			
			//create new bipartite graph, after relabeled nodes in right part
			Map<Integer,ArrayList<Integer>> newBiG = new HashMap<Integer,ArrayList<Integer>>();
			for(int nodei : network.revAdjList.keySet()){
				ArrayList<Integer> relabeledY = new ArrayList<Integer>();
				for(int nodeO : network.revAdjList.get(nodei)){
					relabeledY.add(nodeKeyMapR[nodeO]);
				}
				newBiG.put(nodeKeyMapL[nodei], relabeledY);
			}
			
			MaxMatching mm = new MaxMatching(newBiG, nodes);
			mm.maxMatching();
			// analyze result
			int[] xNTmp = new int[mm.xN.length]; 
			Arrays.fill(xNTmp, -1); 
			for(int i = 1; i < xNTmp.length; ++i){
				if(-1 != mm.xN[nodeKeyMapL[i]]){//unmatched node
					xNTmp[i] = nodeKeyMapRevR[mm.xN[nodeKeyMapL[i]]];
				}
			}
			
			
			
//		}//end iteration
		//display results in node property table
//		for(Node nodeI : nodes){
//			double cCapI = (double) countNumber[network.nodeKeyMap.get(nodeI)] / timeIterations;
//			try{
//				nodeI.setCCap(cCapI);
//				cyNetwork.getRow(nodeI.getN()).set("ControlCapacity", cCapI);
//			}catch(IllegalArgumentException e){	
//				
//			} 
//		}

		return xNTmp;
		
	}
	
	
	
	
	
	
	
	
}
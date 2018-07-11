package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.sample.internal.Node;

public class MaxMatching {
	static final int INF = Integer.MAX_VALUE/2;	
	Map<Integer,ArrayList<Integer>> biGraph = new HashMap<Integer,ArrayList<Integer>>();
	Set<Integer> leftNodeKeys = new HashSet<Integer>();//all the keys of left part nodes
	Set<Integer> rightNodeKeys = new HashSet<Integer>();
	public int[] xNyN;
	boolean[] check;
	public int[] xN;
	public int[] yN;
	Set<Integer> keys = new HashSet<Integer>();
	ArrayList<Node> nodes;
	int nodeNum;
	public int matchNum = 0;
	int unMatched = 0;
	int indispensable = 0;
	int dispensable = 0;
	int neutral = 0;
	
	// for MSS / find all matched SCC***********************************************************
	public MaxMatching(Map<Integer, ArrayList<Integer>> biG, Set<Integer> lKeys, Set<Integer> rKeys, int gMaxKey) { 
		// TODO Auto-generated constructor stub
		biGraph = biG;
		leftNodeKeys = lKeys;
		rightNodeKeys = rKeys;
		xNyN = new int[gMaxKey];
		check = new boolean[gMaxKey];
	}
	
	public void mMatching() {  
		Arrays.fill(xNyN, INF); 
	    for(int i : leftNodeKeys){
	        if(INF == xNyN[i]){
	        	Arrays.fill(check, false);
	            if(DFS(i)){
	                matchNum++;
//	                System.out.println(matchNum);
	            }
	        }
	    }
	}
	

	
	private boolean DFS(int node) {
		// TODO Auto-generated method stub
		if(biGraph.containsKey(node)){
			for(int uOut : biGraph.get(node)){
				if(!check[uOut]){
					check[uOut] = true;
					if(xNyN[uOut] == INF || DFS(xNyN[uOut])){
						xNyN[uOut] = node;
						xNyN[node] = uOut;
						return true;
					}
				}
			}
		}	

		return false;
	}
	//end: for MSS / find all matched SCC***********************************************************
	
	
	public MaxMatching(Map<Integer, ArrayList<Integer>> biG, ArrayList<Node> nodesList) { 
		// TODO Auto-generated constructor stub
		biGraph = biG;
		nodes = nodesList;
		nodeNum = nodes.size();
//		keys = nodes;
		xN = new int[nodeNum+1];
		yN = new int[nodeNum+1];
		check = new boolean[nodeNum+1];
	}
	
	public void maxMatching() {
		// TODO Auto-generated method stub
		Arrays.fill(xN, -1); 
		Arrays.fill(yN, -1); 
		for(int i = 1; i <= nodeNum; i++){
			if(-1 == xN[i]){
				Arrays.fill(check, false);
	            if(DFS2(i)){
	            	matchNum++;
//	                System.out.println(matchNum);
	            }
			}
		}
	}
	private boolean DFS2(int node) {
		// TODO Auto-generated method stub
		if(biGraph.containsKey(node)){
			for(int uOut : biGraph.get(node)){
				if(!check[uOut]){
					check[uOut] = true;
					if(-1 == yN[uOut] || DFS2(yN[uOut])){
						yN[uOut] = node;
						xN[node] = uOut;
						return true;
					}
				}
			}
		}	
		return false;
	}
	public void nodeClassify() {  
		//record node type
		int[] nodeType = new int[yN.length];
		Arrays.fill(nodeType, -1);
		for(int i = 1; i <= nodes.size(); ++i){
			System.out.println(i);
			//restore xN, yN after each iteration
			int[] backXN = new int[xN.length];
			int[] backYN = new int[yN.length];
			for(int j = 0; j < xN.length; ++j){
				backXN[j] = xN[j];
				backYN[j] = yN[j];
			}
			//find alternative path
			int currentUnmatched = unMatched;
//			boolean isIUnmatched = INF == xN[i]? true : false;
			if(yN[i] != -1){
				xN[yN[i]] = -1;
				yN[i] = -1;
				++currentUnmatched;
			}
			if(xN[i] != -1){
				yN[xN[i]] = -1;
				xN[i] = -1;
			}else{
				--currentUnmatched;
			}
			for(int unmatchedNodes = 1; unmatchedNodes <= nodes.size(); ++unmatchedNodes){
		    	if( -1 == xN[unmatchedNodes] &&  unmatchedNodes != i){
					Arrays.fill(check, false);
					if(DFS(unmatchedNodes, i)){
						--currentUnmatched;
		            }
		    	}				
			}
			
			if(currentUnmatched < unMatched){
				++dispensable;
				nodeType[i] = 0;
			}else if(currentUnmatched == unMatched){
				++neutral;
				nodeType[i] = 1;
			}else if(currentUnmatched > unMatched){
				++indispensable;
				nodeType[i] = 2;
			}			
			//restore for next node
			xN = backXN;
			yN = backYN;
		}
		for(Node nodei : nodes){
			int nodeID = nodei.getId();
			nodei.setClfMDS(nodeType[nodeID]);
		}
	}
	private boolean DFS(int node, int removedNode) {
		// TODO Auto-generated method stub
		if(biGraph.containsKey(node)){
			for(int uOut : biGraph.get(node)){
				if(!check[uOut] && uOut != removedNode){
					check[uOut] = true;
					if(yN[uOut] == -1 || DFS(yN[uOut], removedNode)){
						yN[uOut] = node;
						xN[node] = uOut;
						return true;
					}
				}
			}
		}	

		return false;
	}
	
}
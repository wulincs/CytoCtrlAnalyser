package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class Dijkstra {
	static final int INF = Integer.MAX_VALUE/2;
	// Dijkstra's algorithm to find shortest path from s to all other nodes
	Map<Integer,ArrayList<Integer>> graph = new HashMap<Integer,ArrayList<Integer>>();
	Map<String,Float> edgeCost = new HashMap<String,Float>();
//	int maxKey;
	Set<Integer> nodeKeys = new HashSet<Integer>();//all the keys of nodes in graph
//	Map<Integer,Integer> dist = new HashMap<Integer,Integer>();
	public float[] dist;
//	Map<Integer,Integer> preD = new HashMap<Integer,Integer>();
	public int[] preD;
	int source;
	int target;
	
	public Dijkstra(Map<Integer, ArrayList<Integer>> flowG, Map<String, Float> cost, Set<Integer> nKeys, int s, int t, int gMaxKey) { 
		// TODO Auto-generated constructor stub
		graph = flowG;
		edgeCost = cost;
		nodeKeys = nKeys;
		source = s;
		target = t;
//		maxKey = maxKeyFG;
		dist = new float[gMaxKey];
		preD = new int[gMaxKey];
	}

	// use adjacent list
    public void dijkstra() {    	 
//   	 	int nodeNum = maxKey;
   	 	
   	 	float min = 0;
   	 	int nextNode = 0;
	   	// initiate parameters
   	 	
		Arrays.fill(dist, INF);
		Arrays.fill(preD, INF);
		dist[source] = 0;
		preD[source] = source;
		for(int sourceConnect : graph.get(source)){
			dist[sourceConnect] = edgeCost.get(source+":"+sourceConnect);
			preD[sourceConnect] = source;
		}

	 	Set<Integer> unVisited = new HashSet<Integer>();
	 	for(int i : nodeKeys){
	 		unVisited.add(i);
	 	}
	 	unVisited.remove(source);
		
//   	 	Set<Integer> visited = new HashSet<Integer>();
//   	 	visited.add(source);

   	 	Queue<Integer> unvisitedDist0 = new LinkedList<Integer>();  
	   	
	   	boolean flagEndwhile = true;
	   	while(flagEndwhile){
	   		min = INF;	    	 
		   	boolean flag = false;
		   	
		   	if(!unvisitedDist0.isEmpty()){
		   		nextNode = unvisitedDist0.poll();
		   		min = dist[nextNode];
		   		flag = true;
		   	}else{
		   		for(int j:unVisited){
		   			if(min > dist[j]) {
		   				min = dist[j];
		   				nextNode = j;
		   				flag = true;
		   				if(min == 0)
		   					break;
		   			}
		   		}
		   	}
	   		if(!flag){//break if no more unvisited node can be reached
	   			break;
	   		}
//	   		visited.add(nextNode);
	   		unVisited.remove(nextNode);
	   		
	   		if(graph.containsKey(nextNode))
		   		for(int k:graph.get(nextNode)){
		   			
		   			if(dist[k] != 0 && unVisited.contains(k) && min+edgeCost.get(nextNode+":"+k) < dist[k]){ 
		   				dist[k] = min+edgeCost.get(nextNode+":"+k);
		   				preD[k] = nextNode;
		   				
		   				if(dist[k] == 0){
		   					unvisitedDist0.add(k);
		   				}
		   				
		   			}
		   			
		   		}
	   		
	   	}   
//	   	System.out.println("a");
    }	
	     
	     
}
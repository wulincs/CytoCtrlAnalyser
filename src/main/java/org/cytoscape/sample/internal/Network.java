package org.cytoscape.sample.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.cytoscape.model.CyNode;
//created network for running algorithms
public class Network {
	//Map node to integers from 1 to N
	public Map<Node, Integer> nodeKeyMap = new HashMap<Node, Integer>();
	public Map<Integer, Node> revNodeKeyMap = new HashMap<Integer, Node>();
	public Map<CyNode, Integer> cyNodeKeyMap = new HashMap<CyNode, Integer>();
	public Map<Integer, CyNode> revCyNodeKeyMap = new HashMap<Integer, CyNode>();
	public Map<Integer,ArrayList<Integer>> adjList = new HashMap<Integer,ArrayList<Integer>>();
	public Map<Integer,ArrayList<Integer>> revAdjList = new HashMap<Integer,ArrayList<Integer>>();
//	Map<Integer,ArrayList<Integer>> adjList = new HashMap<Integer,ArrayList<Integer>>();
//	Map<Integer,ArrayList<Integer>> revAdjList = new HashMap<Integer,ArrayList<Integer>>();
	public Set<Integer> nodeKeys = new HashSet<Integer>();//the keys of all nodes
	public int edgeNum = 0;
	public int nodeNum = 0;
	public int maxKey = 0;
	//centrality index; for MSS with preference calculation
	public float[] centrality;//normalized to [0,1]
	public double[] ci;//unnormalized centrality, from ci[1] to ci[n]	
	public float avgCiMSS;
	public float avgCiNonRootMSS;
	//for state transition use only
	public Map<Node, Integer> nodeKeyMapSub = new HashMap<Node, Integer>();
	public Map<Integer, Node> revNodeKeyMapSub = new HashMap<Integer, Node>();
	
	public void normalizeCentrality(){
		double max = 0;
		double min = Double.MAX_VALUE;
		for(int i = 1; i <= ci.length-1; ++i){
			max = ci[i] > max ? ci[i] : max;
			min = ci[i] < min ? ci[i] : min;
		}
		centrality = new float[ci.length];
		for(int i = 1; i <= ci.length-1; ++i){
			centrality[i] = (float) ((ci[i] - min) / (max-min));//if 1-xx:minimize ; xx:maximize
		}
	}	
	
	
}

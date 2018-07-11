package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;

import javax.swing.JComboBox;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.graph.MaxMatching;

public class MDS extends Algorithm {
	CyNetwork cyNetwork;
	Network network;//created network for running algorithms
	ArrayList<Node> nodes;
	
	int[] xN;
	int[] yN;
	boolean[] check;
	public int matchedNum = 0;
	
	public MDS(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		xN = new int[allNodes.size()+1];
		yN = new int[allNodes.size()+1];
		check = new boolean[allNodes.size()+1];
	}

	public void run() {		
//		//********test for calculating degree
//		double score;
//		double[] II = new double[50];
//		int i = 0;
//		CyTable c = network.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
//		try{
//   			c.createColumn("Deg", Double.class, false);
//   		}catch(IllegalArgumentException e){
//   			
//		} 
//		for(Node nodeI : nodes){
//			score=0;
//			List<CyEdge> adjlist = network.getAdjacentEdgeList(nodeI.getN(), Type.ANY);
//			score = adjlist.size();
//			nodeI.setMDS(score);
//			network.getRow(nodeI.getN()).set("Deg", score);
//			II[i++] = score;
//		}
//		JOptionPane.showMessageDialog(null, II[0]+";"+II[1]+";"+II[2]+";"+i,"MDS", JOptionPane.ERROR_MESSAGE);
//		//*****************
		
		// if selected MSS, then return the MDS found by the MSS algorithm
		if(ParameterSet.algorithmSet.get("MSS")){
			return;
		}
		
		
		//create a column in table for nodes' properties
		CyTable c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);		
		try{
				c.createColumn("MDS", Boolean.class, false);
			}catch(IllegalArgumentException e){				
		} 
		// run maximum matching algorithm
		MaxMatching mm = new MaxMatching(network.revAdjList, nodes);
		mm.maxMatching();
//		maxMatching();//not use inner function, change to use class MaxMatching
		//display results in node property table
		for(Node nodeI : nodes){
			if(-1 == mm.xN[network.nodeKeyMap.get(nodeI)]){
				nodeI.setMDS(true);
				cyNetwork.getRow(nodeI.getN()).set("MDS", true);
			}else{
				nodeI.setMDS(false);
				cyNetwork.getRow(nodeI.getN()).set("MDS", false); 
			}
		}
//		JOptionPane.showMessageDialog(null, "DriveNodeNum:"+(nodes.size()-matchedNum),"MDS", JOptionPane.ERROR_MESSAGE);
		return;
	}

//	private void maxMatching() {
//		// TODO Auto-generated method stub
//		Arrays.fill(xN, -1); 
//		Arrays.fill(yN, -1); 
//		for(int i = 1; i <= nodes.size(); i++){
//			if(-1 == xN[i]){
//				Arrays.fill(check, false);
//	            if(DFS(i)){
//	                matchedNum++;
////	                System.out.println(matchNum);
//	            }
//			}
//		}
//	}
//
//	private boolean DFS(int node) {
//		// TODO Auto-generated method stub
//		if(network.revAdjList.containsKey(node)){
//			for(int uOut : network.revAdjList.get(node)){
//				if(!check[uOut]){
//					check[uOut] = true;
//					if(-1 == yN[uOut] || DFS(yN[uOut])){
//						yN[uOut] = node;
//						xN[node] = uOut;
//						return true;
//					}
//				}
//			}
//		}	
//
//		return false;
//	}
	
}

package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.graph.Dijkstra;
import org.cytoscape.sample.internal.graph.GraphRead;
import org.cytoscape.sample.internal.graph.MaxMatching;
import org.cytoscape.sample.internal.graph.MinCostMaxFlow;

public class MSS extends Algorithm {
	CyNetwork cyNetwork;
	Network network;
	ArrayList<Node> nodes;

//	Set<Integer> MDS = new HashSet<Integer>();
//	Set<Integer> MSS = new HashSet<Integer>();
	
	
	public MSS(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
	}
	
	public void run() {		
		
		//create a column in table for nodes' properties
		CyTable c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		try{
				c.createColumn("MSS", Boolean.class, false);
			}catch(IllegalArgumentException e){				
		} 
		//run MSS algorithm
		MinCostMaxFlow mcmf = new MinCostMaxFlow(network);
		mcmf.getMaxFlowMM();
		//display results in node property table
		for(Node nodeI : nodes){
			if(mcmf.MSS.contains(network.nodeKeyMap.get(nodeI))){ 
				nodeI.setMSS(true);
				cyNetwork.getRow(nodeI.getN()).set("MSS", true);
			}else{
				nodeI.setMSS(false);
				cyNetwork.getRow(nodeI.getN()).set("MSS", false); 
			}
		}
		//if select MDS and MSS at the same time, display this MDS
		if(ParameterSet.algorithmSet.get("MDS")){
			try{
				c.createColumn("MDS", Boolean.class, false);
			}catch(IllegalArgumentException e){				
			} 
			for(Node nodeI : nodes){
				if(mcmf.MDS.contains(network.nodeKeyMap.get(nodeI))){ 
					nodeI.setMDS(true);
					cyNetwork.getRow(nodeI.getN()).set("MDS", true);
				}else{
					nodeI.setMDS(false);
					cyNetwork.getRow(nodeI.getN()).set("MDS", false); 
				}
			}
		}
		
		return;
		
	}

}

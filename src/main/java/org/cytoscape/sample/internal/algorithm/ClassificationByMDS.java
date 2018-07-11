
package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Set;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.graph.FindSubGraph;
import org.cytoscape.sample.internal.graph.MaxMatching;

public class ClassificationByMDS extends Algorithm {
	CyNetwork cyNetwork;
	Network network;//created network for running algorithms
	ArrayList<Node> nodes;
	CyTable c;
	Set<Integer> chosenN;
	FindSubGraph findSub;
	
	public ClassificationByMDS(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);		
	}

	public void run() {	
		try{
			c.createColumn("Classification", Integer.class, false);
		}catch(IllegalArgumentException e){				
		}
		MaxMatching mm = new MaxMatching(network.revAdjList, nodes);
		mm.maxMatching();
		mm.nodeClassify();
		for(Node nodeI : nodes){
			cyNetwork.getRow(nodeI.getN()).set("Classification", nodeI.getClfMDS());
		}
		
		
	}
}
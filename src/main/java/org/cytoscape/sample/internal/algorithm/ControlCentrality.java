package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.graph.EdgesManage;
import org.cytoscape.sample.internal.graph.FindSubGraph;
import org.cytoscape.sample.internal.graph.KMAlgorithm;

public class ControlCentrality extends Algorithm {
	CyNetwork cyNetwork;
	Network network;//created network for running algorithms
	ArrayList<Node> nodes;
	CyTable c;
	Set<Integer> chosenN;
	FindSubGraph findSub;
	
	public ControlCentrality(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);		
	}

	public void run() {	
		try{
			c.createColumn("ControlCentrality", Integer.class, false);
		}catch(IllegalArgumentException e){				
		}
		//iterate each node for CCen
		for(Node nodeI : nodes){
			chosenN = new HashSet<Integer>();
			chosenN.add(nodeI.getId());//add the node only
			findSub = new FindSubGraph(chosenN, network);//find reachable subnetwork
			//for kmNew******************************************************************
			findSub.edgesSub.put(findSub.nodeNumberSub+1, findSub.chosenNodesSub);//add a control node to the node
			for(int nodeIDSub : findSub.chosenNodesSub){// get the node id in the subnetwork, record reverse edge from node to u
				if(findSub.revEdgesSub.containsKey(nodeIDSub)){
					findSub.revEdgesSub.get(nodeIDSub).add(findSub.nodeNumberSub+1);
				}else{
					Set<Integer> u =  new HashSet<Integer>();
					u.add(findSub.nodeNumberSub+1);
					findSub.revEdgesSub.put(nodeIDSub,u);
				}
				++findSub.nodeNumberSub;
			}
			//end: for kmNew******************************************************************
			EdgesManage edgeMng = new EdgesManage(findSub);
			KMAlgorithm opt = new KMAlgorithm(findSub);
			long optassig = opt.kmNew();
//			int r = (int) (optassig / 100000);
//			int s= (int) (optassig % 10000);
			nodeI.setCCen((int)optassig);
			cyNetwork.getRow(nodeI.getN()).set("ControlCentrality",(int)optassig);
//			nodeI.setCCen(r+s);
//			cyNetwork.getRow(nodeI.getN()).set("ControlCentrality",r+s);
//			JOptionPane.showMessageDialog(null, nodeI.getId(),"Control Centrality", JOptionPane.ERROR_MESSAGE);
		}
		
		return;
	}
}

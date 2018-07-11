package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.graph.EdgesManage;
import org.cytoscape.sample.internal.graph.FindSCC;
import org.cytoscape.sample.internal.graph.FindSubGraph;
import org.cytoscape.sample.internal.graph.KMAlgorithm;

public class StateTransition extends Algorithm {
	CyNetwork cyNetwork;
	CyTable c;
	Network network;
	ArrayList<Node> nodes;
	Set<Integer> chosenN;//nodes related to state transition
	Set<Integer> SteeringNode;
	FindSubGraph findSub;
//	CyColumn preferenceColumn;
	String transitionColumn;
	
	public StateTransition(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		transitionColumn = ParameterSet.transitionColumn.getText();
		SteeringNode = new HashSet<Integer>();
		chosenN = new HashSet<Integer>();
	}
	
	public void run() {
		// pop up window
		if(transitionColumn.matches("Input the column indicating nodes related to state transition please...")){
			//no input
			JOptionPane.showMessageDialog(null, "Please input the column name","State Transition", JOptionPane.ERROR_MESSAGE); 
		}else{
			if(null == c.getColumn(transitionColumn)){ 
				//wrong name
				JOptionPane.showMessageDialog(null, "The column name can not be found","State Transition", JOptionPane.ERROR_MESSAGE);
			}else if(c.getColumn(transitionColumn).getType().equals(Boolean.class)){ 
				//judge column type, only Integer, Long, Double are permitted. if preferential index is correct, do following
				//create a column in table for nodes' properties
				try{
						c.createColumn("SteeringNodesForStateTransition", Boolean.class, false);
					}catch(IllegalArgumentException e){				
				} 
				caculationPerform();
			}else{
				JOptionPane.showMessageDialog(null, "The column type is not correct, Boolean type is supported","State Transition", JOptionPane.ERROR_MESSAGE);
			}					
		}
		
		
		
	}

	private void caculationPerform() {
		// TODO Auto-generated method stub
		getSelectedNodes(); 
		findSub = new FindSubGraph(chosenN, network);
		EdgesManage edgeMng = new EdgesManage(findSub);
		KMAlgorithm opt = new KMAlgorithm(findSub);
		long optassig = opt.km();
		//exam the matching result
		for(int i = 0; i < opt.yN.length; ++i){
			int startNode = i+1;//findSub.nameMapSubNet.get(i+1);
			int endNode = opt.yN[i]+1;//findSub.nameMapSubNet.get(opt.yN[i]+1);
			if(!(findSub.edgesSub.containsKey(startNode) && findSub.edgesSub.get(startNode).contains(endNode))){
				SteeringNode.add(findSub.nameMapSubNet.get(endNode)); 				
			}
		}
		Calculation getResult = new Calculation(opt);
//		int sourceCircleNum = getResult.findSouceCircle();	
		int sourceCircleNum = getResult.findT();
		for(Node nodeI : nodes){
			if(SteeringNode.contains(network.nodeKeyMap.get(nodeI))){ 
				nodeI.setSST(true);
				cyNetwork.getRow(nodeI.getN()).set("SteeringNodesForStateTransition", true);
			}else{
				nodeI.setSST(false);
				cyNetwork.getRow(nodeI.getN()).set("SteeringNodesForStateTransition", false); 
			}
		}
		
		int r = (int) (optassig / 100000);
		int s= (int) (optassig % 10000);
		int gdcs = r + s;
		int numSteeringNode = s +sourceCircleNum;

	

		
	}

	private void getSelectedNodes() {
		// TODO Auto-generated method stub
		for(Node nodeI : nodes){
			if(cyNetwork.getRow(nodeI.getN()).get(transitionColumn, Boolean.class)){
				chosenN.add(nodeI.getId());
			}
		}
	}
	
	
	//-----------------------------------------------------------------------------------------------------
	private class Calculation {
		int[] yN; //node in X connected to yi 
		Map<Integer,Set<Integer>> revEdgeInGraph = new HashMap<Integer,Set<Integer>>();//reverse edges with weight 1
//		int[] offset; //the offset between node number in subgraph and the entrie graph

		//output results
		int sourceCirNum = 0;
		
		public Calculation(KMAlgorithm opt) {
			// TODO Auto-generated constructor stub
			yN = opt.yN;		
			revEdgeInGraph = opt.revEdgeInGraph;
//			offset = offset2;
		}

		public int findT() {
			Map<Integer, Set<Integer>> circleSet = new HashMap<Integer, Set<Integer>>();//all circles
			boolean[] flag = new boolean[yN.length];
			Arrays.fill(flag, false);
			int countCircle = 0;		
			boolean[] isInteCircle = new boolean[yN.length];
			int[] nodeCircleNo = new int[yN.length];
			boolean[] isPotentialSource = new boolean[yN.length];
			Arrays.fill(isInteCircle, false);
			//potential source are circles without weight 1 edge and can not be reached by any circle has edge 1
			Arrays.fill(isPotentialSource, false);
			Arrays.fill(nodeCircleNo, -1);
			//find out all circles, indicated by "circleSet"
			for(int i = 0, len = yN.length; i < len; ++i){
				if(!flag[i]){
					flag[i] = true;
					boolean flagIsInte  = true;
					Set<Integer> newCircle = new HashSet<Integer>();
					int tmp = i;
					newCircle.add(tmp);
					nodeCircleNo[tmp] = countCircle;
					while(yN[tmp] != i){
						if(1 == EdgesManage.getEdgeWeight(tmp+1,  yN[tmp]+1)){
							flagIsInte  = false;
						}
						tmp = yN[tmp];
						flag[tmp] = true;
						newCircle.add(tmp);
						nodeCircleNo[tmp] = countCircle;
					}
					circleSet.put(countCircle, newCircle);
					if(flagIsInte && 1 != EdgesManage.getEdgeWeight(tmp+1,  yN[tmp]+1)){
						isInteCircle[countCircle] = true;
						isPotentialSource[countCircle] = true;
					}
					++countCircle;
//					circleSet.add(newCircle);
				}
			}
			//find out potential circles, indicated by "isPotentialSource"
			while(true){
				boolean isPotentialCirDelete = false;
				for(int i = 0, len = circleSet.size(); i < len; ++i){//iterate each circle
					boolean flag2 = false;
					if(isInteCircle[i] && isPotentialSource[i]){
						Iterator<Integer> it = circleSet.get(i).iterator();
						while(it.hasNext()){//iterate each nodes in a circle
							int node = it.next();
							if(revEdgeInGraph.containsKey(node+1)){
								Iterator<Integer> it2 = revEdgeInGraph.get(node+1).iterator();
								while(it2.hasNext()){//iterate each reverse edge from a node in circle
									int node2 = it2.next();
									if(!circleSet.get(i).contains(node2-1) && !isPotentialSource[nodeCircleNo[node2-1]]){
										isPotentialSource[i] = false;
										flag2 = true;
										isPotentialCirDelete = true;
										break;
									}
								}
							}
							if(flag2)
								break;
						}
					}
				}
				if(!isPotentialCirDelete)
					break;
			}
			//only consider edges in and between potential circles
			Map<Integer, Set<Integer>> potentialCircleSet = new HashMap<Integer, Set<Integer>>();//construct potential circles set
			int count = 0;
			for(int i = 0, len = circleSet.size(); i < len; ++i){
				if(isPotentialSource[i]){
					potentialCircleSet.put(count, circleSet.get(i));
					++count;
				}
			}
			int[] nodePotenCircleNo = new int[yN.length];
			Arrays.fill(nodePotenCircleNo,-1);
			for(int i = 0, len = potentialCircleSet.size(); i < len; ++i){
				Iterator<Integer> it = potentialCircleSet.get(i).iterator();
				while(it.hasNext()){
					int node = it.next();
					nodePotenCircleNo[node] = i;
				}
			}
			//shrink each potential circle to a node, and construct a adjacent matrix
			//the number of root SCC is the t
			int[][] adjM = new int[potentialCircleSet.size()][potentialCircleSet.size()];
			for(int i : potentialCircleSet.keySet()){
				for(int node : potentialCircleSet.get(i)){
					if(revEdgeInGraph.containsKey(node+1)){
						for(int node2 : revEdgeInGraph.get(node+1)){
							if(!potentialCircleSet.get(i).contains(node2-1)){
								adjM[i][nodePotenCircleNo[node2-1]] = 1;
							}
						}
					}
				}
			}
//			for(int i = 0, len = potentialCircleSet.size(); i < len; ++i){
//				Iterator<Integer> it = potentialCircleSet.get(i).iterator();
//				while(it.hasNext()){
//					int node = it.next();
//					if(revEdgeInGraph.containsKey(node+1)){
//						Iterator<Integer> it2 = revEdgeInGraph.get(node+1).iterator();
//						while(it2.hasNext()){
//							int node2 = it2.next();
//							if(!potentialCircleSet.get(i).contains(node2-1)){
//								adjM[i][nodePotenCircleNo[node2-1]] = 1;
//							}
//						}
//					}
//				}
//			}
			
			FindSCC ft = new FindSCC(adjM);
			ft.findSCC();
			for(int SCCi : ft.sourceSCC){
				for(int circlei : ft.sccComponent.get(SCCi)){
					for(int nodei = 0; nodei < nodePotenCircleNo.length; ++nodei){
						if(circlei == nodePotenCircleNo[nodei] && chosenN.contains(findSub.nameMapSubNet.get(nodei+1))){
							SteeringNode.add(findSub.nameMapSubNet.get(nodei+1));
							break;
						}
					}
				}
			}
			for(int SCCi : ft.singleSCC){
				for(int circlei : ft.sccComponent.get(SCCi)){
					for(int nodei = 0; nodei < nodePotenCircleNo.length; ++nodei){
						if(circlei == nodePotenCircleNo[nodei] && chosenN.contains(findSub.nameMapSubNet.get(nodei+1))){
							SteeringNode.add(findSub.nameMapSubNet.get(nodei+1));
							break;
						}
					}
				}
			}
			int tValue = ft.getSourceSCCNum();
			return tValue;
		}
	}
}

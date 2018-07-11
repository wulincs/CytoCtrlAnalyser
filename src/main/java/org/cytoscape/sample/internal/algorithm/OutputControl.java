package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.graph.FindSCC;
import org.cytoscape.sample.internal.graph.FindSubGraph;
import org.cytoscape.sample.internal.graph.KMAlgorithm;

public class OutputControl extends Algorithm {
	CyNetwork cyNetwork;
	CyTable c;
	Network network;
	ArrayList<Node> nodes;
	Set<Integer> chosenN = new HashSet<Integer>();//nodes be output
//	Set<Integer> SN = new HashSet<Integer>();//steering nodes
//	CyColumn preferenceColumn;
	String outputColumn;
	Set<Integer> steeringNodeSet = new HashSet<Integer>();//steering nodes
	
	public OutputControl(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		outputColumn = ParameterSet.outputColumn.getText();
	}
	
	public void run() {				
		// pop up window
		if(outputColumn.matches("Input the column indicates the output please...")){
			//no input
			JOptionPane.showMessageDialog(null, "Please input the column name","Output Control", JOptionPane.ERROR_MESSAGE); 
		}else{
			if(null == c.getColumn(outputColumn)){ 
				//wrong name
				JOptionPane.showMessageDialog(null, "The column name can not be found","Output Control", JOptionPane.ERROR_MESSAGE);
			}else if(c.getColumn(outputColumn).getType().equals(Boolean.class) ){ 
				//judge column type, only Integer, Long, Double are permitted. if preferential index is correct, do following
				//create a column in table for nodes' properties
				try{
						c.createColumn("Output Control", Boolean.class, false);
					}catch(IllegalArgumentException e){				
				} 
				caculationPerform();
			}else{
				JOptionPane.showMessageDialog(null, "The column type is not correct, Boolean type is supported","Output Control", JOptionPane.ERROR_MESSAGE);
			}					
		}		
	}

	private void caculationPerform() {
		// TODO Auto-generated method stub
		getSelectedNodes(); 
		FindSubGraph findSub = new FindSubGraph(chosenN, network);

		KMAlgorithm opt = new KMAlgorithm(findSub);
		opt.kmOC();
		
		for(int i = opt.checkX.length; i < opt.yN.length; i++){
	    	if(opt.yN[i] != -1){
    			steeringNodeSet.add(findSub.nameMapSubNet.get(opt.yN[i]+1));
	    	}
	    }
		
		Calculation cal = new Calculation(opt);
		cal.findT();
		
		for(int node : cal.additionalSteeringNodes){
			steeringNodeSet.add(findSub.nameMapSubNet.get(node));
		} 
		
		//display nodes result, set node attributes
		for(Node nodeI : nodes){
			if(steeringNodeSet.contains(network.nodeKeyMap.get(nodeI))){ 
				nodeI.setOC(true);
				cyNetwork.getRow(nodeI.getN()).set("Output Control", true);
			}else{
				nodeI.setOC(false);
				cyNetwork.getRow(nodeI.getN()).set("Output Control", false); 
			}
		}
		
	}
	
	private void getSelectedNodes() {
		// TODO Auto-generated method stub
		for(Node nodeI : nodes){
			if(cyNetwork.getRow(nodeI.getN()).get(outputColumn, Boolean.class)){
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
		KMAlgorithm kmResult;
		
		public Set<Integer> additionalSteeringNodes = new HashSet<Integer>();
		
		public Calculation(KMAlgorithm opt) {
			// TODO Auto-generated constructor stub
			yN = opt.yN;		
			revEdgeInGraph = opt.revEdgeInGraph;
			kmResult = opt;
//			offset = offset2;
		}

		public void findT() {
			Map<Integer, Set<Integer>> circleSet = new HashMap<Integer, Set<Integer>>();//all circles
			boolean[] flag = new boolean[yN.length];
			Arrays.fill(flag, false);
			int countCircle = 0;		
//			boolean[] isInteCircle = new boolean[yN.length];
			int[] nodeCircleNo = new int[yN.length];
			boolean[] isPotentialSource = new boolean[yN.length];
//			Arrays.fill(isInteCircle, false);
			//potential source are circles without weight 1 edge and can not be reached by any circle has edge 1
			Arrays.fill(isPotentialSource, true);
			Arrays.fill(nodeCircleNo, -1);
			//change the flag of nodes in paths
			for(int i = kmResult.nodeNumber; i < yN.length; ++i){
				flag[i] = true;
				int tmp = i;
				while(yN[tmp] != -1){//nodes in the path from control nodes
					tmp = yN[tmp];
					nodeCircleNo[tmp] = -1;//node tmp is not in the circles
					flag[tmp] = true;
				}
			}
			//find out all circles, indicated by "circleSet"
			for(int i = 0; i < kmResult.nodeNumber; ++i){
				if(!flag[i]){
					flag[i] = true;
//					boolean flagIsInte  = false;//indicates if the circle is weight 0. if not 0, there are output nodes in the circle
					Set<Integer> newCircle = new HashSet<Integer>();
					int tmp = i;
					newCircle.add(tmp);
					nodeCircleNo[tmp] = countCircle;
					while(yN[tmp] != i){
						tmp = yN[tmp];
						flag[tmp] = true;
						newCircle.add(tmp);
						nodeCircleNo[tmp] = countCircle;
					}
					circleSet.put(countCircle, newCircle);
//					isInteCircle[countCircle] = true;//true: the circle contains output nodes
//					isPotentialSource[countCircle] = true;
					++countCircle;
				}
			}
			
			//find out potential circles, indicated by "isPotentialSource"
			while(true){
				boolean isPotentialCirDelete = false;
				for(int i = 0, len = circleSet.size(); i < len; ++i){//iterate each circle
					boolean flag2 = false;
					if(isPotentialSource[i]){
						for(int node:circleSet.get(i)){//iterate each nodes in a circle
							if(revEdgeInGraph.containsKey(node+1)){
								for(int node2 : revEdgeInGraph.get(node+1)){//iterate each reverse edge from a node in circle
//									if((!circleSet.get(i).contains(node2-1) && !isPotentialSource[nodeCircleNo[node2-1]]) || nodeCircleNo[node2-1] == -1 ){
									if(nodeCircleNo[node2-1] == -1 || !isPotentialSource[nodeCircleNo[node2-1]]){
										isPotentialSource[i] = false;//not a source circle
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
			Map<Integer, Integer> mapPotNAllCir = new HashMap<Integer, Integer>();//<potential circle id & all circle id> construct potential circles set
			int count = 0;
			for(int i = 0, len = circleSet.size(); i < len; ++i){
				if(isPotentialSource[i]){
					potentialCircleSet.put(count, circleSet.get(i));
					mapPotNAllCir.put(count, i);
					++count;
				}
			}
			int[] nodePotenCircleNo = new int[yN.length];
//			for(int i = 0, len = potentialCircleSet.size(); i < len; ++i){
//				Iterator<Integer> it = potentialCircleSet.get(i).iterator();
//				while(it.hasNext()){
//					int node = it.next();
//					nodePotenCircleNo[node] = i;
//				}
//			}
			Arrays.fill(nodePotenCircleNo,-1);
			for(int i = 0, len = potentialCircleSet.size(); i < len; ++i){
				for(int node : potentialCircleSet.get(i)){
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
			
			FindSCC ft = new FindSCC(adjM);
			ft.findSCC();
			
			for(int scci : ft.sourceSCC){
				boolean flag3 = false;
				for(int circle : ft.sccComponent.get(scci)){
					for(int node : potentialCircleSet.get(circle)){
						if(kmResult.chosenNodes.contains(node+1)){
							additionalSteeringNodes.add(node+1);
							flag3 = true;
							break;
						}
					}
					if(flag3){
						break;
					}
				}
			}
			for(int scci : ft.singleSCC){
				boolean flag3 = false;
				for(int circle : ft.sccComponent.get(scci)){
					for(int node : potentialCircleSet.get(circle)){
						if(kmResult.chosenNodes.contains(node+1)){
							additionalSteeringNodes.add(node+1);
							flag3 = true;
							break;
						}
					}
					if(flag3){
						break;
					}
				}
			}
			
//			int tValue = ft.getSourceSCCNum();
//			return tValue;
		}
		
		
	}
	
	//end Class Calculation-----------------------------------------------------------------------------------------------------
	
}

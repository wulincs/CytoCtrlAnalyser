package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import org.cytoscape.model.CyColumn;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyTable;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.graph.Dijkstra;
import org.cytoscape.sample.internal.graph.GraphRead;

public class MSSWithPreference extends Algorithm {
	CyNetwork cyNetwork;
	CyTable c;
	Network network;
	ArrayList<Node> nodes;

	Set<Integer> MDS = new HashSet<Integer>();
	Set<Integer> MSS = new HashSet<Integer>();
//	CyColumn preferenceColumn;
	String preferenceColumn;
	
	public MSSWithPreference(CyNetwork inputNetwork, ArrayList<Node> allNodes, Network net){
		this.cyNetwork = inputNetwork;
		network = net;
		nodes = allNodes;
		c = cyNetwork.getTable(CyNode.class, CyNetwork.DEFAULT_ATTRS);
		preferenceColumn = ParameterSet.preferenceColumn.getText();
	}
	
	public void run() {				
		// pop up window
		if(preferenceColumn.matches("Input the column as preference please...")){
			//no input
			JOptionPane.showMessageDialog(null, "Please input the column name","MSS with preference", JOptionPane.ERROR_MESSAGE); 
		}else{
			if(null == c.getColumn(preferenceColumn)){ 
				//wrong name
				JOptionPane.showMessageDialog(null, "The column name can not be found","MSS with preference", JOptionPane.ERROR_MESSAGE);
			}else if(c.getColumn(preferenceColumn).getType().equals(Integer.class) || 
					 c.getColumn(preferenceColumn).getType().equals(Double.class) ||
					 c.getColumn(preferenceColumn).getType().equals(Float.class) ||
					 c.getColumn(preferenceColumn).getType().equals(Long.class) ){ 
				//judge column type, only Integer, Long, Double are permitted. if preferential index is correct, do following
				//create a column in table for nodes' properties
				try{
						c.createColumn("MSSWithPreference", Boolean.class, false);
					}catch(IllegalArgumentException e){				
				} 
				caculationPerform();
			}else{
				JOptionPane.showMessageDialog(null, "The column type is not correct (must be Integer, Long or Float)","MSS with preference", JOptionPane.ERROR_MESSAGE);
			}					
		}		
	}

	private void caculationPerform() {
		// TODO Auto-generated method stub
		//set preference value of nodes to the network
		network.ci = new double[nodes.size()+1];
		for(Node nodeI : nodes){
			if(c.getColumn(preferenceColumn).getType().equals(Double.class)){
				network.ci[nodeI.getId()] = cyNetwork.getRow(nodeI.getN()).get(preferenceColumn, Double.class);
			}else if(c.getColumn(preferenceColumn).getType().equals(Integer.class)){
				network.ci[nodeI.getId()] = (double)cyNetwork.getRow(nodeI.getN()).get(preferenceColumn, Integer.class);
			}else if(c.getColumn(preferenceColumn).getType().equals(Long.class)){
				network.ci[nodeI.getId()] = (double)cyNetwork.getRow(nodeI.getN()).get(preferenceColumn, Long.class);
			}
		}
		network.normalizeCentrality();
		MinCostMaxFlow mcmf = new MinCostMaxFlow(network);
		mcmf.getMaxFlow();
		for(Node nodeI : nodes){
			if(MSS.contains(network.nodeKeyMap.get(nodeI))){ 
				nodeI.setMSSWithPreference(true);
				cyNetwork.getRow(nodeI.getN()).set("MSSWithPreference", true);
			}else{
				nodeI.setMSSWithPreference(false);
				cyNetwork.getRow(nodeI.getN()).set("MSSWithPreference", false); 
			}
		}
	}
	
	//inner classes for calculating MSS with preference
	private class MinCostMaxFlow {
		static final int INF = Integer.MAX_VALUE/2;
		Network g;
		// adjacent list of weighted graph
		Map<Integer,ArrayList<Integer>> flowG = new HashMap<Integer,ArrayList<Integer>>();
//		Map<String,Integer> edgeCost = new HashMap<String,Integer>();
		Map<String,Float> edgeCost = new HashMap<String,Float>();
		Map<String,Integer> capacity = new HashMap<String,Integer>();
		Set<Integer> nodeKeys = new HashSet<Integer>();//all the keys of nodes in flowG
//		Set<Integer> nodeKeysOri = new HashSet<Integer>();//all the keys of nodes in input graph
//		int maxKeyFG;
		int flow = 0; //flow amount
		int fCost = 0; //cost of flow
		Set<Integer> sourceSCCNode = new HashSet<Integer>();
		// get source SCC Index
		Set<Integer> sourceSCC = new HashSet<Integer>();
		GraphRead graphOperation;
//		int offset;//for root scc
		private int source;
		private int target;
		int[] MSSArr;
		
		
		public MinCostMaxFlow(Network graphObj) {
			// TODO Auto-generated constructor stub
			g = graphObj;
//			maxKeyFG = g.maxKey*2+1;
			graphOperation = new GraphRead(g);
//			nodeKeysOri = g.nodeKeys;
//			offset = (int) Math.pow(10, Math.ceil(Math.log10(g.maxKey)));
			source = 3*g.maxKey+3;
			target = 3*g.maxKey+4;
		}
		
		// construct flow graph --- flowG
		private void createFlowGraph(){
			// node number
			//0 ~ maxKey: left part
			//maxKey+1 ~ 2maxKey+1: right part
			//2maxKey+2 ~ 3maxKey+2: auxiliary nodes
			//3maxKey+3: source
			//3maxKey+4: target
			//3maxKey+5 ~ : auxiliary nodes for each root SCC
			// create flow graph, source: -1, target: -2	
			nodeKeys.add(source);
			nodeKeys.add(target);
			// first find SCC, pick up source SCC nodes
			graphOperation.findSCC();
			// find all the nodes in source SCC
			for(int sccNum : graphOperation.sourceSCC){
				sourceSCC.add(sccNum);
				for(int sccNode : graphOperation.sccComponent.get(sccNum)){
					sourceSCCNode.add(sccNode);
				}
			}
			for(int sccNum : graphOperation.singleSCC){
				sourceSCC.add(sccNum);
				for(int sccNode : graphOperation.sccComponent.get(sccNum)){
					sourceSCCNode.add(sccNode);
				}
			}
			
			// endNodes: a set of nodes have incoming edges
			// endNodesTmp: one node in endNodes
			// create bipartite
			Set<Integer> endNodes = new HashSet<Integer>();
			endNodes = g.revAdjList.keySet();		
			for(Iterator<Integer> iter = endNodes.iterator(); iter.hasNext(); ){
				int endNodesTmp = iter.next();
				nodeKeys.add(endNodesTmp);
				// for endNodesTmp, iterate the out nodes, put into outList
				ArrayList<Integer> outList = new ArrayList<Integer>();
				for(Integer outT : g.revAdjList.get(endNodesTmp)){
		            outList.add(outT+g.maxKey+1); //add g.maxKey+1 for the nodes at right part
		            // set cost of edge
		            String edge = String.valueOf(endNodesTmp)+":"+String.valueOf(outT+g.maxKey+1);
		            nodeKeys.add(outT+g.maxKey+1);
		            edgeCost.put(edge, (float) 0);
		        }
				flowG.put(endNodesTmp, outList);
			}


	        // add edges from source -1 to the auxiliary nodes and left nodes
			Set<Integer> SCC = new HashSet<Integer>();
			SCC = graphOperation.sccComponent.keySet();
			ArrayList<Integer> temp = new ArrayList<Integer>();
			temp.add(-9999);
		    flowG.put(source, temp);
			for(Iterator<Integer> iter = SCC.iterator(); iter.hasNext(); ){
				int sccNum = iter.next();
				if(sourceSCC.contains(sccNum)){					
					if(graphOperation.sccComponent.get(sccNum).size() > 1){
						//get maximum centrality of node in the source SCC
						float maxCi = 0;
						for(int node : graphOperation.sccComponent.get(sccNum)){
							maxCi = maxCi > g.centrality[node] ? maxCi : g.centrality[node];
						}
						int auNode1 = sccNum+ 3*g.maxKey + 5;//connect to left part
						int auNode2 = graphOperation.sccComponent.get(sccNum).get(0) + 2*g.maxKey + 2;
//						int auNode3 = (-sccNum)*100-3;
						nodeKeys.add(auNode1);
						nodeKeys.add(auNode2);
//						nodeKeys.add(auNode3);
			        	flowG.get(source).add(auNode2);
			        	temp = new ArrayList<Integer>();
			    		temp.add(auNode1);
			    	    flowG.put(auNode2, temp);		        	
			        	edgeCost.put(String.valueOf(source)+":"+auNode2, (float) 0);
//			        	edgeCost.put(auNode2+":"+auNode1, 1);
//			        	edgeCost.put(auNode2+":"+auNode1, (float) g.nodeNum);//for centrality based MSS
			        	edgeCost.put(auNode2+":"+auNode1, (float) (g.nodeNum - maxCi));//for centrality based MSS
						// auNode1 to left part
			        	temp = new ArrayList<Integer>();
			        	temp.add(-9999);
			        	flowG.put(auNode1, temp);
						for(int sccNode : graphOperation.sccComponent.get(sccNum)){
							flowG.get(auNode1).add(sccNode);
							String edgeSL = auNode1+":"+sccNode;
//							edgeCost.put(edgeSL, (float) 0);
							edgeCost.put(edgeSL, (float) g.centrality[sccNode]);
						}
						flowG.get(auNode1).remove(0);
						// auNodei to auNode1
						for(int i = 1; i < graphOperation.sccComponent.get(sccNum).size(); i++ ){
							int auNodei = graphOperation.sccComponent.get(sccNum).get(i) + 2*g.maxKey + 2;
							temp = new ArrayList<Integer>();
				        	temp.add(auNode1);
				    	    flowG.put(auNodei, temp);
				    	    edgeCost.put(auNodei+":"+auNode1, (float) 0);
				    	    flowG.get(source).add(auNodei);
				    	    edgeCost.put(String.valueOf(source)+":"+auNodei, (float) 0);
				    	    nodeKeys.add(auNodei);
						}
//			        	capacity.put(auNode2+":"+auNode3, graphOperation.sccComponent.get(sccNum).size()-1);
			        	
					}else{
						int sccNode = graphOperation.sccComponent.get(sccNum).get(0);
						if(g.revAdjList.containsKey(sccNode)){
							flowG.get(source).add(sccNode);
							String edgeSL = String.valueOf(source)+":"+sccNode;
//							edgeCost.put(edgeSL, 1);
							edgeCost.put(edgeSL, (float) g.nodeNum);//for centrality based MSS
						}
					}
					
				}else{
					for(int sccNode : graphOperation.sccComponent.get(sccNum)){
						if(g.revAdjList.containsKey(sccNode)){
							flowG.get(source).add(sccNode);
							String edgeSL = String.valueOf(source)+":"+sccNode;
//							edgeCost.put(edgeSL, 0);
							edgeCost.put(edgeSL, g.centrality[sccNode]);//for centrality based MSS
						}
					}
				}
				
			}
			flowG.get(source).remove(0);
	        
	        // add edge from right to target
			Set<Integer> rightPart = new HashSet<Integer>();
			rightPart = g.adjList.keySet();
			for(Iterator<Integer> iter = rightPart.iterator(); iter.hasNext(); ){
				int rightNode = iter.next();
	        	ArrayList<Integer> tgt = new ArrayList<Integer>();
	        	tgt.add(target);
	        	flowG.put(rightNode+g.maxKey+1, tgt);
				String edge = String.valueOf(rightNode+g.maxKey+1)+":"+String.valueOf(target);
				edgeCost.put(edge, (float) 0);
			}
//			System.out.println("a");
		}
		
		
		
		//capacity for all edges are 1, source is node '-1', target is '-2'
		public void getMaxFlow() {
			
			this.createFlowGraph();
//			Map<Integer,Integer> dist = new HashMap<Integer,Integer>();
//			Map<Integer,Integer> preD = new HashMap<Integer,Integer>();
//			for(int d:nodeKeys){
//	   			dist.put(d, 0);
//		   	}
			float[] dist = new float[4*g.maxKey+5];
			Arrays.fill(dist, 0);
			int[] preD = new int[4*g.maxKey+5];
			
			while(true){
				long startMili = System.currentTimeMillis();
				
				// adjust weight of all edges
				Set<String> edgeC = new HashSet<String>();
				edgeC = edgeCost.keySet();		
				for(Iterator<String> iter = edgeC.iterator(); iter.hasNext(); ){
					String edge = iter.next();
					String[] startToEnd = edge.split(":");
					float tempCost = edgeCost.get(edge);
//					edgeCost.put(edge, tempCost+dist.get(Integer.parseInt(startToEnd[0]))-dist.get(Integer.parseInt(startToEnd[1])));
					edgeCost.put(edge, tempCost+dist[Integer.parseInt(startToEnd[0])]-dist[Integer.parseInt(startToEnd[1])]);
				}
				
				if(flowG.containsKey(source)){
					// find least cost path to target
					Dijkstra sp = new Dijkstra(flowG, edgeCost, nodeKeys, source, target, g.maxKey*4+5);
					sp.dijkstra();
					dist = sp.dist;
					preD = sp.preD;
				}else{
					break;
				}
				
				
				//if exist a path, find the residual network
				if(dist[target] < INF){
					int tempNode = target; //back search from target node
					do{
						int tempPreD = preD[tempNode];
						//adjust the network of the path of the flow
						// take inverse of cost
						reverseEdge(tempPreD, tempNode);					
						tempNode = tempPreD;
					}while(tempNode != source);//end at source node
					flow++;//add flow
//					System.out.println(flow);
//					long endMili=System.currentTimeMillis();	
//					double runTime = (double)(endMili-startMili)/(double)1000;
//					System.out.println("runningtime:"+ runTime +"s");	
				}else{
					break;
				}
				
			}
			
			displayResults();
			
		}
		

		// reverse the edge "tempPreD->int tempNode" in flowG and the edgeCost
		private void reverseEdge(int tempPreD, int tempNode) {
			// TODO Auto-generated method stub
			String edge = tempPreD+":"+tempNode;
			String backEdge = tempNode+":"+ tempPreD;
			float tempCost = edgeCost.get(edge);
			edgeCost.remove(edge);
			edgeCost.put(backEdge, -tempCost);
			//reverse the direction of the edge in path
			flowG.get(tempPreD).remove(flowG.get(tempPreD).indexOf(tempNode));
			if(flowG.get(tempPreD).isEmpty()){
				flowG.remove(tempPreD);
			}
			if(flowG.containsKey(tempNode)){
				flowG.get(tempNode).add(tempPreD);
			}else{
				ArrayList<Integer> newTemp = new ArrayList<Integer>();
				newTemp.add(tempPreD);
				flowG.put(tempNode, newTemp);
			}		
		}
		
		private void displayResults() {
			// TODO Auto-generated method stub
			//find the unmatched source SCC
			int allMatchedSourceSCCNum = 0;
			Set<Integer> AllMatchedsSCC = new HashSet<Integer>();
			for(Iterator<Integer> iter = sourceSCC.iterator(); iter.hasNext(); ){//get one source SCC
				int sccNo = iter.next();
				if(graphOperation.sccComponent.get(sccNo).size() > 1){
					if(!edgeCost.containsKey( (graphOperation.sccComponent.get(sccNo).get(0) + 2*g.maxKey + 2)+":"+(sccNo + 3*g.maxKey + 5)) ){
						allMatchedSourceSCCNum++;
						AllMatchedsSCC.add(sccNo);
					}
				}else{
					if(!edgeCost.containsKey(source+":"+graphOperation.sccComponent.get(sccNo).get(0)) && edgeCost.containsKey(graphOperation.sccComponent.get(sccNo).get(0)+":"+source)){
						allMatchedSourceSCCNum++;
						AllMatchedsSCC.add(sccNo);
					}
				}
							
			}
			//find unmatched nodes in left
			MDS = new HashSet<Integer>();
			MSS = new HashSet<Integer>();
			for(int node : g.nodeKeys){
				MDS.add(node);
				MSS.add(node);
			}
			for(int node : flowG.get(target)){
				MDS.remove(flowG.get(node).get(0));
				MSS.remove(flowG.get(node).get(0));
			}
			
			System.out.print("MDS:");
			for(int i:MDS){
				System.out.print(" "+i + ",");
			}
			System.out.print("\nAllmatchedsSCCNodes:");
			for(int i:AllMatchedsSCC){
				System.out.print(" {");
				float maxCi = -INF;
				float minCi = INF;
				int additonalSNMax = -1;
				int additonalSNMin = -1;//additional steering node to MSS, the node has max/min centrality in the source SCC
				for(int node : graphOperation.sccComponent.get(i)){
					System.out.print(node+",");
					if(g.centrality[node] > maxCi){
						maxCi = g.centrality[node];
						additonalSNMax = node;
					}
					if(g.centrality[node] < minCi){
						minCi = g.centrality[node];
						additonalSNMin = node;
					}
				}
				MSS.add(additonalSNMax);//additional steering node to MSS, the node has max centrality in the source SCC
//				MSS.add(additonalSNMin);//additional steering node to MSS, the node has min centrality in the source SCC
				System.out.print("}; ");
			}
			System.out.print("\nMSS:");
			MSSArr = new int[MSS.size()];
			int index = 0;
			for(int i:MSS){
				MSSArr[index] = i;
				System.out.print(" "+i + ",");
				index++;
			}
			
			//average ci of MSS
			float total = 0;
			for(int node : MSS){
				total += g.ci[node];
			}
			g.avgCiMSS = total / MSS.size();		
			
			//average ci of MSS(except root nodes)
			//find root
			Set<Integer> rootN = new HashSet<Integer>();
			for(int node:g.nodeKeys){
				if(!g.revAdjList.containsKey(node)){
					rootN.add(node);
				}
			}
			
			total = 0;
			for(int node : MSS){
				if(!rootN.contains(node)){
					total += g.ci[node];
				}
			}
			g.avgCiNonRootMSS = total / (MSS.size()-rootN.size());
			//MSS without root nodes
			System.out.print("\nMSS without root nodes:");
			for(int i:MDS){
				if(!rootN.contains(i)){
						System.out.print(" "+i + ",");
				}
			}
			
		
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}

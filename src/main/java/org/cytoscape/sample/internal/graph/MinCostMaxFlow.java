package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cytoscape.sample.internal.Network;

public class MinCostMaxFlow {
	static final int INF = Integer.MAX_VALUE/2;
	private Network g;
	// adjacent list of weighted graph
	private Map<Integer,ArrayList<Integer>> flowG = new HashMap<Integer,ArrayList<Integer>>();
	private Map<String, Float> edgeCost = new HashMap<String,Float>();
	private Map<String,Integer> capacity = new HashMap<String,Integer>();
	private Set<Integer> nodeKeys = new HashSet<Integer>();//all the keys of nodes in flowG
//	Set<Integer> nodeKeysOri = new HashSet<Integer>();//all the keys of nodes in input graph
//	int maxKeyFG;
	int flow = 0; //flow amount
	int fCost = 0; //cost of flow
	// get source SCC Index
	private Set<Integer> sourceSCC = new HashSet<Integer>();
	public GraphRead graphOperation;
//	int offset;//for root scc
	private int source;
	private int target;
//	int MDS;
//	int MSS;
	public Set<Integer> MDS = new HashSet<Integer>();
	public Set<Integer> MSS = new HashSet<Integer>();
	
	public MinCostMaxFlow(Network graphObj) {
		// TODO Auto-generated constructor stub
		g = graphObj;
//		maxKeyFG = g.maxKey*2+1;
		graphOperation = new GraphRead(g);
//		nodeKeysOri = g.nodeKeys;
//		offset = (int) Math.pow(10, Math.ceil(Math.log10(g.maxKey)));
		source = 3*g.maxKey+3;
		target = 3*g.maxKey+4;
	}

			
	// construct flow graph --- flowG
	private void createFlowGraphMM(){
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
		Map<Integer,Integer> sourceSCCNode = new HashMap<Integer,Integer>();
		for(int sccNum : graphOperation.sourceSCC){
			sourceSCC.add(sccNum);
			for(int sccNode : graphOperation.sccComponent.get(sccNum)){
				sourceSCCNode.put(sccNode, sccNum);
			}
		}
		for(int sccNum : graphOperation.singleSCC){
			sourceSCC.add(sccNum);
			for(int sccNode : graphOperation.sccComponent.get(sccNum)){
				sourceSCCNode.put(sccNode, sccNum);
			}
		}
				
		// create flowG--------------------------------------------------
		// create bipartite
		for(int endNodesTmp : g.revAdjList.keySet()){
			// endNodesTmp: one node in endNodes	
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
					int auNode1 = sccNum+ 3*g.maxKey + 5;//connect to left part
					int auNode2 = graphOperation.sccComponent.get(sccNum).get(0) + 2*g.maxKey + 2;
//					int auNode3 = (-sccNum)*100-3;
					nodeKeys.add(auNode1);
					nodeKeys.add(auNode2);
//					nodeKeys.add(auNode3);
		        	flowG.get(source).add(auNode2);
		        	temp = new ArrayList<Integer>();
		    		temp.add(auNode1);
		    	    flowG.put(auNode2, temp);		        	
		        	edgeCost.put(String.valueOf(source)+":"+auNode2, (float) 0);
		        	edgeCost.put(auNode2+":"+auNode1, (float) 1);
					// auNode1 to left part
		        	temp = new ArrayList<Integer>();
		        	temp.add(-9999);
		        	flowG.put(auNode1, temp);
					for(int sccNode : graphOperation.sccComponent.get(sccNum)){
						flowG.get(auNode1).add(sccNode);
						String edgeSL = auNode1+":"+sccNode;
						edgeCost.put(edgeSL, (float) 0);
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
//		        	capacity.put(auNode2+":"+auNode3, graphOperation.sccComponent.get(sccNum).size()-1);
		        	
				}else{
					int sccNode = graphOperation.sccComponent.get(sccNum).get(0);
					if(g.revAdjList.containsKey(sccNode)){
						flowG.get(source).add(sccNode);
						String edgeSL = String.valueOf(source)+":"+sccNode;
						edgeCost.put(edgeSL, (float) 1);
					}
				}
				
			}else{
				for(int sccNode : graphOperation.sccComponent.get(sccNum)){
					if(g.revAdjList.containsKey(sccNode)){
						flowG.get(source).add(sccNode);
						String edgeSL = String.valueOf(source)+":"+sccNode;
						edgeCost.put(edgeSL, (float) 0);
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
		// end create flowG--------------------------------------------------
		
		// create a bipartite without root SCC nodes and find max matching
		// all the edges are weight 0, the flow has no cost
		Map<Integer,ArrayList<Integer>> flowBipartite = new HashMap<Integer,ArrayList<Integer>>();		
		Set<Integer> leftNodeKeys = new HashSet<Integer>();//all the keys of left part nodes
		Set<Integer> rightNodeKeys = new HashSet<Integer>();
		Set<Integer> visitedRootSCC = new HashSet<Integer>();
		for(int endNodesTmp : g.revAdjList.keySet()){			
			// for endNodesTmp not in Root SCC, iterate the out nodes, put into outList
			if(!sourceSCCNode.containsKey(endNodesTmp)){
				ArrayList<Integer> outList = new ArrayList<Integer>();
				for(Integer outT : g.revAdjList.get(endNodesTmp)){
		            outList.add(outT+g.maxKey+1); //add g.maxKey+1 for the nodes at right part
		            if(!rightNodeKeys.contains(outT+g.maxKey+1)){
		            	rightNodeKeys.add(outT+g.maxKey+1);
		            }
		        }
				flowBipartite.put(endNodesTmp, outList);
				leftNodeKeys.add(endNodesTmp);
			}else{
				// for endNodesTmp in Root SCC which larger than one node
				if(!visitedRootSCC.contains(sourceSCCNode.get(endNodesTmp))){
					visitedRootSCC.add(sourceSCCNode.get(endNodesTmp));
				}else{
					ArrayList<Integer> outList = new ArrayList<Integer>();
					for(Integer outT : g.revAdjList.get(endNodesTmp)){
			            outList.add(outT+g.maxKey+1); //add g.maxKey+1 for the nodes at right part
			            if(!rightNodeKeys.contains(outT+g.maxKey+1)){
			            	rightNodeKeys.add(outT+g.maxKey+1);
			            }
			        }
					flowBipartite.put(endNodesTmp, outList);
					leftNodeKeys.add(endNodesTmp);
				}
			}
		}
		MaxMatching mm = new MaxMatching(flowBipartite,leftNodeKeys,rightNodeKeys,g.maxKey*4+5);
		mm.mMatching();
		flow = mm.matchNum;
		// residue network, flowG - the zero cost flow (maximum matching bipartite graph)
		int[] xNyN = mm.xNyN;
		for(int i = 0; i <= g.maxKey + 1; i++){
			if(xNyN[i] != INF){
				if(!sourceSCCNode.containsKey(i)){
					reverseEdge(source,i);
					reverseEdge(i,xNyN[i]);
					reverseEdge(xNyN[i],target);		
				}else{
					reverseEdge(source, i + 2*g.maxKey + 2);
					reverseEdge(i + 2*g.maxKey + 2, sourceSCCNode.get(i)+ 3*g.maxKey + 5);
					reverseEdge(sourceSCCNode.get(i)+ 3*g.maxKey + 5,i);
					reverseEdge(i,xNyN[i]);
					reverseEdge(xNyN[i],target);
				}
			}
		}
		
//		System.out.println("a");
	}
	
	
	//capacity for all edges are 1, source is node '-1', target is '-2'
	public void getMaxFlowMM() {
		
		this.createFlowGraphMM();
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
//				edgeCost.put(edge, tempCost+dist.get(Integer.parseInt(startToEnd[0]))-dist.get(Integer.parseInt(startToEnd[1])));
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
//				System.out.println(flow);
				long endMili=System.currentTimeMillis();	
				double runTime = (double)(endMili-startMili)/(double)1000;
//				System.out.println("runningtime:"+ runTime +"s");	
			}else{
				break;
			}
			
		}
		
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
//		graphOperation.findAllMatchableRootSCC();
//		System.out.println("NodeNum:"+g.nodeNum);
//		System.out.println("EdgeNum:"+g.edgeNum);
//		System.out.println("DriverNodes:"+(g.nodeNum-flow));
////		System.out.println("IsolatedNodeNum:"+graphOperation.isolatedNode);
//		System.out.println("SourceSCC:"+graphOperation.singleSCC.size()+"+"+graphOperation.sourceSCC.size());
//		System.out.println("Sink:"+graphOperation.sinkSCC.size());
//		System.out.println("AllMatchedSourceSCC:"+allMatchedSourceSCCNum);
//		MDS = g.nodeNum-flow;
//		MSS = MDS + allMatchedSourceSCCNum;
		
		//find unmatched nodes in left
		for(int node : g.nodeKeys){
			MDS.add(node);
			MSS.add(node);
		}
		for(int node : flowG.get(target)){
			MDS.remove(flowG.get(node).get(0));
			MSS.remove(flowG.get(node).get(0));
		}
		for(int i:AllMatchedsSCC){
			MSS.add(graphOperation.sccComponent.get(i).get(0));//set the first node in the all matched SCC to MSS
		}
	}

	// reverse the edge "tempPreD->int tempNode" in flowG and the edgeCost
	private void reverseEdge(int tempPreD, int tempNode) {
		// TODO Auto-generated method stub
		String edge = tempPreD+":"+tempNode;
		String backEdge = tempNode+":"+ tempPreD;
		Float tempCost = edgeCost.get(edge);
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
	
}
//private class GraphRead{
//	Network graph = new Network();
//	//key integer is the No.of a scc, ArrayList are the nodes in that scc
//	Map<Integer,ArrayList<Integer>> sccComponent = new HashMap<Integer,ArrayList<Integer>>();
//	//record the source and sink SCC
//	ArrayList<Integer> sourceSCC = new ArrayList<Integer>();
//	ArrayList<Integer> sinkSCC = new ArrayList<Integer>();
//	ArrayList<Integer> singleSCC = new ArrayList<Integer>();
//	int allMatchableRootSCC = 0;
//	int isolatedNode = 0; //nodes without incoming and outgoing edge
//	//parameters in searching
//	private Boolean[] flag;
//	private Integer[] numb;
//	private int sig = 0;
////	Set<Integer> SCC = new HashSet<Integer>();
////	int recur = 0;//recursion depth
//	GraphRead(Network g){
//		this.graph = g;
//		flag = new Boolean[g.nodeNum];
//		Arrays.fill(flag, false);
//		numb = new Integer[g.nodeNum];
//	}
//	
//
//	//find SCCs
//	public void findSCC(){
//		stepOne();
//		stepTwo();
//		getSourceSCC();
////		System.out.println("Number of Nodes: " + graph.countNode);
////		System.out.println("Number of Edges: " + graph.countEdge);
////		System.out.println("Number of sourceSCC: " + sourceSCC.size());
////		System.out.println("Number of sinkSCC: " + sinkSCC.size());
////		System.out.println("Number of singleSCC: " + singleSCC.size());
//	}
//
//	private void getSourceSCC() {
//		// TODO Auto-generated method stub
//		//i SCCs
//		//k,j nodes in one scc
//		//m edges in one node
//		for(int i = 1, len = sccComponent.size(); i <= len; ++i){
//			boolean out = false;//if true, not a sink
//			boolean in = false;// if true, not a source
//			Set<Integer> nodesInSCC = new HashSet<Integer>();// put all the node in one scc to a set
//			for(int k = 0, lenk = sccComponent.get(i).size(); k < lenk; ++k)
//				nodesInSCC.add(sccComponent.get(i).get(k));
//			for(int j = 0, lenj = sccComponent.get(i).size(); j < lenj ;++j){//check edges of each node in scc
//				int tempNodeNo = sccComponent.get(i).get(j);
//				//check each node in scc, whether it direct to other scc
//				if(!out && graph.adjList.containsKey(tempNodeNo)){
//					for(int m = 0, lenm = graph.adjList.get(tempNodeNo).size(); m < lenm; ++m){//edges of one scc
//						if(!nodesInSCC.contains(graph.adjList.get(tempNodeNo).get(m))){
//							out = true;
//							break;
//						}
//					}
//				}
//				//check each node in scc, whether if other scc into this
//				if(!in && graph.revAdjList.containsKey(tempNodeNo)){
//					for(int m = 0, lenm = graph.revAdjList.get(tempNodeNo).size(); m < lenm; ++m){//edges of one scc
//						if(!nodesInSCC.contains(graph.revAdjList.get(tempNodeNo).get(m))){
//							in = true;
//							break;
//						}
//					}
//				}
//			}
//			if(in && !out){
//				sinkSCC.add(i);
//			}else if(!in && out){
//				sourceSCC.add(i);
//			}else if(!in && !out){
//				singleSCC.add(i);
//			}
//		}
////		System.out.print("s");
//	}
//	
//	private void stepOne() {
////		Set<Integer> node = new HashSet<Integer>();
////		node = graph.adjList.keySet();
////		Iterator<Integer> iter = node.iterator();
//		// TODO Auto-generated method stub
//		for(int j : graph.adjList.keySet()){
////			int j = iter.next();
//			if (!flag[j-1]){
//				visitOne(j);
//			}
//		}
//	}
//	
//	private void stepTwo() {
//		// TODO Auto-generated method stub
//		for(int i = 0; i < flag.length; ++i)
//			flag[i] = false;
//		sig = 0;
//		for(int i = numb.length-1; i >= 0; --i){
//			if(numb[i] == null){
//				isolatedNode++;
//				continue;
//			}
//			if(!flag[numb[i]-1]){
//				++sig;
//				visitTwo(numb[i]);
//			}
//		}
////		System.out.println("IsolatedNodeNum:"+isolatedNode);
//	}
//	
//	private void visitTwo(int cur) {
//		// TODO Auto-generated method stub
//		flag[cur-1] = true;
//		if(sccComponent.containsKey(sig)){
//			sccComponent.get(sig).add(cur);
//		}else{
//			ArrayList<Integer> newNode = new ArrayList<Integer>();
//			newNode.add(cur);
//			sccComponent.put(sig, newNode);
//		}
//		if(graph.revAdjList.containsKey(cur)){
//			for(int i = 0, j = graph.revAdjList.get(cur).size(); i < j; ++i){
//				int adjCur = graph.revAdjList.get(cur).get(i);
//				if(!flag[adjCur-1])
//					visitTwo(adjCur);
//			}	
//		}
//	}
//	//DFS for one node
//	private void visitOne(int cur){
//	    flag[cur-1] = true;
//	    if(graph.adjList.containsKey(cur)){
//	    	for(int i = 0, j = graph.adjList.get(cur).size(); i < j; ++i){
//		    	int adjCur = graph.adjList.get(cur).get(i);
//		    	if(!flag[adjCur-1]){
////		    		System.out.println(++recur);//show recursion depth
//		    		visitOne(adjCur);
////		    		System.out.println(--recur);
//		    	}
//		    }	
//	    }
//
//	    numb[sig] = cur;
//	    ++sig;
//	}
//	
//	public void findAllMatchableRootSCC(){
//		// all the root scc index
//		Set<Integer> rootSCC = new HashSet<Integer>();
//		for(int sccNum : sourceSCC){
//			rootSCC.add(sccNum);
//		}
//		for(int sccNum : singleSCC){
//			rootSCC.add(sccNum);
//		}
//		//for each root scc, max matching
//		for(int iSCC : rootSCC){
////			System.out.println("sourceSCCSize:"+sccComponent.get(iSCC).size());
//			// create a bipartite graph
//			Map<Integer,ArrayList<Integer>> biGraph = new HashMap<Integer,ArrayList<Integer>>();
//			Map<Integer,Integer> nodeIndexInSCC = new HashMap<Integer,Integer>();
//			int nodeCount = 0;
//			Set<Integer> leftNodeKeys = new HashSet<Integer>();//all the keys of left part nodes
//			Set<Integer> rightNodeKeys = new HashSet<Integer>();
//			for(int sccNode : sccComponent.get(iSCC)){
//				nodeCount++;
//				nodeIndexInSCC.put(sccNode, nodeCount);
//			}
//			for(int node:nodeIndexInSCC.keySet()){
//				if(graph.revAdjList.containsKey(node)){
//					ArrayList<Integer> outList = new ArrayList<Integer>();
//					outList.add(-9999);
//					for(Integer outT : graph.revAdjList.get(node)){
//						leftNodeKeys.add(nodeIndexInSCC.get(outT));
//						// if right node is in the root scc
//						if(nodeIndexInSCC.containsKey(outT)){ 
//							outList.add(nodeIndexInSCC.get(outT)+nodeCount+1);
//						}
//					}
//					outList.remove(0);
//					biGraph.put(nodeIndexInSCC.get(node), outList);
//				}
//			}
//			for(int i : leftNodeKeys){
//				rightNodeKeys.add(i+nodeCount+1);
//			}
//			MaxMatching mm = new MaxMatching(biGraph,leftNodeKeys,rightNodeKeys,2*nodeCount+2);
//			mm.mMatching();
//			if(mm.matchNum == nodeCount){
//				allMatchableRootSCC++;
//			}
//		}
//		System.out.println("allMatchableRootSCC:"+allMatchableRootSCC);
//	}
//}


//private class MaxMatching {
//	static final int INF = Integer.MAX_VALUE/2;	
//	Map<Integer,ArrayList<Integer>> biGraph = new HashMap<Integer,ArrayList<Integer>>();
//	Set<Integer> leftNodeKeys = new HashSet<Integer>();//all the keys of left part nodes
//	Set<Integer> rightNodeKeys = new HashSet<Integer>();
//	int[] xNyN;
//	boolean[] check;
//	int matchNum = 0;
//	
//	public MaxMatching(Map<Integer, ArrayList<Integer>> biG, Set<Integer> lKeys, Set<Integer> rKeys, int gMaxKey) { 
//		// TODO Auto-generated constructor stub
//		biGraph = biG;
//		leftNodeKeys = lKeys;
//		rightNodeKeys = rKeys;
//		xNyN = new int[gMaxKey];
//		check = new boolean[gMaxKey];
//	}
//	
//	public void mMatching() {  
//		Arrays.fill(xNyN, INF); 
//	    for(int i : leftNodeKeys){
//	        if(INF == xNyN[i]){
//	        	Arrays.fill(check, false);
//	            if(DFS(i)){
//	                matchNum++;
////	                System.out.println(matchNum);
//	            }
//	        }
//	    }
//	}
//
//	private boolean DFS(int node) {
//		// TODO Auto-generated method stub
//		if(biGraph.containsKey(node)){
//			for(int uOut : biGraph.get(node)){
//				if(!check[uOut]){
//					check[uOut] = true;
//					if(xNyN[uOut] == INF || DFS(xNyN[uOut])){
//						xNyN[uOut] = node;
//						xNyN[node] = uOut;
//						return true;
//					}
//				}
//			}
//		}	
//
//		return false;
//	}
//	
//
//	
//}


//private class Dijkstra {
//	static final int INF = Integer.MAX_VALUE/2;
//	// Dijkstra's algorithm to find shortest path from s to all other nodes
//	Map<Integer,ArrayList<Integer>> graph = new HashMap<Integer,ArrayList<Integer>>();
//	Map<String,Integer> edgeCost = new HashMap<String,Integer>();
////	int maxKey;
//	Set<Integer> nodeKeys = new HashSet<Integer>();//all the keys of nodes in graph
////	Map<Integer,Integer> dist = new HashMap<Integer,Integer>();
//	int[] dist;
////	Map<Integer,Integer> preD = new HashMap<Integer,Integer>();
//	int[] preD;
//	int source;
//	int target;
//	
//	public Dijkstra(Map<Integer, ArrayList<Integer>> flowG, Map<String, Integer> cost, Set<Integer> nKeys, int s, int t, int gMaxKey) { 
//		// TODO Auto-generated constructor stub
//		graph = flowG;
//		edgeCost = cost;
//		nodeKeys = nKeys;
//		source = s;
//		target = t;
////		maxKey = maxKeyFG;
//		dist = new int[gMaxKey];
//		preD = new int[gMaxKey];
//	}
//
//	// use adjacent list
//    public void dijkstra() {    	 
////   	 	int nodeNum = maxKey;
//   	 	
//   	 	int min = 0;
//   	 	int nextNode = 0;
//	   	// initiate parameters
//   	 	
//		Arrays.fill(dist, INF);
//		Arrays.fill(preD, INF);
//		dist[source] = 0;
//		preD[source] = source;
//		for(int sourceConnect : graph.get(source)){
//			dist[sourceConnect] = edgeCost.get(source+":"+sourceConnect);
//			preD[sourceConnect] = source;
//		}
//
//	 	Set<Integer> unVisited = new HashSet<Integer>();
//	 	for(int i : nodeKeys){
//	 		unVisited.add(i);
//	 	}
//	 	unVisited.remove(source);
//		
////   	 	Set<Integer> visited = new HashSet<Integer>();
////   	 	visited.add(source);
//
//   	 	Queue<Integer> unvisitedDist0 = new LinkedList<Integer>();  
//	   	
//	   	boolean flagEndwhile = true;
//	   	while(flagEndwhile){
//	   		min = INF;	    	 
//		   	boolean flag = false;
//		   	
//		   	if(!unvisitedDist0.isEmpty()){
//		   		nextNode = unvisitedDist0.poll();
//		   		min = dist[nextNode];
//		   		flag = true;
//		   	}else{
//		   		for(int j:unVisited){
//		   			if(min > dist[j]) {
//		   				min = dist[j];
//		   				nextNode = j;
//		   				flag = true;
//		   				if(min == 0)
//		   					break;
//		   			}
//		   		}
//		   	}
//	   		if(!flag){//break if no more unvisited node can be reached
//	   			break;
//	   		}
////	   		visited.add(nextNode);
//	   		unVisited.remove(nextNode);
//	   		
//	   		if(graph.containsKey(nextNode))
//		   		for(int k:graph.get(nextNode)){
//		   			
//		   			if(dist[k] != 0 && unVisited.contains(k) && min+edgeCost.get(nextNode+":"+k) < dist[k]){ 
//		   				dist[k] = min+edgeCost.get(nextNode+":"+k);
//		   				preD[k] = nextNode;
//		   				
//		   				if(dist[k] == 0){
//		   					unvisitedDist0.add(k);
//		   				}
//		   				
//		   			}
//		   			
//		   		}
//	   		
//	   	}  
//    }	
//}
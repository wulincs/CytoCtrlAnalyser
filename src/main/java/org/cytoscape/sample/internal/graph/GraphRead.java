package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.cytoscape.sample.internal.Network;

public class GraphRead {
	Network graph = new Network();
	//key integer is the No.of a scc, ArrayList are the nodes in that scc
	public Map<Integer,ArrayList<Integer>> sccComponent = new HashMap<Integer,ArrayList<Integer>>();
	//record the source and sink SCC
	public ArrayList<Integer> sourceSCC = new ArrayList<Integer>();
	public ArrayList<Integer> sinkSCC = new ArrayList<Integer>();
	public ArrayList<Integer> singleSCC = new ArrayList<Integer>();
	int allMatchableRootSCC = 0;
	//parameters in searching
	Boolean[] flag;
	Integer[] numb;
	int isolatedNode = 0; //nodes without incoming and outgoing edge
	int sig = 0;
//	Set<Integer> SCC = new HashSet<Integer>();
//	int recur = 0;//recursion depth
	public GraphRead(Network g){
		this.graph = g;
		flag = new Boolean[g.maxKey+1];
		Arrays.fill(flag, false);
		numb = new Integer[g.nodeNum];
	}
	public void findSCC(){
		stepOne();
		stepTwo();
		getSourceSCC();
//		System.out.println("Number of Nodes: " + graph.countNode);
//		System.out.println("Number of Edges: " + graph.countEdge);
//		System.out.println("Number of sourceSCC: " + sourceSCC.size());
//		System.out.println("Number of sinkSCC: " + sinkSCC.size());
//		System.out.println("Number of singleSCC: " + singleSCC.size());
	}

	private void getSourceSCC() {
		// TODO Auto-generated method stub
		//i SCCs
		//k,j nodes in one scc
		//m edges in one node
		for(int i = 1, len = sccComponent.size(); i <= len; ++i){
			boolean out = false;//if true, not a sink
			boolean in = false;// if true, not a source
			Set<Integer> nodesInSCC = new HashSet<Integer>();// put all the node in one scc to a set
			for(int k = 0, lenk = sccComponent.get(i).size(); k < lenk; ++k)
				nodesInSCC.add(sccComponent.get(i).get(k));
			for(int j = 0, lenj = sccComponent.get(i).size(); j < lenj ;++j){//check edges of each node in scc
				int tempNodeNo = sccComponent.get(i).get(j);
				//check each node in scc, whether it direct to other scc
				if(!out && graph.adjList.containsKey(tempNodeNo)){
					for(int m = 0, lenm = graph.adjList.get(tempNodeNo).size(); m < lenm; ++m){//edges of one scc
						if(!nodesInSCC.contains(graph.adjList.get(tempNodeNo).get(m))){
							out = true;
							break;
						}
					}
				}
				//check each node in scc, whether if other scc into this
				if(!in && graph.revAdjList.containsKey(tempNodeNo)){
					for(int m = 0, lenm = graph.revAdjList.get(tempNodeNo).size(); m < lenm; ++m){//edges of one scc
						if(!nodesInSCC.contains(graph.revAdjList.get(tempNodeNo).get(m))){
							in = true;
							break;
						}
					}
				}
			}
			if(in && !out){
				sinkSCC.add(i);
			}else if(!in && out){
				sourceSCC.add(i);
			}else if(!in && !out){
				singleSCC.add(i);
			}
		}
//		System.out.print("s");
	}
	
	private void stepOne() {
		Set<Integer> node = new HashSet<Integer>();
		node = graph.adjList.keySet();
		Iterator<Integer> iter = node.iterator();
		// TODO Auto-generated method stub
		for(int i = 0; i < node.size(); ++i){
			int j = iter.next();
			if (!flag[j-1]){
				visitOne(j);
			}
		}
	}
	
	private void stepTwo() {
		// TODO Auto-generated method stub
		for(int i = 0; i < flag.length; ++i)
			flag[i] = false;
		sig = 0;
		for(int i = numb.length-1; i >= 0; --i){
			if(numb[i] == null){
				isolatedNode++;
				continue;
			}
			if(!flag[numb[i]-1]){
				++sig;
				visitTwo(numb[i]);
			}
		}
//		System.out.println("IsolatedNodeNum:"+isolatedNode);
	}
	
	private void visitTwo(int cur) {
		// TODO Auto-generated method stub
		flag[cur-1] = true;
		if(sccComponent.containsKey(sig)){
			sccComponent.get(sig).add(cur);
		}else{
			ArrayList<Integer> newNode = new ArrayList<Integer>();
			newNode.add(cur);
			sccComponent.put(sig, newNode);
		}
		if(graph.revAdjList.containsKey(cur)){
			for(int i = 0, j = graph.revAdjList.get(cur).size(); i < j; ++i){
				int adjCur = graph.revAdjList.get(cur).get(i);
				if(!flag[adjCur-1])
					visitTwo(adjCur);
			}	
		}
	}
	//DFS for one node
	private void visitOne(int cur){
	    flag[cur-1] = true;
	    if(graph.adjList.containsKey(cur)){
	    	for(int i = 0, j = graph.adjList.get(cur).size(); i < j; ++i){
		    	int adjCur = graph.adjList.get(cur).get(i);
		    	if(!flag[adjCur-1]){
//		    		System.out.println(++recur);//show recursion depth
		    		visitOne(adjCur);
//		    		System.out.println(--recur);
		    	}
		    }	
	    }

	    numb[sig] = cur;
	    ++sig;
	}
	
	public void findAllMatchableRootSCC(){
		// all the root scc index
		Set<Integer> rootSCC = new HashSet<Integer>();
		for(int sccNum : sourceSCC){
			rootSCC.add(sccNum);
		}
		for(int sccNum : singleSCC){
			rootSCC.add(sccNum);
		}
		//for each root scc, max matching
		for(int iSCC : rootSCC){
//			System.out.println("sourceSCCSize:"+sccComponent.get(iSCC).size());
			// create a bipartite graph
			Map<Integer,ArrayList<Integer>> biGraph = new HashMap<Integer,ArrayList<Integer>>();
			Map<Integer,Integer> nodeIndexInSCC = new HashMap<Integer,Integer>();
			int nodeCount = 0;
			Set<Integer> leftNodeKeys = new HashSet<Integer>();//all the keys of left part nodes
			Set<Integer> rightNodeKeys = new HashSet<Integer>();
			for(int sccNode : sccComponent.get(iSCC)){
				nodeCount++;
				nodeIndexInSCC.put(sccNode, nodeCount);
			}
			for(int node:nodeIndexInSCC.keySet()){
				if(graph.revAdjList.containsKey(node)){
					ArrayList<Integer> outList = new ArrayList<Integer>();
					outList.add(-9999);
					for(Integer outT : graph.revAdjList.get(node)){
						leftNodeKeys.add(nodeIndexInSCC.get(outT));
						// if right node is in the root scc
						if(nodeIndexInSCC.containsKey(outT)){ 
							outList.add(nodeIndexInSCC.get(outT)+nodeCount+1);
						}
					}
					outList.remove(0);
					biGraph.put(nodeIndexInSCC.get(node), outList);
				}
			}
			for(int i : leftNodeKeys){
				rightNodeKeys.add(i+nodeCount+1);
			}
			MaxMatching mm = new MaxMatching(biGraph,leftNodeKeys,rightNodeKeys,2*nodeCount+2);
			mm.mMatching();
			if(mm.matchNum == nodeCount){
				allMatchableRootSCC++;
			}
		}
		System.out.println("allMatchableRootSCC:"+allMatchableRootSCC);
	}
}
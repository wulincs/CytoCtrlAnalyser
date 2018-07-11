package org.cytoscape.sample.internal.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


//for state transition use only
public class FindSCC {
//	FileInput graph = new FileInput();
	//key integer is the No.of a scc, ArrayList are the nodes in that scc
	public Map<Integer,ArrayList<Integer>> sccComponent = new HashMap<Integer,ArrayList<Integer>>();
	//record the source and sink SCC
	public ArrayList<Integer> sourceSCC = new ArrayList<Integer>();
	public ArrayList<Integer> sinkSCC = new ArrayList<Integer>();
	public ArrayList<Integer> singleSCC = new ArrayList<Integer>();
	//parameters in searching
	Boolean[] flag;
	Integer[] numb;
	int sig = 0;
	int[][] adjMatrix;
//	Set<Integer> SCC = new HashSet<Integer>();
//	int recur = 0;//recursion depth
	int nodeSize;
	public FindSCC(int[][] adjM){
//		this.graph = g;
		adjMatrix = adjM;
		nodeSize = adjMatrix.length;
		flag = new Boolean[nodeSize];
		Arrays.fill(flag, false);
		numb = new Integer[nodeSize];
	}
	public void findSCC(){
		stepOne();
		stepTwo();
		getSourceSCC();
//		System.out.println("Number of sourceSCC: " + sourceSCC.size());
//		System.out.println("Number of sinkSCC: " + sinkSCC.size());
//		System.out.println("Number of singleSCC: " + singleSCC.size());
	}
	
	public int getSourceSCCNum(){
		return sourceSCC.size()+singleSCC.size();
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
				if(!out){
					for(int m = 0; m < nodeSize; ++m){//edges of one scc
						if(!nodesInSCC.contains(m) && 1 == adjMatrix[m][tempNodeNo]){
							out = true;
							break;
						}
					}
				}
				//check each node in scc, whether if other scc into this
				if(!in){
					for(int m = 0; m < nodeSize; ++m){//edges of one scc
						if(!nodesInSCC.contains(m) && 1 == adjMatrix[tempNodeNo][m]){
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
		// TODO Auto-generated method stub
		for(int i = 0; i < nodeSize; ++i){
			if (!flag[i]){
				visitOne(i);
			}
		}
	}
	
	private void stepTwo() {
		// TODO Auto-generated method stub
		Arrays.fill(flag, false);
		sig = 0;
		for(int i = nodeSize-1; i >= 0; --i){
			if(!flag[numb[i]]){
				++sig;
				visitTwo(numb[i]);
			}
		}
	}
	
	private void visitTwo(int cur) {
		// TODO Auto-generated method stub
		flag[cur] = true;
		if(sccComponent.containsKey(sig)){
			sccComponent.get(sig).add(cur);
		}else{
			ArrayList<Integer> newNode = new ArrayList<Integer>();
			newNode.add(cur);
			sccComponent.put(sig, newNode);
		}
//		if(graph.revAdjList.containsKey(cur)){
			for(int i = 0; i < nodeSize; ++i){
//				int adjCur = graph.revAdjList.get(cur).get(i);
				if(!flag[i] && 1 == adjMatrix[cur][i])
					visitTwo(i);
			}	
//		}
	}
	//DFS for one node
	private void visitOne(int cur){
	    flag[cur] = true;
//	    if(graph.adjList.containsKey(cur)){
	    	for(int i = 0; i < nodeSize; ++i){
//		    	int adjCur = graph.adjList.get(cur).get(i);
		    	if(!flag[i] && 1 == adjMatrix[i][cur]){
//		    		System.out.println(++recur);//show recursion depth
		    		visitOne(i);
//		    		System.out.println(--recur);
		    	}
		    }	
//	    }

	    numb[sig] = cur;
	    ++sig;
	}
}

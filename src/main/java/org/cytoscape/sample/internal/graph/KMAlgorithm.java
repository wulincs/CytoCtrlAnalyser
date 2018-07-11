package org.cytoscape.sample.internal.graph;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;



public class KMAlgorithm {
//	// the edges in bipartite graph
//	Map<Integer,ArrayList<Integer>> edgeInGraph = new HashMap<Integer,ArrayList<Integer>>();//edges with weight 1
//	Map<Integer,ArrayList<Integer>> edgeDeci = new HashMap<Integer,ArrayList<Integer>>();//edges with weight 0.0001
	public int nodeNumber;
	// the edges in bipartite graph
	Map<Integer,Set<Integer>> edgeInGraph = new HashMap<Integer,Set<Integer>>();//edges with weight 1
	public Map<Integer,Set<Integer>> revEdgeInGraph = new HashMap<Integer,Set<Integer>>();//reverse edges with weight 1
	//chosen nodes
	public Set<Integer> chosenNodes;
	// mediate parameters for the algorithm
	public boolean[] checkX;//node in X belong to tree
	boolean[] checkY;
	long[] lx;//weight of node in X
	long[] ly;//weight of node in Y
	public int[] yN;//node in X connected to yi 
	long[] slack;

//	int count = 0;
	//constructor
	public KMAlgorithm(FindSubGraph findSub) {
		// TODO Auto-generated constructor stub
		chosenNodes = findSub.chosenNodesSub;
		nodeNumber = findSub.nodeNumberSub;
		edgeInGraph = findSub.edgesSub;
		revEdgeInGraph = findSub.revEdgesSub;
	}

	public long km() {   //for steering node of state transition
		// TODO Auto-generated method stub
		init();
	    for(int u=0; u < nodeNumber; u++)
	    {
//	    	++count;
//	    	System.out.println(u);
	    	Arrays.fill(slack, Long.MAX_VALUE);
//	        for(int v=0; v <  nodeNumber; v++)    
//	        	slack[v] = Long.MAX_VALUE;
	        while(true)
	        {
	        	Arrays.fill(checkX, false);
	        	Arrays.fill(checkY, false);
	            if(dfs(u)) //find match then break
	            	break;	
	            long d = Long.MAX_VALUE;
	            for(int i = 0; i < nodeNumber; i++)    
	            	if(!checkY[i]){
		                d = Math.min(d,slack[i]);
		            }
//	            for(int i = 0; i <  nodeNumber; i++)    
//	            	if(checkX[i]){
//		                lx[i] -= d;
//		            }
	            for(int i = 0; i < nodeNumber; i++)
	            {
	            	if(checkX[i])
		                lx[i] -= d;
	                if(checkY[i])    
	                	ly[i] += d;
	                else    
	                	slack[i] -= d;
	            }
	        }
	    }
	    long ans = 0;
	    for(int i = 0; i < nodeNumber; i++){
	    	if(yN[i] != -1){
	    		ans += getEdgeWeight(i+1, yN[i]+1);
	    	}
	    }
	    return ans;
	}

	private boolean dfs(int u) {
		// TODO Auto-generated method stub
		checkX[u] = true;
		if(chosenNodes.contains(u+1)){
			if(revEdgeInGraph.containsKey(u+1)){
				int[] tempE = new int[nodeNumber];
				for(int chN : revEdgeInGraph.get(u+1)){
					tempE[chN-1] = 1;
				}
				for(int v = 0; v < nodeNumber; v++)    
					if(!checkY[v]){
						long t = lx[u] + ly[v] - getEdgeWeightStartChosenA(u, v, tempE);
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
			            continue;
			        }
			        checkY[v] = true;
			        if(yN[v] == -1 || dfs(yN[v])){
			            yN[v] = u;
			            return true;
			        }
				}
			}else{
				for(int v = 0; v < nodeNumber; v++)    
					if(!checkY[v]){
						long t = lx[u] + ly[v] - 1;
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
			            continue;
			        }
			        checkY[v] = true;
			        if(yN[v] == -1 || dfs(yN[v])){
			            yN[v] = u;
			            return true;
			        }
				}
			}
		}else{
			if(revEdgeInGraph.containsKey(u+1)){
				int[] tempE = new int[nodeNumber];
				for(int chN : revEdgeInGraph.get(u+1)){
					tempE[chN-1] = 1;
				}
				for(int v = 0; v <  nodeNumber; v++)    
					if(!checkY[v] && (tempE[v] == 1 || v ==u)){
						long t = lx[u] + ly[v] - getEdgeWeightStartChosenB(u, v, tempE);
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
			            continue;
			        }
			        checkY[v] = true;
			        if(yN[v] == -1 || dfs(yN[v])){
			            yN[v] = u;
			            return true;
			        }
				}
			}else{
//				for(int v = 0; v <  nodeNumber; v++)    
				int v = u;
				if(!checkY[v]){
//					long t = lx[u] + ly[v] - getEdgeWeightStartChosenC(u, v);
					long t = lx[u] + ly[v] - 0;
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
//				            continue;
			        }else{
				        checkY[v] = true;
				        if(yN[v] == -1 || dfs(yN[v])){
				            yN[v] = u;
				            return true;
				        }
			        }
				}
			}
		}
		return false;
	}
	public long getEdgeWeight(int start, int end){
		if(edgeInGraph.containsKey(start) && edgeInGraph.get(start).contains(end)){
			return 100000;
		}else if(chosenNodes.contains(end)){
			return 1;
		}else if(start == end){
			return 0;
		}else{
			return -nodeNumber*100000;
		}
	}
	private long getEdgeWeightStartChosenA(int start, int end, int[] tmpM){
		if(1 == tmpM[end]){
			return 100000;
		}else{
			return 1;
		}
	}
	private long getEdgeWeightStartChosenB(int start, int end, int[] tmpM){
		if(1 == tmpM[end]){
			return 100000;
		}else if(start == end){
			return 0;
		}else{
			return -nodeNumber*100000;
		}
	}
	private long getEdgeWeightStartChosenC(int start, int end) {
		if(start == end){
			return 0;
		}else{
			return -nodeNumber*100000;
		}
	}
	private void init() { 
		// TODO Auto-generated method stub
		lx = new long[ nodeNumber];
		ly = new long[ nodeNumber];
		checkX = new boolean[ nodeNumber];
		checkY = new boolean[ nodeNumber];
		yN = new int[ nodeNumber];
		slack = new long[ nodeNumber];
		Arrays.fill(lx, 0);
		Arrays.fill(ly, 0);
		Arrays.fill(yN, -1);
		for(int i=0 ; i <  nodeNumber; i++){
//	        lx[i] = ly[i] = 0;
//	        yN[i] = -1;
	        for(int j = 0; j <  nodeNumber; j++){
	            lx[i] = Math.max(lx[i], getEdgeWeight(j+1, i+1));
	        }
	    }
	}
	//for control centrality**************************************************************
	//algorithm based on control centrality paper
	public long kmNew() {
		// TODO Auto-generated method stub
		initNew();
	    for(int u=0; u < nodeNumber; u++)
	    {
	    	Arrays.fill(slack, Long.MAX_VALUE);
	        while(true)
	        {
	        	Arrays.fill(checkX, false);
	        	Arrays.fill(checkY, false);
	            if(dfsNew(u)) //find match then break
	            	break;	
	            long d = Long.MAX_VALUE;
	            for(int i = 0; i < nodeNumber; i++)    
	            	if(!checkY[i]){
		                d = Math.min(d,slack[i]);
		            }
	            for(int i = 0; i < nodeNumber; i++)
	            {
	            	if(checkX[i])
		                lx[i] -= d;
	                if(checkY[i])    
	                	ly[i] += d;
	                else    
	                	slack[i] -= d;
	            }
	        }
	    }
	    long ans = 0;
	    for(int i = 0; i < nodeNumber; i++){
	    	if(yN[i] != -1){
	    		ans += getEdgeWeightNew(i+1, yN[i]+1);
	    	}
	    }
	    return ans;
	}
	private void initNew() { 
		// TODO Auto-generated method stub
		lx = new long[ nodeNumber];
		ly = new long[ nodeNumber];
		checkX = new boolean[ nodeNumber];
		checkY = new boolean[ nodeNumber];
		yN = new int[ nodeNumber];
		slack = new long[ nodeNumber];
		Arrays.fill(lx, 0);
		Arrays.fill(ly, 0);
		Arrays.fill(yN, -1);
//		for(int i=0 ; i <  nodeNumber; i++){
//	        for(int j = 0; j <  nodeNumber; j++){
//	            lx[i] = Math.max(lx[i], getEdgeWeightNew(j+1, i+1));
//	        }
//	    }
		//special case for initial setting, improve the performance
		for(int i=0 ; i <  nodeNumber-1; i++){
			lx[i] = 1;
		}
	}
	private boolean dfsNew(int u) {
		// TODO Auto-generated method stub
		checkX[u] = true;
		//if u is the control node
		if(u+1 == nodeNumber){
			for(int v = 0; v < nodeNumber; v++){
				if(!checkY[v]){
					long t = lx[u] + ly[v];// lx[u] + ly[v] - 0
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
			            continue;
			        }
			        checkY[v] = true;
			        if(yN[v] == -1 || dfsNew(yN[v])){
			            yN[v] = u;
			            return true;
			        }
				}
			}
		}else{
			Set<Integer> newSet = new HashSet<Integer>();
			boolean hasSelfLoop = false;
			for(int v : revEdgeInGraph.get(u+1)){
				--v;
				if(v == u){
					hasSelfLoop = true;
				}
				newSet.add(v);
			}
			if(!hasSelfLoop){
				newSet.add(u);
			}
			for(int v : newSet){
				if(!checkY[v]){
					long t = lx[u] + ly[v] - getEdgeWeightNew(v+1, u+1);
			        if(t > 0){
			            slack[v] = Math.min(slack[v],t);
			            continue;
			        }
			        checkY[v] = true;
			        if(yN[v] == -1 || dfsNew(yN[v])){
			            yN[v] = u;
			            return true;
			        }
				}
			}
		}
//		//check as adjacent matrix, too slow, changed by adjacent list
//		for(int v = 0; v < nodeNumber; v++){
//			if(!checkY[v]){
//				long t = lx[u] + ly[v] - getEdgeWeightNew(v+1, u+1);
//		        if(t > 0){
//		            slack[v] = Math.min(slack[v],t);
//		            continue;
//		        }
//		        checkY[v] = true;
//		        if(yN[v] == -1 || dfsNew(yN[v])){
//		            yN[v] = u;
//		            return true;
//		        }
//			}
//		}
		return false;
	}
	public long getEdgeWeightNew(int start, int end){
		if(edgeInGraph.containsKey(start) && edgeInGraph.get(start).contains(end)){
			return 1;
		}else if(nodeNumber == end || start == end){
			return 0;
		}else{
			return -nodeNumber*100000;
		}
	}
	//end for control centrality
	
	//for output controllability **************************************************************
	//algorithm based on output controllability paper
	public void kmOC() {
		// TODO Auto-generated method stub
		initOC();
	    for(int u=0; u < checkX.length; u++)
	    {
//	    	++count;
	    	System.out.println(u);
	    	Arrays.fill(slack, Long.MAX_VALUE);
	        while(true)
	        {
	        	Arrays.fill(checkX, false);
	        	Arrays.fill(checkY, false);
	            if(dfsOC(u)) //find match then break
	            	break;	
	            long d = Long.MAX_VALUE;
	            for(int i = 0; i < slack.length; i++)    
	            	if(!checkY[i]){
		                d = Math.min(d,slack[i]);
		            }
	            for(int i = 0; i < checkX.length; i++)    
	            	if(checkX[i]){
		                lx[i] -= d;
		            }
	            for(int i = 0; i < checkY.length; i++)
	            {
	                if(checkY[i])    
	                	ly[i] += d;
	                else    
	                	slack[i] -= d;
	            }
	        }
	    }
//	    long ans = 0;
//	    for(int i = 0; i < checkY.length; i++){
//	    	if(yN[i] != -1){
//	    		ans += getEdgeWeight(yN[i],i);
////	    		System.out.println(i+"  "+ans);
//	    		if(i >= checkX.length){
//	    			steeringNodeSet.add(yN[i]+1);
//	    		}
//	    	}
//	    }
//	    return ans;
	}
	
	private boolean dfsOC(int u) {
//    	System.out.println(count);
		// TODO Auto-generated method stub
		checkX[u] = true;
		for(int v = 0; v < checkY.length; v++)    
			if(!checkY[v]){
				long t = lx[u] + ly[v] - getEdgeWeightOC(u,v);
		        if(t > 0){
		            slack[v] = Math.min(slack[v],t);
		            continue;
		        }
		        checkY[v] = true;
		        if(yN[v] == -1 || dfsOC(yN[v])){
		            yN[v] = u;
		            return true;
		        }
			}
		return false;
	}	
	
	public long getEdgeWeightOC(int left, int right){
		if(right >= nodeNumber){//u' nodes
			if(right - nodeNumber == left){//corresponding candidate input nodes
				if(chosenNodes.contains(left+1)){//left node is an output node
					return 1;
				}else{
					return 1-100000;
				}
			}else{// no edge between
				return -nodeNumber*100000;
			}
		}else{//V_C nodes
			if(chosenNodes.contains(left+1)){//left node is an output node
				if(edgeInGraph.containsKey(right+1) && edgeInGraph.get(right+1).contains(left+1)){
					return 100000;
				}else{
					return -nodeNumber*100000;
				}
			}else{
				if((edgeInGraph.containsKey(right+1) && edgeInGraph.get(right+1).contains(left+1)) || right == left){
					return 0;
				}else{
					return -nodeNumber*100000;
				}
			}
			
		}
		
	}
	private void initOC() { 
		// TODO Auto-generated method stub
		lx = new long[ nodeNumber];
		ly = new long[ 2*nodeNumber];
		checkX = new boolean[ nodeNumber];
		checkY = new boolean[ 2*nodeNumber];
		yN = new int[ 2*nodeNumber];
		slack = new long[ 2*nodeNumber];
		Arrays.fill(lx, 0);
		Arrays.fill(ly, 0);
		Arrays.fill(yN, -1);
		for(int i=0 ; i <  nodeNumber; i++){
//	        lx[i] = ly[i] = 0;
//	        yN[i] = -1;
	        for(int j = 0; j <  2*nodeNumber; j++){
	            lx[i] = Math.max(lx[i], getEdgeWeightOC(i, j));
	        }
	    }
	}
	//end for output controllability
	
}


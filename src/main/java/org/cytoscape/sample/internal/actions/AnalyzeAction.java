package org.cytoscape.sample.internal.actions;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JOptionPane;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.events.SetCurrentNetworkEvent;
import org.cytoscape.application.events.SetCurrentNetworkListener;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedEvent;
import org.cytoscape.application.swing.events.CytoPanelComponentSelectedListener;
import org.cytoscape.model.CyEdge;
import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.model.CyRow;
import org.cytoscape.model.CyTable;
import org.cytoscape.model.events.AddedEdgesEvent;
import org.cytoscape.model.events.AddedEdgesListener;
import org.cytoscape.model.events.AddedNodesEvent;
import org.cytoscape.model.events.AddedNodesListener;
import org.cytoscape.model.events.RemovedEdgesEvent;
import org.cytoscape.model.events.RemovedEdgesListener;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.sample.internal.Network;
import org.cytoscape.sample.internal.Node;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.algorithm.Algorithm;
import org.cytoscape.sample.internal.algorithm.ClassificationByMDS;
import org.cytoscape.sample.internal.algorithm.ControlCapacity;
import org.cytoscape.sample.internal.algorithm.ControlCentrality;
import org.cytoscape.sample.internal.algorithm.MDS;
import org.cytoscape.sample.internal.algorithm.MSS;
import org.cytoscape.sample.internal.algorithm.MSSWithPreference;
import org.cytoscape.sample.internal.algorithm.OutputControl;
import org.cytoscape.sample.internal.algorithm.ProbabilityInMSS;
import org.cytoscape.sample.internal.algorithm.StateTransition;
import org.cytoscape.sample.internal.tasks.AnalyzeTask;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskIterator;
import org.cytoscape.work.TaskManager;

public class AnalyzeAction extends AbstractPAction implements SetCurrentNetworkListener, AddedNodesListener,
		AddedEdgesListener, RemovedEdgesListener, RemovedNodesListener, CytoPanelComponentSelectedListener {
	
	private final CyServiceRegistrar registrar;
	private final TaskManager taskManager;
	private TaskIterator taskIterator;
	private Map<Long, Boolean>  dirtyNetworks;
//	private ResultPanel resultsPanel;
	
	// Action when the analyze buttom is pressed
    public AnalyzeAction(String title, CyApplicationManager applicationManager, CySwingApplication swingApplication, 
    		CyNetworkViewManager netViewManager, CyServiceRegistrar registrar, 
    		TaskManager taskManager){
		super(title, applicationManager, swingApplication, netViewManager, "network");
//		analyze = 0;
		this.registrar = registrar;
		this.taskManager = taskManager;
		this.dirtyNetworks = new HashMap();
	}
	
//	public AnalyzeAction(String name) {
//		super(name, applicationManager, swingApplication, netViewManager, "network");  
//		// TODO Auto-generated constructor stub
//	}
    
    //action when press the "analyze" button
    public void actionPerformed(ActionEvent event) {
//    	JOptionPane.showMessageDialog(null, "yahoo", "oyeah", JOptionPane.ERROR_MESSAGE);
    	final CyNetwork cyNetwork = applicationManager.getCurrentNetwork();
    	ArrayList<Algorithm> alg = new ArrayList<Algorithm>();
    	final ArrayList<String> alg2 = new ArrayList<String>();
    	
    	//pre-processing*******************************
    	//add all nodes to allNodes
    	// create a network object for all nodes
		ArrayList<Node> allNodes = new ArrayList<Node>();
		Network network = new Network();
		int i = 1;//node number: from 1 to N
		for(CyNode cyNodeI :cyNetwork.getNodeList()){
			Node nodeI = new Node(cyNodeI, cyNetwork);
			nodeI.setId(i);
			allNodes.add(nodeI);
			network.nodeKeyMap.put(nodeI, i);
			network.revNodeKeyMap.put(i, nodeI);
			network.cyNodeKeyMap.put(cyNodeI, i);
			network.revCyNodeKeyMap.put(i, cyNodeI);
			i++;
		}
		//Create adjacent list for network		
		for(CyEdge edge: cyNetwork.getEdgeList()){
			int startNode = network.cyNodeKeyMap.get(edge.getSource());
			int endNode = network.cyNodeKeyMap.get(edge.getTarget());
			if(network.adjList.containsKey(startNode)){
				network.adjList.get(startNode).add(endNode);
			}else{
				ArrayList<Integer> newNode = new ArrayList<Integer>();
				newNode.add(endNode);
				network.adjList.put(startNode, newNode);
			}
			if(network.revAdjList.containsKey(endNode)){
				network.revAdjList.get(endNode).add(startNode);
			}else{
				ArrayList<Integer> newNode = new ArrayList<Integer>();
				newNode.add(startNode);
				network.revAdjList.put(endNode, newNode);
			}			
		}
		network.nodeKeys = network.revNodeKeyMap.keySet();
		network.edgeNum = cyNetwork.getEdgeCount();
		network.nodeNum = network.nodeKeys.size();
		network.maxKey = network.nodeKeys.size();
		//end of pre-processing*************************************************
		
		//check algorithms to be executed
    	for(Entry<String, Boolean> e :ParameterSet.algorithmSet.entrySet()){	
    		if(e.getValue().equals(true)){
    			if(e.getKey().equals(ParameterSet.MDS)){               				
    				alg.add(new MDS(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.MDS);		
    			}else if(e.getKey().equals(ParameterSet.MSS)){
        			alg.add(new MSS(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.MSS);
        		}else if(e.getKey().equals(ParameterSet.MP)){
        			alg.add(new MSSWithPreference(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.MP);
        		}else if(e.getKey().equals(ParameterSet.ST)){
        			alg.add(new StateTransition(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.ST);
        		}else if(e.getKey().equals(ParameterSet.CCap)){
        			alg.add(new ControlCapacity(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.CCap);
        		}else if(e.getKey().equals(ParameterSet.CCen)){
        			alg.add(new ControlCentrality(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.CCen);
        		}else if(e.getKey().equals(ParameterSet.ClfMDS)){
        			alg.add(new ClassificationByMDS(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.ClfMDS);
        		}else if(e.getKey().equals(ParameterSet.OC)){
        			alg.add(new OutputControl(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.OC);
        		}else if(e.getKey().equals(ParameterSet.probInMSS)){
        			alg.add(new ProbabilityInMSS(cyNetwork, allNodes, network));
    				alg2.add(ParameterSet.probInMSS);
        		}
    		}
    	}
    	if(taskIterator != null){
			taskIterator.append(new AnalyzeTask(cyNetwork, alg, alg2));
    	}else{
			taskIterator = new TaskIterator(new AnalyzeTask(cyNetwork, alg, alg2));
    	}
		this.taskManager.execute(taskIterator);
    	
    	
    }
    
    
    
	@Override
	public void handleEvent(CytoPanelComponentSelectedEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(RemovedNodesEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(RemovedEdgesEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(AddedEdgesEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(AddedNodesEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(SetCurrentNetworkEvent arg0) {
		// TODO Auto-generated method stub

	}

}

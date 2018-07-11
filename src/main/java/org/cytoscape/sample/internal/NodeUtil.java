package org.cytoscape.sample.internal;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.model.CyNetworkManager;
import org.cytoscape.model.events.RemovedNodesEvent;
import org.cytoscape.model.events.RemovedNodesListener;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;

public class NodeUtil implements RemovedNodesListener {
	
	private final CyApplicationManager applicationMgr;
	private final CyNetworkViewManager networkViewMgr;
	private final CyServiceRegistrar registrar;
	public NodeUtil(CyApplicationManager appMgr, CyNetworkViewManager netViewMgr, CyServiceRegistrar serviceRegistrar) {
		// TODO Auto-generated constructor stub
		applicationMgr = appMgr;
		networkViewMgr = netViewMgr;
		registrar = serviceRegistrar;
	}
	@Override
	public void handleEvent(RemovedNodesEvent e) {
		// TODO Auto-generated method stub
		
	}

}

package org.cytoscape.sample.internal.actions;

import java.awt.event.ActionEvent;

import org.cytoscape.application.CyApplicationManager;
import org.cytoscape.application.swing.AbstractCyAction;
import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.view.model.CyNetworkViewManager;

public class AbstractPAction extends AbstractCyAction {
	protected final CySwingApplication swingApplication;
	protected final CyApplicationManager applicationManager;
	protected final CyNetworkViewManager netViewManager;
	
	public AbstractPAction(String name, CyApplicationManager applicationManager, CySwingApplication swingApplication, CyNetworkViewManager netViewManager, String enableFor)
	{
		super(name, applicationManager, enableFor, netViewManager);
		this.applicationManager = applicationManager;
		this.swingApplication = swingApplication;
		this.netViewManager = netViewManager;
	}
	
//	public AbstractPAction(String name) {
//		super(name);
//		// TODO Auto-generated constructor stub
//	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub

	}

}

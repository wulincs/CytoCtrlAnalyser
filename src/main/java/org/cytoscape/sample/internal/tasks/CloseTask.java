package org.cytoscape.sample.internal.tasks;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.sample.internal.panels.MainPanel;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class CloseTask implements Task {
	
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	
	public CloseTask(CySwingApplication swingApplication, CyServiceRegistrar registrar)
	{
	  this.swingApplication = swingApplication;
	  this.registrar = registrar;
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		
		//close the App in the left panel
		CytoPanel cytoPanel = swingApplication.getCytoPanel(CytoPanelName.WEST);
		for(int i = 0; i < cytoPanel.getCytoPanelComponentCount(); i++){
			if(cytoPanel.getComponentAt(i) instanceof MainPanel){
				this.registrar.unregisterService(cytoPanel.getComponentAt(i), CytoPanelComponent.class);
				return;
			}
		}		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub

	}

}

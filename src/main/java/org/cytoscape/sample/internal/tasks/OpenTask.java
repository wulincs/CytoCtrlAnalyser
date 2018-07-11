package org.cytoscape.sample.internal.tasks;

import java.util.Properties;


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanel;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.application.swing.CytoPanelState;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.sample.internal.panels.MainPanel;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class OpenTask implements Task{
	private final CySwingApplication swingApplication;
	private final CyServiceRegistrar registrar;
	private final CytoPanel cytoPanelWest;
	
	public OpenTask(CySwingApplication swingApplication, CyServiceRegistrar registrar)
	{
	  this.swingApplication = swingApplication;
	  this.registrar = registrar;
	  this.cytoPanelWest = swingApplication.getCytoPanel(CytoPanelName.WEST);
	}
	
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
		MainPanel mainPanel = new MainPanel(this.swingApplication);
		this.registrar.registerService(mainPanel, CytoPanelComponent.class, new Properties());
		//check if west panel is open
		if (cytoPanelWest.getState() == CytoPanelState.HIDE) {
			cytoPanelWest.setState(CytoPanelState.DOCK);
		}
		//select panel
		int index = cytoPanelWest.indexOfComponent(mainPanel);
		if (index == -1) {
			return;
		}
		cytoPanelWest.setSelectedIndex(index);
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

}

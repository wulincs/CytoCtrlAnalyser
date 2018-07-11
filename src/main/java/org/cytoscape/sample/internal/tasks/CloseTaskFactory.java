package org.cytoscape.sample.internal.tasks;


import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskFactory;
import org.cytoscape.work.TaskIterator;

public class CloseTaskFactory implements TaskFactory {
	  private final CySwingApplication swingApplication;
	  private final CyServiceRegistrar registrar;
	  
	  
	  public CloseTaskFactory(CySwingApplication swingApplication, CyServiceRegistrar registrar)
	  {
	    this.swingApplication = swingApplication;
	    this.registrar = registrar;
	  }
	  
	  
	@Override
	public TaskIterator createTaskIterator() {
		// TODO Auto-generated method stub
		return new TaskIterator(new Task[] { new CloseTask(swingApplication, this.registrar) });
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return true;
	}

}

package org.cytoscape.sample.internal;

import org.cytoscape.application.swing.CySwingApplication;

import org.cytoscape.sample.internal.actions.AnalyzeAction;
import org.cytoscape.sample.internal.tasks.CloseTaskFactory;
import org.cytoscape.sample.internal.tasks.OpenTaskFactory;
import org.cytoscape.application.CyApplicationManager;
import org.osgi.framework.BundleContext;

import org.cytoscape.service.util.AbstractCyActivator;
import org.cytoscape.service.util.CyServiceRegistrar;
import org.cytoscape.view.model.CyNetworkViewManager;
import org.cytoscape.work.TaskManager;

import java.util.Properties;


public class CyActivator extends AbstractCyActivator {
	public CyActivator() {
		super();
	}


	public void start(BundleContext bc) {
		ParameterSet.bc = bc;
		CySwingApplication swingApp = getService(bc,CySwingApplication.class);
		CyServiceRegistrar serviceRegistrar = getService(bc, CyServiceRegistrar.class);
		CyApplicationManager appMgr = (CyApplicationManager)getService(bc, org.cytoscape.application.CyApplicationManager.class);
		CyNetworkViewManager netViewMgr = (CyNetworkViewManager)getService(bc, org.cytoscape.view.model.CyNetworkViewManager.class);
		TaskManager taskMgr = (TaskManager)getService(bc, org.cytoscape.work.TaskManager.class);
		
		NodeUtil nodeUtil = new NodeUtil(appMgr, netViewMgr,serviceRegistrar);

		
		
		AnalyzeAction analyzeAction = new AnalyzeAction("Analyze", appMgr, swingApp, netViewMgr, serviceRegistrar, taskMgr);
		ParameterSet.analyzeAction = analyzeAction;
		
		
		//open button in menu
		OpenTaskFactory openTaskFactory = new OpenTaskFactory(swingApp, serviceRegistrar);
		Properties openTaskFactoryProps = new Properties();
		openTaskFactoryProps.setProperty("preferredMenu", "Apps.CytoCtrlAnalyser");
		openTaskFactoryProps.setProperty("title", "Open");
		openTaskFactoryProps.setProperty("menuGravity", "1.0");
		registerService(bc, openTaskFactory, org.cytoscape.work.TaskFactory.class, openTaskFactoryProps);
		//close button in menu
		CloseTaskFactory closeTaskFactory = new CloseTaskFactory(swingApp, serviceRegistrar);
		Properties closeTaskFactoryProps = new Properties();
		closeTaskFactoryProps.setProperty("preferredMenu", "Apps.CytoCtrlAnalyser");
		closeTaskFactoryProps.setProperty("title", "Close");
		closeTaskFactoryProps.setProperty("menuGravity", "1.0");				
		registerService(bc, closeTaskFactory, org.cytoscape.work.TaskFactory.class, closeTaskFactoryProps);
		
		
//		registerService(bc, closeTaskFactory, org.cytoscape.model.events.NetworkAboutToBeDestroyedListener.class, new Properties());
		
		
		//previous sample program
//		MyControlPanel myControlPanel = new MyControlPanel();
//		ControlPanelAction controlPanelAction = new ControlPanelAction(swingApp,myControlPanel);
//		registerService(bc,myControlPanel,CytoPanelComponent.class, new Properties());
//		registerService(bc,controlPanelAction,CyAction.class, new Properties());
	}
}


package org.cytoscape.sample.internal.panels;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JPanel;

import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;

public class ResultPanel extends JPanel implements CytoPanelComponent{

	
	
	
	
	
	
	
	
	
	
	
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.EAST;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "Result";
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}

package org.cytoscape.sample.internal.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.cytoscape.application.swing.CySwingApplication;
import org.cytoscape.application.swing.CytoPanelComponent;
import org.cytoscape.application.swing.CytoPanelName;
import org.cytoscape.sample.internal.ParameterSet;

public class MainPanel extends JScrollPane implements CytoPanelComponent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8820108031425225285L;
	private CySwingApplication desktopApp;
	private JPanel emPanel;
	private JPanel bottomPanel;
	ParameterSet currentParamsCopy; // store panel fields
	
	private JCheckBox MDSButton;
	private JCheckBox MSSButton;
	private JCheckBox probInMSS;
	private JCheckBox outputCtrlButton;
	private JCheckBox transitButton;
	private JCheckBox preferedMSSButton;
//	private JCheckBox preferedMSSButtonMin;
//	private JCheckBox preferedMSSButtonMax;
	private JCheckBox ctrlCapButton;//control capacity
	private JCheckBox ctrlCentralityButton;//
//	private JCheckBox ctrlProfileButton;
	private JCheckBox clfMDSButton;
	private JCheckBox selectAllButton;
	
	public MainPanel(CySwingApplication swingApplication){
		super();
		currentParamsCopy = new ParameterSet();
		this.desktopApp = swingApplication;
	    emPanel = new JPanel();
	    emPanel.setLayout(new BorderLayout());
	    JLabel lbXYZ = new JLabel("Please check the box for calculation:");
	    emPanel.add(lbXYZ,BorderLayout.NORTH);
	    
	    //add check box panel
	    emPanel.add(getCheckboxPanel(), BorderLayout.CENTER);
		
	      
		
//		emPanel.setPreferredSize(new Dimension(swingApplication.getCytoPanel(CytoPanelName.WEST).getSelectedComponent().getSize()));//get the size from current component
		emPanel.setPreferredSize(new Dimension(220,600));
		this.setViewportView(emPanel);
		this.setVisible(true);        
		
		
		
	 }
	
	
	@SuppressWarnings("unchecked")
	private JPanel getCheckboxPanel() {
		// TODO Auto-generated method stub
		if (this.bottomPanel == null) {
			
			//create the check box panel
		      this.bottomPanel = new JPanel();
		      setPreferredSize(new Dimension(270, 100));
		      this.bottomPanel.setLayout(new GridLayout(10,1));
		      
		      //add checkbox for selection    
		      
		      MDSButton = new JCheckBox("MDS");
		      this.bottomPanel.add(MDSButton);
		      
		      	      
		      ctrlCapButton = new JCheckBox("Control Capacity");
		      this.bottomPanel.add(ctrlCapButton);
		      
		      ctrlCentralityButton = new JCheckBox("Control Centrality");
		      this.bottomPanel.add(ctrlCentralityButton);
		      
//		      ctrlProfileButton = new JCheckBox("Control Profile");
//		      this.bottomPanel.add(ctrlProfileButton);
		      
		      clfMDSButton = new JCheckBox("Node classification");
		      this.bottomPanel.add(clfMDSButton);
		      
		      MSSButton = new JCheckBox("MSS");
		      this.bottomPanel.add(MSSButton);
		      
		      probInMSS = new JCheckBox("Probability in a random MSS");
		      this.bottomPanel.add(probInMSS);
		      
		      
		    //transitability
		      transitButton = new JCheckBox("Steering node for state transition");
		      this.bottomPanel.add(transitButton);
		      final JTextField transitionColumn = new JTextField("Input the column indicating nodes related to state transition please...");
		      ParameterSet.transitionColumn = transitionColumn; 
		      transitionColumn.addFocusListener(new FocusAdapter()
		        {
		            @Override
		            public void focusGained(FocusEvent e)
		            {
		            	if(transitionColumn.getText().matches("Input the column indicating nodes related to state transition please...")) 
		            		transitionColumn.setText("");
		            }
		            @Override
		            public void focusLost(FocusEvent e)
		            {
		            	if(transitionColumn.getText().isEmpty())
		            		transitionColumn.setText("Input the column indicating nodes related to state transition please...");
		            }
		        });
		      this.bottomPanel.add(transitionColumn);
		      
		      
		      //output control
		      outputCtrlButton = new JCheckBox("Output Control");
		      this.bottomPanel.add(outputCtrlButton);
		      final JTextField outputColumn = new JTextField("Input the column indicating the output nodes please...");
		      ParameterSet.outputColumn = outputColumn; 
		      outputColumn.addFocusListener(new FocusAdapter()
		        {
		            @Override
		            public void focusGained(FocusEvent e)
		            {
		            	if(outputColumn.getText().matches("Input the column indicating the output nodes please...")) 
		            		outputColumn.setText("");
		            }
		            @Override
		            public void focusLost(FocusEvent e)
		            {
		            	if(outputColumn.getText().isEmpty())
		            		outputColumn.setText("Input the column indicating the output nodes please...");
		            }
		        });
		      this.bottomPanel.add(outputColumn);
		      
		      
		      
		      
		      // MSS with preference
		      preferedMSSButton = new JCheckBox("MSS with preference");
		      this.bottomPanel.add(preferedMSSButton);
//		      JLabel label1 = new JLabel("Please input the column name for preference selection");
		      final JTextField preferenceColumn = new JTextField("Input the column as preference please...");
		      ParameterSet.preferenceColumn = preferenceColumn; 
		      preferenceColumn.addFocusListener(new FocusAdapter()
		        {
		            @Override
		            public void focusGained(FocusEvent e)
		            {
		            	if(preferenceColumn.getText().matches("Input the column as preference please...")) 
		            	preferenceColumn.setText("");
		            }
		            @Override
		            public void focusLost(FocusEvent e)
		            {
		            	if(preferenceColumn.getText().isEmpty())
		            		preferenceColumn.setText("Input the column as preference please...");
		            }
		        });
//		      this.bottomPanel.add(label1);
		      this.bottomPanel.add(preferenceColumn);
		      
//		      ParameterSet.getAlgorithmSet().put(ParameterSet.MDS, false);
		      


		      selectAllButton = new JCheckBox("Select all");
		      this.bottomPanel.add(selectAllButton);
		      
		      
		      MDSButton.addItemListener(new AlgorithmAction());
		      MSSButton.addItemListener(new AlgorithmAction());
		      probInMSS.addItemListener(new AlgorithmAction());
		      transitButton.addItemListener(new AlgorithmAction());
		      outputCtrlButton.addItemListener(new AlgorithmAction());
		      preferedMSSButton.addItemListener(new AlgorithmAction());
		      ctrlCapButton.addItemListener(new AlgorithmAction());
		      ctrlCentralityButton.addItemListener(new AlgorithmAction());
//		      ctrlProfileButton.addItemListener(new AlgorithmAction());
		      clfMDSButton.addItemListener(new AlgorithmAction());
		      selectAllButton.addItemListener(new AlgorithmAction());
		      
		      JButton button = new JButton(ParameterSet.analyzeAction);//add button for analyze action 
//		      JButton button = new JButton("Analyze");
//		        button.addActionListener(new ActionListener() {
//		            public void actionPerformed(ActionEvent arg0) {
////		                textField.setText(this.bottomPanel.getSelectedItem().toString());
//		            }
//		        });
		      button.setBounds(137, 10, 85, 25);
		      this.bottomPanel.add(button);
	    }

	    return this.bottomPanel;
	}

	/**
     * Handles the press of a algorithm option. Makes sure that appropriate options
     * inputs are added and removed depending on which algorithm is selected
     */
    private class AlgorithmAction implements ItemListener {
    	
    	public void itemStateChanged(ItemEvent e) {
            JCheckBox alg = (JCheckBox) e.getSource();
            if(alg.isSelected()){
            	if(alg.equals(MDSButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.MDS, true);
            	else if(alg.equals(MSSButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.MSS, true);
            	else if(alg.equals(transitButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.ST, true);
            	else if(alg.equals(outputCtrlButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.OC, true);
            	else if(alg.equals(preferedMSSButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.MP, true);
            	else if(alg.equals(ctrlCapButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.CCap, true);
            	else if(alg.equals(ctrlCentralityButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.CCen, true);   
            	else if(alg.equals(probInMSS))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.probInMSS, true); 
//            	else if(alg.equals(ctrlProfileButton))
//            		ParameterSet.getAlgorithmSet().put(ParameterSet.CP, true);
            	else if(alg.equals(clfMDSButton))
            		ParameterSet.getAlgorithmSet().put(ParameterSet.ClfMDS, true);
            	else if(alg.equals(selectAllButton)){
	        		MDSButton.setSelected(true);
	        		MSSButton.setSelected(true);
	        		transitButton.setSelected(true);
	        		outputCtrlButton.setSelected(true);
	        		preferedMSSButton.setSelected(true);
	        		ctrlCapButton.setSelected(true);
	        		ctrlCentralityButton.setSelected(true);
	        		probInMSS.setSelected(true);
//	        		ctrlProfileButton.setSelected(true);
	        		clfMDSButton.setSelected(true);
            	}
            		
            }
            else{
            	if(alg.equals(selectAllButton)){
            		selectAllButton.setSelected(false);
            		MDSButton.setSelected(false);
            		MSSButton.setSelected(false);
            		transitButton.setSelected(false);
            		outputCtrlButton.setSelected(false);
            		preferedMSSButton.setSelected(false);
            		ctrlCapButton.setSelected(false);
            		probInMSS.setSelected(false);
            		ctrlCentralityButton.setSelected(false);
//            		ctrlProfileButton.setSelected(false);
            		clfMDSButton.setSelected(false);
            	}else{
            		if(alg.equals(MDSButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.MDS, false);
                	else if(alg.equals(MSSButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.MSS, false);
                	else if(alg.equals(transitButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.ST, false);
                	else if(alg.equals(outputCtrlButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.OC, false);
                	else if(alg.equals(preferedMSSButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.MP, false);
                	else if(alg.equals(ctrlCapButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.CCap, false);
                	else if(alg.equals(ctrlCentralityButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.CCen, false);   
                	else if(alg.equals(probInMSS))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.probInMSS, false); 
//                	else if(alg.equals(ctrlProfileButton))
//                		ParameterSet.getAlgorithmSet().put(ParameterSet.CP, false);
                	else if(alg.equals(clfMDSButton))
                		ParameterSet.getAlgorithmSet().put(ParameterSet.ClfMDS, false);
            	}
            	
            	
            }
        }
    }

	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public CytoPanelName getCytoPanelName() {
		// TODO Auto-generated method stub
		return CytoPanelName.WEST;
	}

	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "CytoCtrlAnalyser";
	}

	@Override
	public Icon getIcon() {
		// TODO Auto-generated method stub
		return null;
	}

}

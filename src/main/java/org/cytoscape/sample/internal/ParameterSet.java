package org.cytoscape.sample.internal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JTextField;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;
import org.cytoscape.sample.internal.actions.AnalyzeAction;
import org.cytoscape.sample.internal.panels.MainPanel;
import org.osgi.framework.BundleContext;

public class ParameterSet {
	public static Map<String, Boolean> algorithmSet;
	public static String MDS = "MDS";
	public static String MSS = "MSS";
	public static String ST = "Steering nodes for state transition";
	public static String OC = "Output control";
	public static String MP = "MSS with preference";
	public static String MPMax = "MSS with preference (Max)";
	public static String MPMin = "MSS with preference (Min)";
	public static String CCap = "Control Capacity";
	public static String CCen = "Control Centrality";
	public static String CP = "Control Profile";
	public static String ClfMDS = "Classification by MDS";
	public static String probInMSS = "Probability in a random MSS";
	public static BundleContext bc;
	
	public static JTextField preferenceColumn;
	public static JTextField outputColumn;
	public static JTextField transitionColumn;
//	public static CyNetwork cyNetwork; 
//	public static MainPanel mainPanel;
	
	
	public static AnalyzeAction analyzeAction;
	
	public ParameterSet()
	  {
		algorithmSet = new HashMap<String, Boolean>();
	    setDefaultParams();
//	    this.defaultRowHeight = 40;
	  }


	private void setDefaultParams() {
		// TODO Auto-generated method stub
		algorithmSet.put(ParameterSet.MDS, false);
	    algorithmSet.put(ParameterSet.MSS, false);
	    algorithmSet.put(ParameterSet.ST, false);
	    algorithmSet.put(ParameterSet.OC, false);
	    algorithmSet.put(ParameterSet.MP, false);
	    algorithmSet.put(ParameterSet.MPMax, false);
	    algorithmSet.put(ParameterSet.MPMin, false);
	    algorithmSet.put(ParameterSet.CCap, false);
	    algorithmSet.put(ParameterSet.CCen, false);
	    algorithmSet.put(ParameterSet.CP, false);
	    algorithmSet.put(ParameterSet.ClfMDS, false);
	    algorithmSet.put(ParameterSet.probInMSS, false);
	}
	
	public static Map<String, Boolean> getAlgorithmSet() {
		return algorithmSet;
	}
	
	
	
}

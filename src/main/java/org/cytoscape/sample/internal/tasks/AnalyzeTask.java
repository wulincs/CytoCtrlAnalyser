package org.cytoscape.sample.internal.tasks;

import java.util.ArrayList;
import org.cytoscape.model.CyNetwork;
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
import org.cytoscape.work.Task;
import org.cytoscape.work.TaskMonitor;

public class AnalyzeTask implements Task {
	private final ArrayList<Algorithm> algSet;
//	private final int analyze;
//	private final int resultId;
	private boolean interrupted;
	private CyNetwork network;
	private final ArrayList<String> algnames;
	private final ArrayList<String> Successfelalgnames;
//	private String results;
	MDS algoMDS;
	MSS algoMSS;
	MSSWithPreference algoMSSWithPreference;
	StateTransition algoST;
	ControlCapacity algoCCap;
	ControlCentrality algoCCen;
	ClassificationByMDS algoClfMDS;
	OutputControl algoOC;
	ProbabilityInMSS algoPIS;
	
	//start multi-thread calculation
	public AnalyzeTask(CyNetwork network, ArrayList<Algorithm> algSet, ArrayList<String> algnames) {
		this.network = network;
		this.algSet = algSet;
		this.Successfelalgnames = algnames;
		this.algnames = (ArrayList<String>) algnames.clone(); 
//		results = "<html>";
	}
	@Override
	public void run(TaskMonitor taskMonitor) throws Exception {
		// TODO Auto-generated method stub
//		JOptionPane.showMessageDialog(null, "yahoo22", "oyeah", JOptionPane.ERROR_MESSAGE);

		try {
			for(Algorithm alg : algSet){				
				if (alg instanceof MDS && !ParameterSet.algorithmSet.get("MSS")){ 
					algoMDS = (MDS)alg;
					algoMDS.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("MDS...");
					algoMDS.start();
				}else if(alg instanceof MSS){
					algoMSS = (MSS)alg;
					algoMSS.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("MSS...");
					algoMSS.start();
				}else if(alg instanceof MSSWithPreference){
					algoMSSWithPreference = (MSSWithPreference)alg;
					algoMSSWithPreference.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("MSS with preference...");
					algoMSSWithPreference.start();
				}else if(alg instanceof StateTransition){
					algoST = (StateTransition)alg;
					algoST.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Steering node for state transition...");
					algoST.start();
				}else if(alg instanceof ControlCapacity){
					algoCCap = (ControlCapacity)alg;
					algoCCap.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Calculating control capacity...");
					algoCCap.start();
				}else if(alg instanceof ControlCentrality){
					algoCCen = (ControlCentrality)alg;
					algoCCen.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Calculating control centrality...");
					algoCCen.start();
				}else if(alg instanceof ClassificationByMDS){
					algoClfMDS = (ClassificationByMDS)alg;
					algoClfMDS.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Classifying nodes...");
					algoClfMDS.start();
				}else if(alg instanceof OutputControl){
					algoOC = (OutputControl)alg;
					algoOC.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Output controllability...");
					algoOC.start();
				}else if(alg instanceof ProbabilityInMSS){
					algoPIS = (ProbabilityInMSS)alg;
					algoPIS.setTaskMonitor(taskMonitor, network.getSUID());					
					taskMonitor.setProgress(0);
					taskMonitor.setStatusMessage("Probability in an MSS...");
					algoPIS.start();
				}
				
			}
			
			
//			algoMDS.join();
//			algoMSS.join();
//			JOptionPane.showMessageDialog(null, "done", "done!", JOptionPane.ERROR_MESSAGE);
//			
		} catch (Exception e) {
			throw new Exception("Error while executing the analysis", e);
		} catch(OutOfMemoryError oe){
			System.out.println("out of mem...");
		}finally {
		}
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

}

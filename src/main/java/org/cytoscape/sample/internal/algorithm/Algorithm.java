package org.cytoscape.sample.internal.algorithm;

import java.util.ArrayList;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.sample.internal.ParameterSet;
import org.cytoscape.work.TaskMonitor;

public abstract class Algorithm extends Thread{
	protected boolean cancelled = false;//If set, will schedule the canceled algorithm  at the next convenient opportunity
    protected TaskMonitor taskMonitor = null;
    protected ParameterSet params;   //the parameters used for this instance of the algorithm
    protected CyNetwork currentNetwork;
    //states
    protected long lastScoreTime;	//the time taken by the last score operation
    protected long lastFindTime;	//the time taken by the last find operation
    protected long findCliquesTime=0;//time used to find maximal cliques
    protected boolean isweight;
    
    
    //This method is used in AnalyzeTask
    public void setTaskMonitor(TaskMonitor taskMonitor,long  networkID) {
        this.taskMonitor = taskMonitor;
    }
 
    public ParameterSet getParams() {
        return params;
    }
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }    

    public boolean isCancelled() {
        return this.cancelled;
    }
    
    
    
    
}

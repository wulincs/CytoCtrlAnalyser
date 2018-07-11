package org.cytoscape.sample.internal;

import java.awt.Color;
import java.awt.Paint;
import java.util.ArrayList;
import java.util.HashMap;

import org.cytoscape.model.CyNetwork;
import org.cytoscape.model.CyNode;


public class Node {

	private CyNode n ;
	private int id; // from 1 to N
	private String name;
	private CyNetwork network;
	private boolean MDS = false;
	private boolean MSS = false;
	private boolean MSSWithPreference = false;
	private boolean ST = false; //state transition
	private boolean OC = false; //output control
	private double CCap = 0; //control capacity
	private double pInMSS = 0; //control capacity
	private int CCen = 0; //control centrality
	private int CP = 0; //Control Profile
	private ArrayList<String> selectGroups = null;
	private Paint originColor = null;
	private HashMap<String, Double> BioparasMap;
	private int clfMDS; //0-dispensable; 1-Neutral; 2-indispensable
	
	public Node(CyNode n, CyNetwork network){
		this.network = network;
		this.n = n;
		name = network.getRow(n).get("name", String.class);
		BioparasMap = new HashMap<String, Double>();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public CyNetwork getNetwork() {
		return network;
	}
	public void setNetwork(CyNetwork network) {
		this.network = network;
	}
	public CyNode getN() {
		return n;
	}
	public void setN(CyNode n) {
		this.n = n;
	}
	public boolean getMDS() {
		return MDS;
	}
	public void setMDS(boolean b) {
		MDS = b;
	}
	public boolean getMSSWithPreference() {
		return MSS;
	}
	public void setMSSWithPreference(boolean b) {
		MSS = b;
	}
	public boolean getMSS() {
		return MSS;
	}
	public void setMSS(boolean b) {
		MSS = b;
	}
	public boolean getSST() { // steering node for state transition
		return ST;
	}
	public void setSST(boolean b) {
		ST = b;
	}
	public boolean getOC() { // steering node for output control
		return OC;
	}
	public void setOC(boolean b) {
		OC = b;
	}
	public double getCCap() {
		return CCap;
	}
	public void setCCap(double cCapI) {
		CCap = cCapI;
	}
	public double getPInMSS() {
		return pInMSS;
	}
	public void setpPInMSS(double pInMSSI) {
		pInMSS = pInMSSI;
	}
	public double getCCen() {
		return CCen;
	}
	public void setCCen(int cCen) {
		CCen = cCen;
	}
	public double getCP() {
		return CP;
	}
	public void setCP(int cP) {
		CP = cP;
	}
	public int getClfMDS() {
		return clfMDS;
	}
	public void setClfMDS(int clf) {
		clfMDS = clf;
	}
	
	
//	public void setBioPara(String pname, double biopara){
//		BioparasMap.put(pname, biopara);
//	}
//	
//	public double getPara(String alg){
//		double para = -1;
//		if(alg.equals(ParameterSet.MDS))
//			para = MDS;
//		else if(alg.equals(ParameterSet.CCen))
//			para = CCen;
//		else if(alg.equals(ParameterSet.CCap))
//			para = CCap; 
//		else if(alg.equals(ParameterSet.CP))
//			para = CP;
//		else{
//			para = BioparasMap.get(alg);
//		}
//		return para;
//
//	}
	
	public double getBioPara(String alg){
		return BioparasMap.get(alg);
	}
	
	public ArrayList<String> getSelectGroups() {
		return selectGroups;
	}
	public void setSelectGroups(ArrayList<String> selectGroups) {
		this.selectGroups = selectGroups;
	}
	public Paint getOriginColor() {
		return originColor;
	}
	public void setOriginColor(Paint originColor) {
		this.originColor = originColor;
	}
	
	
	
}

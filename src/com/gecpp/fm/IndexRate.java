package com.gecpp.fm;

public class IndexRate {
	
	public String getPn() {
		return pn;
	}


	public void setPn(String pn) {
		this.pn = pn;
	}


	public float getWeight() {
		return weight;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}


	public String getFullword() {
		return fullword;
	}


	public void setFullword(String fullword) {
		this.fullword = fullword;
	}


	private String pn;
    private float weight;
    private String fullword;
    

    public IndexRate(String _pn, float _weight, String _fullword) {
    	this.pn = _pn;
    	this.weight = _weight;
    	this.fullword = _fullword;
    }

  

}

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
	
	public int getKind() {
		return kind;
	}


	public void setKind(int kind) {
		this.kind = kind;
	}


	private String pn;
    private float weight;
    private String fullword;
    private int kind;
    private int page;
    private int order;
    

    public int getOrder() {
		return order;
	}


	public void setOrder(int order) {
		this.order = order;
	}


	public int getPage() {
		return page;
	}


	public void setPage(int page) {
		this.page = page;
	}


	public IndexRate(String _pn, float _weight, String _fullword, int _kind, int _page, int _order) {
    	this.pn = _pn;
    	this.weight = _weight;
    	this.fullword = _fullword;
    	this.kind = _kind;
    	this.page = _page;
    	this.order = _order;
    }

  

}

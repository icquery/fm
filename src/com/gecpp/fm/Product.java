package com.gecpp.fm;

import java.util.List;

public class Product {
	
	public String getMfs() {
		return mfs;
	}

	public void setMfs(String mfs) {
		this.mfs = mfs;
	}

	public String getPn() {
		return pn;
	}

	public void setPn(String pn) {
		this.pn = pn;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getInventory() {
		return inventory;
	}

	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	
	public Product(String _pn, int _id, String _mfs, int _inventory) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
    	
    }

	private String pn;
	
	private int id;
	
	private String mfs;
	
	private int inventory;

	
	

}

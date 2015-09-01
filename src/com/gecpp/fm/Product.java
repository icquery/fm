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

	public String getSupplierpn() {
		return supplierpn;
	}

	public void setSupplierpn(String supplierpn) {
		this.supplierpn = supplierpn;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}
	
	public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
		this.price = _price;
		this.supplierpn = _supplierpn;
    	
    }

	private String pn;
	
	private int id;
	
	private String mfs;
	
	private int inventory;

	private String price;

	private String supplierpn;
}

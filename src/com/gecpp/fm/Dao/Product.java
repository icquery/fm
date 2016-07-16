package com.gecpp.fm.Dao;

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
	
	public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs, int _supplierid) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
		this.price = _price;
		this.supplierpn = _supplierpn;
    	this.supplierid = _supplierid;
    }
	
	// 20160518深度查詢優化
	public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs, int _supplierid, String _lead, String _rohs, int _mfs_id) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
		this.price = _price;
		this.supplierpn = _supplierpn;
    	this.supplierid = _supplierid;
    	this.lead = _lead;
    	this.rohs = _rohs;
    	this.mfs_id = _mfs_id;
    }
	
	// 20160518深度查詢優化
	/* 20160706 ------------------            詳情頁深度搜尋 by PN */
		public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs, int _supplierid, String _lead, String _rohs, int _mfs_id, String _pkg, String _name) {
	    	this.pn = _pn;
	    	this.id = _id;
	    	this.mfs = _mfs;
	    	this.inventory = _inventory;
			this.price = _price;
			this.supplierpn = _supplierpn;
	    	this.supplierid = _supplierid;
	    	this.lead = _lead;
	    	this.rohs = _rohs;
	    	this.mfs_id = _mfs_id;
	    	this.pkg = _pkg;
	    	this.supplier = _name;
	}
		
	
	public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs, int _supplierid, String _supplier) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
		this.price = _price;
		this.supplierpn = _supplierpn;
    	this.supplierid = _supplierid;
    	this.supplier = _supplier;
    }
	
	public Product(int _inventory, String _price, int _id, String _pn, String _supplierpn, String _mfs, int _supplierid, String _supplier, String _pkg, String _description) {
    	this.pn = _pn;
    	this.id = _id;
    	this.mfs = _mfs;
    	this.inventory = _inventory;
		this.price = _price;
		this.supplierpn = _supplierpn;
    	this.supplierid = _supplierid;
    	this.supplier = _supplier;
    	this.pkg = _pkg;
    	this.desciption = _description;
    }

	public int getSupplierid() {
		return supplierid;
	}

	public void setSupplierid(int supplierid) {
		this.supplierid = supplierid;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	/**
	 * @return the pkg
	 */
	public String getPkg() {
		return pkg;
	}

	/**
	 * @param pkg the pkg to set
	 */
	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	/**
	 * @return the desciption
	 */
	public String getDesciption() {
		return desciption;
	}

	/**
	 * @param desciption the desciption to set
	 */
	public void setDesciption(String desciption) {
		this.desciption = desciption;
	}

	/**
	 * @return the lead
	 */
	public String getLead() {
		return lead;
	}

	/**
	 * @param lead the lead to set
	 */
	public void setLead(String lead) {
		this.lead = lead;
	}

	/**
	 * @return the rohs
	 */
	public String getRohs() {
		return rohs;
	}

	/**
	 * @param rohs the rohs to set
	 */
	public void setRohs(String rohs) {
		this.rohs = rohs;
	}

	/**
	 * @return the mfs_id
	 */
	public int getMfs_id() {
		return mfs_id;
	}

	/**
	 * @param mfs_id the mfs_id to set
	 */
	public void setMfs_id(int mfs_id) {
		this.mfs_id = mfs_id;
	}


	private String pn;
	
	private int id;
	
	private String mfs;
	
	private int inventory;

	private String price;

	private String supplierpn;
	
	private int supplierid;
	
	private String supplier;
	
	private String pkg;
	
	private String desciption;
	
	private String lead;
	
	private String rohs;
	
	private int mfs_id;
	
}

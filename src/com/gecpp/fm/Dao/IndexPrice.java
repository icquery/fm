package com.gecpp.fm.Dao;

public class IndexPrice {
	private int id;
	private int inventory;
	private String price;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return the inventory
	 */
	public int getInventory() {
		return inventory;
	}
	/**
	 * @param inventory the inventory to set
	 */
	public void setInventory(int inventory) {
		this.inventory = inventory;
	}
	/**
	 * @return the price
	 */
	public String getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(String price) {
		this.price = price;
	}
	
	public IndexPrice(int _id, int _inventory, String _price) {
 
    	this.id = _id;
    	this.inventory = _inventory;
    	this.inventory = _inventory;
		this.price = _price;
	
    }
}

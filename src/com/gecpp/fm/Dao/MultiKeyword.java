package com.gecpp.fm.Dao;

import java.util.List;

public class MultiKeyword {
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public List<Product> getPkey() {
		return pkey;
	}
	public void setPkey(List<Product> pkey) {
		this.pkey = pkey;
	}

	public int getSearchtype() {
		return searchtype;
	}
	public void setSearchtype(int searchtype) {
		this.searchtype = searchtype;
	}

	private int count;
	private String keyword;
	private List<Product> pkey;
	private int searchtype;

}

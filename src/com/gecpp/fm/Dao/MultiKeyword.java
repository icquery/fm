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

	/**
	 * @return the mfs
	 */
	public String getMfs() {
		return mfs;
	}
	/**
	 * @param mfs the mfs to set
	 */
	public void setMfs(String mfs) {
		this.mfs = mfs;
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

	private int count;
	private String keyword;
	private String mfs;
	private String pkg;
	private List<Product> pkey;
	private int searchtype;

}

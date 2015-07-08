package com.gecpp.fm;

import java.util.List;

public interface IFuzzySearch {
	/**
	 * @param name
	 * @return
	 */
	public int DeleteFuzzyRecord(int pid);
	
	/**
	 * @return
	 */
	public int InsertFuzzyRecord(
			int pid,
			String pn,
			String mfs,
			String catalog,
			String description,
			String param
);
	
	/**
	 * @return
	 */
	public List<String> QueryFuzzyRecord(String fuzzyString);
}



package com.gecpp.fm;

import java.util.List;

public interface IFuzzySearch {
	
	/**
	 * DeleteFuzzyRecord，刪除該ID之索引
	 * @param pid
	 * @return 1 success, 0 fail
	 */
	public int DeleteFuzzyRecord(int pid);
	
	/**
	 * InsertFuzzyRecord，新增或更新傳入ID之索引
	 * @param pid
	 * @param pn
	 * @param mfs
	 * @param catalog
	 * @param description
	 * @param param
	 * @return 1 success, 0 fail
	 * 如果FuzzyDB中已有該筆pid資料，則會更新
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
	 * 舊的zzySearch查詢查詢
	 * @param fuzzyString
	 * @return
	 */
	public List<String> QueryFuzzyRecord(String fuzzyString);
	
	
	/**
	 * QueryFuzzyRecordByListPage，是依照om排序過，且可以回傳分頁過後的料號資訊
	 */
	public OrderResult QueryFuzzyRecordByListPage(String fuzzyString, int currentPage, int pageSize);
	
	/**
	 * QueryFuzzyRecordByDeptSearch，2015/11/30深度搜尋
	 */
	public OrderResult QueryFuzzyRecordByDeptSearch(String pn, int inventory, int lead, int rohs, List<Integer> mfs, List<Integer> abbreviation, int currentPage, int pageSize);
	
	/** 
	 * GetMaxIndexID，可以查詢目前索引建立最後的ID
	 * @return  最後的ID
	 */
	public int GetMaxIndexID();
	
	/** GetIndexIDStatus，可以查詢該索引ID建立的狀態(已建OR未建) 1 為已建 , 0 為未建
	 * 
	 * @param pid
	 * @return  1 為已建 , 0 為未建
	 */
	public int GetIndexIDStatus(int pid);
	
	
	/**
	 * getProductByMultipleSearch，2016/01/07 多料號搜索
	 */
	public QueryResult getProductByMultipleSearch(String[]  parts);
	
	
	
}



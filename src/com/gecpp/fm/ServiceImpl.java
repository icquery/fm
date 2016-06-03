package com.gecpp.fm;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

public class ServiceImpl extends HessianServlet implements IFuzzySearch {
 
	// 20160218 function deprecated
	//protected static fuzzysearch fm = null;

	@Override 
	public void init(ServletConfig config) 
	{
		// 20160218 not using connection pool anymore
		/*
		if(fm == null)
		{
			fm = new fuzzysearch();
			fm.loadParams();
			fm.connectPostgrel();
		}
		*/
		
		try {
			super.init(config);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int DeleteFuzzyRecord(int pid) {
		// 20160218 function deprecated
		
		//FuzzyInstance fi = new FuzzyInstance();
		//return fi.DeleteFuzzyRecord(pid);
		return -1;
	}

	@Override
	public int InsertFuzzyRecord(
			int pid,
			String pn,
			String mfs,
			String catalog,
			String description,
			String param
								){
		// 20160218 function deprecated
		//FuzzyInstance fi = new FuzzyInstance();
		//return fi.InsertFuzzyRecord(pid, pn, mfs, catalog, description, param, fm.GetDbConnection(), fm.getSegmenter());
		return -1;
	}

	@Override
	public List<String> QueryFuzzyRecord(String fuzzyString) {

		// 20160218 function deprecated
		//FuzzyInstance fi = new FuzzyInstance();
		//List<String> list = fi.GetQueryByEachWord(fuzzyString, fm.GetDbConnection(), fm.getSegmenter());
		////List<String> list = fi.GetQuery(fuzzyString, fm.GetDbConnection(), fm.getSegmenter());
		////list.add(fm.DebugGetQuery(fuzzyString));
		//return list;
		return null;
	}
	
	@Override
	public OrderResult QueryFuzzyRecordByListPage(String fuzzyString,
			int currentPage, int pageSize) {
		// TODO Auto-generated method stub
		FuzzyInstance fi = new FuzzyInstance();
		
		OrderResult result = fi.QueryFuzzyRecordByListPage(fuzzyString, currentPage, pageSize);
		
		return result;
	}
	
	@Override
	public OrderResult QueryFuzzyRecordByDeptSearch(String pn, 
			int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation, 
			int currentPage, 
			int pageSize)
	{
		FuzzyInstance fi = new FuzzyInstance();
		
		OrderResult result = fi.QueryFuzzyRecordByDeptSearch(pn, inventory, lead, rohs, mfs, abbreviation, currentPage, pageSize);
		
		return result;
	}

	@Override
	public int GetMaxIndexID() {
		// TODO Auto-generated method stub
		FuzzyInstance fi = new FuzzyInstance();
		// 20160218 function deprecated
		//return fi.GetMaxIndexID(fm.GetDbConnection());
		return fi.GetMaxIndexID();
	}

	@Override
	public int GetIndexIDStatus(int pid) {
		// TODO Auto-generated method stub
		FuzzyInstance fi = new FuzzyInstance();
		
		return fi.GetIndexIDStatus(pid);
	}
	
	@Override
	public void destroy()
	{
		// 20160218 function deprecated
		//fm.closePostgrel();
		super.destroy();
	}

	@Override
	public QueryResult getProductByMultipleSearch(String[]  parts)
	{
		FuzzyInstance fi = new FuzzyInstance();
		
		QueryResult result = fi.QueryProductByMultipleSearch(parts);
		
		return result;
	}
	
	@Override
	public QueryResult getProductByMultipleSearchJson(String  parts)
	{
		FuzzyInstance fi = new FuzzyInstance();
		
		QueryResult result = fi.QueryProductByMultipleSearchJson(parts);
		
		return result;
	}
	
	@Override
	public Map<String,Map<String,MultipleParam>> findParamByPn(List<String> pns)
	{
		String[] stockArr = new String[pns.size()];
		stockArr = pns.toArray(stockArr);
		
		FuzzyInstance fi = new FuzzyInstance();
		
		Map<String,Map<String,MultipleParam>> result = fi.QueryParamterByMultipleSearch(stockArr);
		
		return result;
	}
}


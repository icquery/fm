package com.gecpp.fm;


import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import com.caucho.hessian.server.HessianServlet;

public class ServiceImpl extends HessianServlet implements IFuzzySearch {

	protected fuzzysearch fm = null;

	@Override 
	public void init(ServletConfig config) 
	{
		fm = new fuzzysearch();
		fm.loadParams();
		fm.connectPostgrel();
		
		try {
			super.init(config);
		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public int DeleteFuzzyRecord(int pid) {
		return fm.DeleteFuzzyRecord(pid);
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
		
		return fm.InsertFuzzyRecord(pid, pn, mfs, catalog, description, param);
	}

	@Override
	public List<String> QueryFuzzyRecord(String fuzzyString) {

		List<String> list = fm.GetQuery(fuzzyString);
		//list.add(fm.DebugGetQuery(fuzzyString));
		return list;
	}
	
	@Override
	public void destroy()
	{
		fm.closePostgrel();
		super.destroy();
	}

}


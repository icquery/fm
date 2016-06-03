package com.gecpp.fm.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gecpp.fm.Dao.IndexPrice;
import com.gecpp.fm.Dao.Product;
import com.gecpp.fm.Util.CommonUtil;
import com.gecpp.fm.Util.DbHelper;
import com.gecpp.fm.Util.DbHelper.Site;

public class OrderManagerModel {
	
	public static boolean IsPn(String pnKey) {
		
		// 20160304 放寬pn認定條件
		if(pnKey.length() <= 3)
		{
			return false;
		}
		
		
		String strInverseArray[] = pnKey.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		if(strInverseArray.length < 2 && !CommonUtil.IsNumeric(pnKey)) // 非中英夾雜有可能是單字
		{
			return false;
		}

		List<String> sList = new ArrayList<>();
		
		pnKey = CommonUtil.parsePnKey(pnKey);
		
		// 20160427 認定是否為pn放至最寬鬆
		// 20160526 sky suggest not using pm_supplier_pn anymore
		/*
		String strSql = "(select pn from pm_supplier_pn where supplier_pn_key like '" + pnKey + "' limit 20)  "
		+ " UNION (SELECT pn FROM pm_pn where pn_key like '" + pnKey + "' limit 20) ORDER BY pn limit 20";
		*/

		String strSql = "SELECT count(pn) FROM pm_pn where pn_key like '" + pnKey + "' limit 20";
		
		sList = DbHelper.getList(strSql, Site.pm);
		
		int nCount = 0;
		if(sList.size() > 0)
		{
			try
			{
				nCount = Integer.parseInt(sList.get(0).trim());
			}
			catch (Exception e)
			{
				System.out.print(e.getMessage());
			}
			
		}
		
		return nCount > 0 ? true: false;

	}
	
	public static List<String> getPnsByPnKey(String pnKey) {
		
		List<String> sList = null;
		
		pnKey = CommonUtil.parsePnKey(pnKey);
		
		String strSql = "(select pn from pm_supplier_pn where supplier_pn_key like '" + pnKey + "' limit 50) "
		+ " UNION (SELECT pn FROM pm_pn where pn_key like '" + pnKey + "' limit 50) ORDER BY pn limit 50";

		sList = DbHelper.getList(strSql, Site.pm);
		
		sList = CommonUtil.removeSpaceList(sList);

		return sList;

	}
	
	public static List<IndexPrice> getPriceByProdcut(String strSql) {
		
		List<IndexPrice> sList = new ArrayList<>();
		
		
		try {

			Connection conn = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {

					conn = DbHelper.connectPm();
			
				
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexPrice(rs.getInt(1), rs.getInt(2), rs.getString(3)));
				
			}

			finally {

				DbHelper.attemptClose(rs);
				DbHelper.attemptClose(stmt);
				DbHelper.attemptClose(conn);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return sList;

	}
}

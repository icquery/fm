package com.gecpp.fm.model;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gecpp.fm.Util.CommonUtil;
import com.gecpp.fm.Util.DbHelper;
import com.gecpp.fm.Util.DbHelper.Site;

public class OrderManagerModel {
	
	public static boolean IsPn(String pnKey) {
		
		if(pnKey.length() <= 4)
		{
			return false;
		}
		
		String strInverseArray[] = pnKey.split("(?<=\\D)(?=\\d)|(?<=\\d)(?=\\D)");
		if(strInverseArray.length < 2) // 非中英夾雜有可能是單字
		{
			return false;
		}
		
		List<String> sList = new ArrayList<>();
		
		String strSql = "(select pn from pm_supplier_pn where supplier_pn_key like '" + pnKey + "' limit 20) "
		+ " UNION (SELECT pn FROM pm_pn where pn_key like '" + pnKey + "' limit 20) ORDER BY pn limit 20";

		sList = DbHelper.getList(strSql, Site.pm);

		return sList.size() > 0 ? true: false;

	}
	
	public static List<String> getPnsByPnKey(String pnKey) {
		
		List<String> sList = null;
		
		String strSql = "(select pn from pm_supplier_pn where supplier_pn_key like '" + pnKey + "' limit 20) "
		+ " UNION (SELECT pn FROM pm_pn where pn_key like '" + pnKey + "' limit 20) ORDER BY pn limit 20";

		sList = DbHelper.getList(strSql, Site.pm);
		
		sList = CommonUtil.removeSpaceList(sList);

		return sList;

	}
}

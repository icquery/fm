package com.gecpp.fm.model;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.gecpp.fm.Dao.IndexAdj;
import com.gecpp.fm.Dao.IndexRate;
import com.gecpp.fm.Dao.IndexShort;
import com.gecpp.fm.Util.DbHelper;

public class FuzzyManagerModel {
	
	public static List<IndexAdj> GetAdjust()
	{
		String strSql = "select word, alterword, kind, adjust from qeindexadj";
		
		List<IndexAdj> sList = new ArrayList<IndexAdj>();

		try {

			Connection conn = DbHelper.connectFm();
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexAdj(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getFloat(4)));
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
	
	public static List<IndexShort> GetShort()
	{
		String strSql = "select word, alterword from qeindexshort";
		
		List<IndexShort> sList = new ArrayList<IndexShort>();

		try {

			Connection conn = DbHelper.connectFm();
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexShort(rs.getString(1), rs.getString(2)));
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
	
	public static List<IndexRate> GetAllIndexRate(String stoken, int order, int limitNumber)
	{
		String strSql;
		
		strSql = "select pn, weight, fullword, kind, page, " + order + " from qeindex where word = '"
                + stoken + "' order by weight desc limit " + limitNumber;
		
		List<IndexRate> sList = new ArrayList<IndexRate>();

		try {
			Connection conn = DbHelper.connectFm();
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexRate(rs.getString(1), rs.getFloat(2), rs.getString(3), rs.getInt(4), rs.getInt(5), rs.getInt(6)));
				// System.out.println(rs.getString(0));
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

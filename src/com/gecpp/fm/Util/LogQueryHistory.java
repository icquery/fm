package com.gecpp.fm.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gecpp.fm.fuzzysearch;
import com.gecpp.fm.Dao.Keyword;
import com.gecpp.fm.Dao.Keyword.KeywordKind;

public class LogQueryHistory {

	public static void InsertLog(String key, String value)
	{
		String strSql = "INSERT INTO qeindexlog(keyword, sqlstr) VALUES(?, ?)";
		List<String> params = new ArrayList<String>();
		
		params.add(key);
		params.add(value);
		
		DbHelper.Insert(strSql, params);
      
	}
	
	public static void InsertExecLog(String uuid, String proc, String elapsetime, String topid)
	{
		String strSql = "insert into logquerytime(uuid, proc, elapsetime, topid) values(?,?,?,?)";

		List<String> params = new ArrayList<String>();
		
		params.add(uuid);
		params.add(proc);
		params.add(elapsetime);
		params.add(topid);

		
		DbHelper.Insert(strSql, params);
	}
	
	public static void InsertQueryLog(Keyword inputKeyword, int page)
	{
		String strSql = "insert into logqueryhistory(uuid, seq, keyword, kind, fullword) values(?, ?, ?, ?, ?)";
		
		List<String> keywords = inputKeyword.getKeyword();
		List<KeywordKind> kinds = inputKeyword.getKind();
		
		int i=0;
		for(String keyword : keywords )
		{
			List<String> params = new ArrayList<String>();
			
			params.add(inputKeyword.getUuid());
			params.add(String.valueOf(i)); 
			params.add(keyword);
			params.add(String.valueOf(page));
			params.add(inputKeyword.getInputdata());
			
			DbHelper.InsertLog(strSql, params);
			
			i++;
		}
		
	}
	
	public static void InsertQueryLog(Keyword inputKeyword, int page, List<Integer> mfs, List<Integer> abbreviation)
	{
		String strSql = "insert into logqueryhistory(uuid, seq, keyword, kind, fullword) values(?, ?, ?, ?, ?)";
		
		List<String> keywords = inputKeyword.getKeyword();
		List<KeywordKind> kinds = inputKeyword.getKind();
		
		int i=0;
		for(String keyword : keywords )
		{
			List<String> params = new ArrayList<String>();
			
			params.add(inputKeyword.getUuid());
			params.add(String.valueOf(i)); 
			params.add(keyword);
			params.add(String.valueOf(page));
			params.add(inputKeyword.getInputdata() + "mfs:" + mfs.toString() + "supp:" + abbreviation.toString());
			
			DbHelper.InsertLog(strSql, params);
			
			i++;
		}
		
	}
}

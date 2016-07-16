package com.gecpp.fm.Util;

import java.util.ArrayList;
import java.util.List;

import com.gecpp.fm.Dao.IndexResult;
import com.gecpp.fm.Dao.Keyword;

public class StopWatch {
	private long startTime;
	private String procName;
	
	public StopWatch(String _procName) {
        startTime = System.currentTimeMillis();
        procName = _procName;
    }
	
	public void getElapsedTime() {
        long endTime = System.currentTimeMillis();
        LogQueryHistory.InsertLog(procName + " exec time:", String.valueOf(endTime - startTime));
    }
	
	public void getElapseTime(Keyword keyQuery, List<String> result)
	{
		long endTime = System.currentTimeMillis();
		List<String> logResult = new ArrayList<String>();
		
		int i=0;
		
		// log
		for(String str : result)
		{
			logResult.add(str);
			i++;
			if(i==10)
				break;
		}
		
		LogQueryHistory.InsertExecLog(keyQuery.getUuid(), procName, String.valueOf(endTime - startTime), logResult.toString());
	}
	
	public void getElapseTimeIndexResult(Keyword keyQuery, List<IndexResult> result)
	{
		long endTime = System.currentTimeMillis();
		List<String> logResult = new ArrayList<String>();
		
		int i=0;
		
		// log
		for(IndexResult str : result)
		{
			logResult.add(str.getPn());
			i++;
			if(i==10)
				break;
		}
		
		LogQueryHistory.InsertExecLog(keyQuery.getUuid(), procName, String.valueOf(endTime - startTime), logResult.toString());
	}
	
	public void getElapseTimeOrderResult(Keyword keyQuery, List<String> result)
	{
		long endTime = System.currentTimeMillis();
		List<String> logResult = new ArrayList<String>();
		
		int i=0;
		
		// log
		for(String str : result)
		{
			logResult.add(str);
			i++;
			if(i==10)
				break;
		}
		
		LogQueryHistory.InsertExecLog(keyQuery.getUuid(), procName, String.valueOf(endTime - startTime), logResult.toString());
	}
}

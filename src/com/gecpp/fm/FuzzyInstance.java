package com.gecpp.fm;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;

class ValueComparator implements Comparator<String> {

    Map<String, Float> base;
    public ValueComparator(Map<String, Float> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}

class IntComparator implements Comparator<String> {

    Map<String, Integer> base;
    public IntComparator(Map<String, Integer> base) {
        this.base = base;
    }

    // Note: this comparator imposes orderings that are inconsistent with equals.    
    public int compare(String a, String b) {
        if (base.get(a) >= base.get(b)) {
            return -1;
        } else {
            return 1;
        } // returning 0 would merge keys
    }
}


public class FuzzyInstance {
	
	private String strSkipWord = ", . ; + - | / \\ ' \" : ? < > [ ] { } ! @ # $ % ^ & * ( ) ~ ` _ － ‐ ， （ ）";
	private String[] SkipWord = null;
	
	public int DeleteFuzzyRecord(int pid, Connection conn) {
		String strSql = "delete from qeindex where page = " + pid;

		execUpdate(strSql, conn);
		
		attemptClose(conn);
		return 0;
	}
	
	public int InsertFuzzyRecord(int pid, String pn, String mfs,
			String catalog, String description, String param, Connection conn, CRFClassifier<CoreLabel> segmenter) {
		String sPid = Integer.toString(pid);
		
		// first delete old data
		String strSql = "delete from qeindex where page = " + pid;
		execUpdate(strSql, conn);
		
		ProcessData(sPid, pn, mfs, catalog, description, param, conn, segmenter);
		
		attemptClose(conn);
		
		return 0;
	}
	
	protected void ProcessData(String pid, String pn, String mfs,
			String catalog, String description, String param, Connection conn, CRFClassifier<CoreLabel> segmenter) {
		Map<String, String> scoreMap = null;

		// 清除雜訊
		if (description != null && !description.isEmpty())
			description.replaceAll("[\"\']", "");
		if (param != null && !param.isEmpty())
			param.replaceAll("[\"\']", "");
 
		// 料號
		scoreMap = segmentData(pn, segmenter);

		// 料號需有完整紀錄
		if (!scoreMap.containsKey(pn)) {
			InsertPostgrel(pn, Integer.parseInt(pid), 1, 0, pn, mfs, catalog, pn, conn);
		}

		InsertAllWord(pid, 0, pn,mfs, catalog, scoreMap, conn);

		// mfs
		scoreMap = segmentData(mfs, segmenter);
		InsertAllWord(pid, 1, pn,mfs, catalog, scoreMap, conn);

		// catalog
		scoreMap = segmentData(catalog, segmenter);
		InsertAllWord(pid, 2, pn,mfs, catalog, scoreMap, conn);

		// description
		scoreMap = segmentData(description, segmenter);
		InsertAllWord(pid, 3, pn,mfs, catalog, scoreMap, conn);

		// param
		scoreMap = segmentData(param, segmenter);
		InsertAllWord(pid, 4, pn,mfs, catalog, scoreMap, conn);
		
		
	}
	
	protected List<IndexAdj> GetAdjust(Connection conn)
	{
		String strSql = "select word, alterword, kind, adjust from qeindexadj";
		
		List<IndexAdj> sList = new ArrayList<IndexAdj>();

		try {

			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexAdj(rs.getString(1), rs.getString(2), rs.getInt(3), rs.getFloat(4)));
				// System.out.println(rs.getString(0));
			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sList;
	}
	
	public List<String> GetQueryByEachWord(String strData, Connection conn, CRFClassifier<CoreLabel> segmenter)
	{
		long startTime = System.currentTimeMillis();
		
		long elapsedSqlTime = 0L;
		
		String[] strFullword = null;
		List<String> sList = new ArrayList<String>();
		String sNumber = "20000";
		int nTotal = 20;
		
		if (strData != null && !strData.isEmpty())
		{
			strData = strData.toUpperCase();
			strFullword = strData.replaceAll("^[,\\s]+", "").split("[,\\s]+");
		}
		else
			return sList;
		
		String sCombine = "";
		
		String strSql = "";
		
		HashMap<String,Float> PnWeightMap = new HashMap<String,Float>();
		ValueComparator bvc =  new ValueComparator(PnWeightMap);
        TreeMap<String,Float> sorted_map = new TreeMap<String,Float>(bvc);
        
        // 找各關鍵字對應的料號，如果重複，要排在前面
        HashMap<String, Integer> PnKeywordMap = new HashMap<String, Integer>();
        IntComparator ivc =  new IntComparator(PnKeywordMap);
        TreeMap<String,Integer> int_map = new TreeMap<String,Integer>(ivc);
        
        
      
        // 取得關鍵字調整
        List<IndexAdj> adjust = GetAdjust(conn);

        if (strFullword != null) {
			for (String stoken : strFullword) {

				strSql = "";
				
				if (SkipWord(stoken) || stoken.length() == 0)
					continue;
				
				String sKeyword = "";
				String sFullword = "";
				sList.clear();
				
				sKeyword = stoken;

				List<String> segmented = segmenter.segmentString(stoken);
				
				//List<String> segmented = new ArrayList<String>();
				//segmented.add(stoken);
	
				if (segmented != null) {
					for (String element : segmented) {

						if (SkipWord(element) || element.length() == 0)
							continue;

						sList.add(element);
						

					}
				}
				
				// 目前先都不要用like
				// 再加一個不要段字的
				sList.remove(stoken);
				//sList.add(stoken);
				strSql += "(select pn, weight, fullword, kind from qeindex where word = '"
						+ stoken + "' and weight >= 0.5 order by weight desc limit " + sNumber + ") ";
				strSql += " union ";
				
				for (int i = 0; i < sList.size(); i++) {
					
					if(!stoken.equals((sList).get(i)))
					{
						
						strSql += "(select pn, weight, fullword, kind from qeindex where word = '"
								+ sList.get(i) + "' and weight >= 0.5 order by weight desc limit " + sNumber + ") ";
						strSql += " union ";
						
					}
				}
				
			

				strSql = strSql.substring(0, strSql.length() - 7);
				
				long startSqlTime = System.currentTimeMillis();
				
				List<IndexRate> sIndexRate = GetAllIndexRate(strSql, conn);
				
				long stopSqlTime = System.currentTimeMillis();
				elapsedSqlTime += stopSqlTime - startSqlTime;
				
				sCombine += strSql + ":" + elapsedSqlTime + ";";
				
				// 調整權證正確性
				for(IndexRate iter:sIndexRate)
				{
					String lngWord = "";
					String shtWord = "";
					float addWeight = 0f;
					
					// 查詢字與關鍵字比對長度後對換
					// 方便string compare時找頻率
					if(iter.getFullword().length() >= sKeyword.length())
					{
						lngWord = iter.getFullword();
						shtWord = sKeyword;
						
						// 部分相同
						addWeight = 2.5f;
					}
					else
					{
						lngWord = sKeyword;
						shtWord = iter.getFullword();
						
						// 查詢字大於關鍵字
						addWeight = 0.75f;
					}
					
					if(CountStringOccurrences(lngWord, shtWord) >= 1)
					{
						// 完全符合時提高
						if(lngWord.length() == shtWord.length())
							iter.setWeight(iter.getWeight() + 5);
						else
							iter.setWeight(iter.getWeight() + addWeight);
					}
					else
						iter.setWeight(0.01f);
					
					// 調整關鍵字(依照類別)
					for(IndexAdj adj:adjust)
					{
						if(adj.getWord().equalsIgnoreCase(iter.getFullword()) && adj.getKind()==iter.getKind())
							iter.setWeight(iter.getWeight() + adj.getAdjust());

						if(adj.getWord().equalsIgnoreCase(iter.getFullword()) && adj.getKind()!=iter.getKind())
							iter.setWeight(iter.getWeight() - 2);
					
					}
					
					if(PnWeightMap.containsKey(iter.getPn()))
		            {
						Float fWeight = PnWeightMap.get(iter.getPn());
						
						PnWeightMap.put(iter.getPn(), fWeight+iter.getWeight());

		            }
		            else {

		            	PnWeightMap.put(iter.getPn(), iter.getWeight());
		            }
				}
				
				// 不同關鍵字，但是有相同料號，將會排在前面給前端
				for (Map.Entry<String, Float> entry  : PnWeightMap.entrySet()) {
					
					String key = entry.getKey();
					Float value = entry.getValue();
				    
					if(value >= 1.0f)	// 到達一定權重再加
					{
						if(PnKeywordMap.containsKey(key))
			            {
							Integer iCount = PnKeywordMap.get(key);
							
							PnKeywordMap.put(key, iCount+1);
	
			            }
						
			            else {
	
			            	PnKeywordMap.put(key, 1);
			            }
					}
				}
				
			}
		}
		
		// sort 後傳回
		sorted_map.putAll(PnWeightMap);
		
		int_map.putAll(PnKeywordMap);
		
		List<String> sPnReturn = new ArrayList<String>();
		
		int iCount = 0;
		
		for(Map.Entry<String,Integer> entry : int_map.entrySet()) {
			if(entry.getValue() > 1)
			{
				sPnReturn.add(entry.getKey());
				iCount++;
				
				if(iCount >= nTotal)
					break;
			}
		}
		
		for(Map.Entry<String,Float> entry : sorted_map.entrySet()) {
			if(!sPnReturn.contains(entry.getKey()))
			{
				sPnReturn.add(entry.getKey());
				iCount++;
				
				if(iCount >= nTotal)
					break;
			}
		}
		
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    
	    // log query history
	    //InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine + "; " + sorted_map.toString(), conn);
	    //InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine, conn);
	    InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine + "; " + sPnReturn.toString(), conn);

		return sPnReturn;
		
	}
	
	
	public List<String> GetQuery(String strData, Connection conn, CRFClassifier<CoreLabel> segmenter) {
		
		long startTime = System.currentTimeMillis();
		
		long elapsedSqlTime = 0L;
		
		String[] strFullword = null;
		List<String> sList = new ArrayList<String>();
		String sNumber = "20000";
		int nTotal = 20;
		
		String sCombine = "";
		
		String delimiters = "[\\p{Punct}\\s]+";

		if (strData != null && !strData.isEmpty())
		{
			strData = strData.toUpperCase();
			strFullword = strData.split(delimiters);
		}
		else
			return sList;

		String strSql = "";
		
		HashMap<String,Float> PnWeightMap = new HashMap<String,Float>();
		ValueComparator bvc =  new ValueComparator(PnWeightMap);
        TreeMap<String,Float> sorted_map = new TreeMap<String,Float>(bvc);
        
        // 取得關鍵字調整
        List<IndexAdj> adjust = GetAdjust(conn);

		if (strFullword != null) {
			for (String stoken : strFullword) {

				strSql = "";
				
				if (SkipWord(stoken) || stoken.length() == 0)
					continue;
				
				String sKeyword = "";
				String sFullword = "";
				sList.clear();
				
				sKeyword = stoken;

				List<String> segmented = segmenter.segmentString(stoken);
				
				//List<String> segmented = new ArrayList<String>();
				//segmented.add(stoken);
	
				if (segmented != null) {
					for (String element : segmented) {

						if (SkipWord(element) || element.length() == 0)
							continue;

						sList.add(element);
						

					}
				}
				
				// 目前先都不要用like
				// 再加一個不要段字的
				sList.remove(stoken);
				//sList.add(stoken);
				strSql += "(select pn, weight, fullword, kind from qeindex where word = '"
						+ stoken + "' and weight >= 0.5 order by weight desc limit " + sNumber + ") ";
				strSql += " union ";
				
				for (int i = 0; i < sList.size(); i++) {
					
					if(!stoken.equals((sList).get(i)))
					{
						
						strSql += "(select pn, weight, fullword, kind from qeindex where word = '"
								+ sList.get(i) + "' and weight >= 0.5 order by weight desc limit " + sNumber + ") ";
						strSql += " union ";
						
					}
				}
				
				/*
				// 再加一個不要段字的
				sList.remove(stoken);
				//sList.add(stoken);
				strSql += "(select pn, weight, fullword, kind from qeindex where word like '"
						+ stoken + "%' order by weight desc limit " + sNumber + ") ";
				strSql += " union ";
				
				for (int i = 0; i < sList.size(); i++) {
					
					if(!stoken.equals((sList).get(i)))
					{
						// 為了效能，先排除數字跟短字的模糊搜尋 
						if(isNumeric(sList.get(i)) || sList.get(i).length() < 4)
						{
							strSql += "(select pn, weight, fullword, kind from qeindex where word = '"
									+ sList.get(i) + "' order by weight desc limit " + sNumber + ") ";
							strSql += " union ";
						}
						else
						{
							strSql += "(select pn, weight, fullword, kind from qeindex where word like '"
									+ sList.get(i) + "%' order by weight desc limit " + sNumber + ") ";
							strSql += " union ";
						}
					}
				}
				
				*/

				strSql = strSql.substring(0, strSql.length() - 7);
				
				long startSqlTime = System.currentTimeMillis();
				
				List<IndexRate> sIndexRate = GetAllIndexRate(strSql, conn);
				
				long stopSqlTime = System.currentTimeMillis();
				elapsedSqlTime += stopSqlTime - startSqlTime;
				
				sCombine += strSql + ":" + elapsedSqlTime + ";";
				
				// 調整權證正確性
				for(IndexRate iter:sIndexRate)
				{
					String lngWord = "";
					String shtWord = "";
					float addWeight = 0f;
					
					// 查詢字與關鍵字比對長度後對換
					// 方便string compare時找頻率
					if(iter.getFullword().length() >= sKeyword.length())
					{
						lngWord = iter.getFullword();
						shtWord = sKeyword;
						
						// 部分相同
						addWeight = 2.5f;
					}
					else
					{
						lngWord = sKeyword;
						shtWord = iter.getFullword();
						
						// 查詢字大於關鍵字
						addWeight = 0.75f;
					}
					
					if(CountStringOccurrences(lngWord, shtWord) >= 1)
					{
						// 完全符合時提高
						if(lngWord.length() == shtWord.length())
							iter.setWeight(iter.getWeight() + 5);
						else
							iter.setWeight(iter.getWeight() + addWeight);
					}
					else
						iter.setWeight(0.01f);
					
					// 調整關鍵字(依照類別)
					for(IndexAdj adj:adjust)
					{
						if(adj.getWord().equalsIgnoreCase(iter.getFullword()) && adj.getKind()==iter.getKind())
							iter.setWeight(iter.getWeight() + adj.getAdjust());

						if(adj.getWord().equalsIgnoreCase(iter.getFullword()) && adj.getKind()!=iter.getKind())
							iter.setWeight(iter.getWeight() - 2);
					
					}
					
					if(PnWeightMap.containsKey(iter.getPn()))
		            {
						Float fWeight = PnWeightMap.get(iter.getPn());
						
						PnWeightMap.put(iter.getPn(), fWeight+iter.getWeight());

		            }
		            else {

		            	PnWeightMap.put(iter.getPn(), iter.getWeight());
		            }
				}
				
				
			}
		}
		
		// sort 後傳回
		sorted_map.putAll(PnWeightMap);
		
			
		
		List<String> sPnReturn = new ArrayList<String>();
		
		int iCount = 0;
		
		for(Map.Entry<String,Float> entry : sorted_map.entrySet()) {
			sPnReturn.add(entry.getKey());
			iCount++;
			
			if(iCount >= nTotal)
				break;
		}
		
		long stopTime = System.currentTimeMillis();
	    long elapsedTime = stopTime - startTime;
	    
	    // log query history
	    //InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine + "; " + sorted_map.toString(), conn);
	    //InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine, conn);
	    InsertQueryLog(strData, "AllTime: " + elapsedTime + ", SqlTime : " + elapsedSqlTime + ", Sql : " + sCombine + "; " + sPnReturn.toString(), conn);

		return sPnReturn;
	}
	
	protected void InsertQueryLog(String keyword, String sql, Connection conWriter)
	{
		PreparedStatement pst = null;
	
		try {


			String strSql = "INSERT INTO qeindexlog(keyword, sqlstr) VALUES(?, ?)";
            pst = conWriter.prepareStatement(strSql);
            pst.setString(1, keyword);
            pst.setString(2, sql);
            pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(fuzzysearch.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {

			attemptClose(pst);
			attemptClose(conWriter);

		}
	}
	
	protected boolean isNumeric(String str)
	{
	  return str.matches("-?\\d+(\\.\\d+)?");  //match a number with optional '-' and decimal.
	}

	
	protected int execUpdate(String strSql, Connection con) {

		int snum = 0;
		try {
			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = con.createStatement();
				snum = stmt.executeUpdate(strSql);

			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);
				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return snum;
	}
	
	protected void attemptClose(ResultSet o) {
		try {
			if (o != null)
				o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void attemptClose(Statement o) {
		try {
			if (o != null)
				o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void attemptClose(Connection o) {
		try {
			if (o != null)
				o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected boolean SkipWord(String strIn) {
		boolean bHave = false;
	
		if(SkipWord == null)
			SkipWord = strSkipWord.split(" ");

		for (String str : SkipWord) {
			if (strIn.trim().equalsIgnoreCase(str))
				bHave = true;
		}

		return bHave;
	}
	
	protected List<IndexRate> GetAllIndexRate(String strSql, Connection con) {

		List<IndexRate> sList = new ArrayList<IndexRate>();

		try {

			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = con.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexRate(rs.getString(1), rs.getFloat(2), rs.getString(3), rs.getInt(4)));
				// System.out.println(rs.getString(0));
			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sList;
	}
	
	protected int CountStringOccurrences(String text, String pattern) {
		// Loop through all instances of the string 'text'.
		int count = 0;
		int i = 0;
		while ((i = text.indexOf(pattern, i)) != -1) {
			i += pattern.length();
			count++;
		}
		return count;
	}
	
	protected Map<String, String> segmentData(String strData, CRFClassifier<CoreLabel> segmenter) {
		String [] strFullword = null;

        List<String> sList = new ArrayList<String>();
        List<String> sFullword = new ArrayList<String>();

        Map<String, String> scoreMap = new HashMap<String, String>();
        
        String delimiters = "[\\p{Punct}\\s]+";

        if(strData != null && !strData.isEmpty())
            strFullword = strData.split(delimiters);

        if(strFullword != null) {
            for (String stoken : strFullword) {

                if (SkipWord(stoken) || stoken.length() == 0)
                    continue;

                List<String> segmented = segmenter.segmentString(stoken);
                //System.out.println(segmented);


                if(segmented != null) {
                    for (String element : segmented) {

                        if (SkipWord(element) || element.length() == 0)
                            continue;

                        sList.add(element);
                        sFullword.add(stoken);
                        //System.out.println(element);
                    }
                }
            }
        }

        for(int i=0; i<sList.size(); i++)
        {
            float weight = 0.0f;

            int count = CountStringOccurrences(sFullword.get(i), sList.get(i));

            try {
                weight = (float) sList.get(i).length() / (float) sFullword.get(i).length() * count;
            }
            catch (Exception ex)
            {
                Logger lgr = Logger.getLogger(fuzzysearch.class.getName());
                lgr.log(Level.SEVERE, ex.getMessage(), ex);

                weight = 0.01f;
            }

            if(weight > 1)
                weight = 0.8f;


            if(scoreMap.containsKey(sList.get(i).toUpperCase()))
            {

                String sValue = scoreMap.get(sList.get(i).toUpperCase());
                String [] token = sValue.split(",");

                double score = Double.parseDouble(token[0]);

                score += weight;

                if(score > 1.0f)
                    score = Math.sqrt(score);

                String s = Double.toString(score);

                if(s.length() > 4)
                    s = s.substring(0, 4);

                s += "," + sFullword.get(i);

                scoreMap.put(sList.get(i).toUpperCase(), s);

            }
            else {
                String s = Float.toString(weight) + "," + sFullword.get(i);
                scoreMap.put(sList.get(i).toUpperCase(), s);
            }
        }

        return scoreMap;
	}
	
	protected void InsertPostgrel(String word, int page, float weight,
			int kind, String pn, String mfs, String catalog, String fullword, Connection conWriter) {
		PreparedStatement pst = null;
		
		

		try {


			String strSql = "INSERT INTO qeindex(word, page, weight, kind, pn, mfs, catalog, fullword) VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
            pst = conWriter.prepareStatement(strSql);
            pst.setString(1, word);
            pst.setInt(2, page);
            pst.setFloat(3, weight);
            pst.setInt(4, kind);
            pst.setString(5, pn);
            pst.setString(6, mfs);
            pst.setString(7, catalog);
            pst.setString(8, fullword);
            pst.executeUpdate();

		} catch (SQLException ex) {
			Logger lgr = Logger.getLogger(fuzzysearch.class.getName());
			lgr.log(Level.SEVERE, ex.getMessage(), ex);

		} finally {

			attemptClose(pst);
		}
	}
	
	protected void InsertAllWord(String pid, int kind, String pn, String mfs,
			String catalog, Map<String, String> scoreMap, Connection conn) {
		Iterator it = scoreMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());

			String[] token = pair.getValue().toString().split(",");

			InsertPostgrel(pair.getKey().toString(), Integer.parseInt(pid),
					Float.parseFloat(token[0]), kind, pn, mfs, catalog, token[1], conn);

			it.remove(); // avoids a ConcurrentModificationException
		}
	}
}

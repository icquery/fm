package com.gecpp.fm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.logging.Logger;
import java.util.logging.Level;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import java.sql.*;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.mchange.v2.c3p0.DataSources;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreLabel;


public class fuzzysearch implements Serializable {

	private String ReaderUrl;
	private String ReaderUser;
	private String ReaderPwd;

	private String basedir;

	private CRFClassifier<CoreLabel> segmenter = null;
 
	public CRFClassifier<CoreLabel> getSegmenter() {
		return segmenter;
	}

	private String strSkipWord = ", . ; + - | / \\ ' \" : ? < > [ ] { } ! @ # $ % ^ & * ( ) ~ ` _ － ‐ ， （ ）";
	private String[] SkipWord;

	private static DataSource  datasource;
    private ComboPooledDataSource cpds;

	public void fuzzysearch() {
		//InsertQueryLog("fuzzysearch", "fuzzysearch()");
	}

	public String getReaderUrl() {
		return ReaderUrl;
	}

	public String getReaderUser() {
		return ReaderUser;
	}

	public String getReaderPwd() {
		return ReaderPwd;
	}

	public int DeleteFuzzyRecord(int pid) {
		String strSql = "delete from qeindex where page = " + pid;

		execUpdate(strSql);
		return 0;
	}

	public int InsertFuzzyRecord(int pid, String pn, String mfs,
			String catalog, String description, String param) {
		String sPid = Integer.toString(pid);
		
		ProcessData(sPid, pn, mfs, catalog, description, param);
		return 0;
	}

	public void connectPostgrel() {
		
	
		
		try

		{
			/*
			Class.forName("org.postgresql.Driver").newInstance();
			DataSource unpooled = DataSources.unpooledDataSource(ReaderUrl,
					ReaderUser, ReaderPwd);

			Map overrides = new HashMap();
			overrides.put("initialPoolSize", "10");
			overrides.put("minPoolSize", "10");
			overrides.put("maxPoolSize", "30");
			overrides.put("acquireIncrement", "0");
			overrides.put("maxIdleTime", "300");
			overrides.put("maxStatements", "0");

			poolDataSource = DataSources.pooledDataSource(unpooled, overrides);
			*/
			cpds = new ComboPooledDataSource();
	        cpds.setDriverClass("org.postgresql.Driver"); //loads the jdbc driver
	        cpds.setJdbcUrl(ReaderUrl);
	        cpds.setUser(ReaderUser);
	        cpds.setPassword(ReaderPwd);

	        // the settings below are optional -- c3p0 can work with defaults
	        cpds.setMinPoolSize(10);
	        cpds.setAcquireIncrement(5);
	        cpds.setMaxPoolSize(70);
	        cpds.setMaxStatements(180);
	        
	        InsertQueryLog("fuzzysearch", "connectPostgrel()");

		} catch (Exception ee)

		{
			System.out.print(ee.getMessage());

		}
	}
	
	public void closePostgrel()
	{
		InsertQueryLog("fuzzysearch", "connectPostgrel()");
		
		try {
			
			DataSources.destroy(cpds);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	protected int execUpdate(String strSql) {

		int snum = 0;
		try {

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = cpds.getConnection();
				stmt = con.createStatement();
				snum = stmt.executeUpdate(strSql);

			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);
				attemptClose(con);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return snum;
	}
	
	public Connection GetDbConnection()
	{
		Connection conn = null;
		
		try {
			conn = this.cpds.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return conn;
	}
	
	protected List<IndexRate> GetAllIndexRate(String strSql) {

		List<IndexRate> sList = new ArrayList<IndexRate>();

		try {

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = cpds.getConnection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new IndexRate(rs.getString(1), rs.getFloat(2), rs.getString(3), rs.getInt(4)));
				// System.out.println(rs.getString(0));
			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);
				attemptClose(con);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sList;
	}

	protected List<String> execQuery(String strSql) {

		List<String> sList = new ArrayList<String>();

		try {

			Connection con = null;
			Statement stmt = null;
			ResultSet rs = null;

			try {
				con = cpds.getConnection();
				stmt = con.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(rs.getString(1));
				// System.out.println(rs.getString(0));
			}

			finally {

				attemptClose(rs);
				attemptClose(stmt);
				attemptClose(con);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return sList;
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

	protected void loadParams() {
		
		//InsertQueryLog("fuzzysearch", "loadParams()");

		Context envurl, envusr, envpwd, envbase;
		String entryurl = null, entryusr = null, entrypwd = null, entrybase = null;

		try {
			envurl = (Context) new InitialContext().lookup("java:comp/env");
			entryurl = (String) envurl.lookup("fm.param.url");

			envusr = (Context) new InitialContext().lookup("java:comp/env");
			entryusr = (String) envusr.lookup("fm.param.user");

			envpwd = (Context) new InitialContext().lookup("java:comp/env");
			entrypwd = (String) envpwd.lookup("fm.param.pwd");

			envbase = (Context) new InitialContext().lookup("java:comp/env");
			entrybase = (String) envbase.lookup("fm.param.base");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		ReaderUrl = entryurl;
		ReaderUser = entryusr;
		ReaderPwd = entrypwd;
		// URL path =
		// this.getClass().getProtectionDomain().getCodeSource().getLocation();
		// ReaderPwd = path.toString();
		basedir = entrybase;

		// ReaderUrl = "fm.param.url";
		// ReaderUser = "fm.param.user";
		// ReaderPwd = "fm.param.pwd";

		SkipWord = strSkipWord.split(" ");

		initCoreLabelCRFClassifier();
	}

	protected void initCoreLabelCRFClassifier() {

		try {
			System.setOut(new PrintStream(System.out, true, "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		Properties props = new Properties();
		props.setProperty("sighanCorporaDict", basedir + "/data");
		// props.setProperty("NormalizationTable", "data/norm.simp.utf8");
		// props.setProperty("normTableEncoding", "UTF-8");
		// below is needed because CTBSegDocumentIteratorFactory accesses it
		props.setProperty("serDictionary", basedir + "/data/dict-chris6.ser.gz");

		props.setProperty("inputEncoding", "UTF-8");
		props.setProperty("sighanPostProcessing", "true");

		segmenter = new CRFClassifier<CoreLabel>(props);
		segmenter.loadClassifierNoExceptions(basedir + "/data/ctb.gz", props);

	}

	protected boolean SkipWord(String strIn) {
		boolean bHave = false;

		for (String str : SkipWord) {
			if (strIn.trim().equalsIgnoreCase(str))
				bHave = true;
		}

		return bHave;
	}
	
	public String DebugGetQuery(String strData) {
		String[] strFullword = null;
		List<String> sList = new ArrayList<String>();
		String sNumber = "1000";
		String sTotal = "100";
		
		String delimiters = "[\\p{Punct}\\s]+";
		
		if (strData != null && !strData.isEmpty())
		{
			strData = strData.toUpperCase();
			strFullword = strData.split(delimiters);
		}
		else
			return "";

		String strSql = "";

		strSql = "select eq.pn, SUM(weight) weight from ( ";

		if (strFullword != null) {
			for (String stoken : strFullword) {

				if (SkipWord(stoken) || stoken.length() == 0)
					continue;

				List<String> segmented = segmenter.segmentString(stoken);
				// System.out.println(segmented);

				// List<String> segmented = new ArrayList<String>();

				// segmented.add(stoken);

				if (segmented != null) {
					for (String element : segmented) {

						if (SkipWord(element) || element.length() == 0)
							continue;

						sList.add(element);

					}
				}
			}
		}

		for (int i = 0; i < sList.size(); i++) {
			strSql += "(select pn, weight from qeindex where word like '"
					+ sList.get(i) + "%' order by weight desc limit " + sNumber + ") ";
			strSql += " union ";

		}

		strSql = strSql.substring(0, strSql.length() - 7) + ") eq "
				+ "group by eq.pn order by SUM(weight) desc limit " + sTotal;

		return  strSql;
	}

	public List<String> GetQuery(String strData) {
		String[] strFullword = null;
		List<String> sList = new ArrayList<String>();
		String sNumber = "1000";
		String sTotal = "100";
		
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
	
				if (segmented != null) {
					for (String element : segmented) {

						if (SkipWord(element) || element.length() == 0)
							continue;

						sList.add(element);
						
						sCombine += element + " ";

					}
				}
				
				// 再加一個不要段字的
				sList.remove(stoken);
				sList.add(stoken);
				
				for (int i = 0; i < sList.size(); i++) {
					strSql += "(select pn, weight, fullword, kind from qeindex where word like '"
							+ sList.get(i) + "%' order by weight desc limit " + sNumber + ") ";
					strSql += " union ";

				}

				strSql = strSql.substring(0, strSql.length() - 7);
				
				List<IndexRate> sIndexRate = GetAllIndexRate(strSql);
				
				// 調整權證正確性
				for(IndexRate iter:sIndexRate)
				{
					String lngWord = "";
					String shtWord = "";
					float addWeight = 0f;
					
					if(iter.getFullword().length() >= sKeyword.length())
					{
						lngWord = iter.getFullword();
						shtWord = sKeyword;
						
						addWeight = 2.5f;
					}
					else
					{
						lngWord = sKeyword;
						shtWord = iter.getFullword();
						
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
		
		// log query history
		InsertQueryLog(strData, sorted_map.toString());		
		
		List<String> sPnReturn = new ArrayList<String>();
		
		for(Map.Entry<String,Float> entry : sorted_map.entrySet()) {
			sPnReturn.add(entry.getKey());
		}

		return sPnReturn;
	}

	
	protected void InsertQueryLog(String keyword, String sql)
	{
		PreparedStatement pst = null;
		Connection conWriter = null;
		

		try {

			conWriter = cpds.getConnection();

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

	protected Map<String, String> segmentData(String strData) {
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
			int kind, String pn, String mfs, String catalog, String fullword) {
		PreparedStatement pst = null;
		Connection conWriter = null;
		
		// first delete old data
		DeleteFuzzyRecord(page);

		try {

			conWriter = cpds.getConnection();

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
			attemptClose(conWriter);

		}
	}

	protected void ProcessData(String pid, String pn, String mfs,
			String catalog, String description, String param) {
		Map<String, String> scoreMap = null;

		// 清除雜訊
		if (description != null && !description.isEmpty())
			description.replaceAll("[\"\']", "");
		if (param != null && !param.isEmpty())
			param.replaceAll("[\"\']", "");

		// 料號
		scoreMap = segmentData(pn);

		// 料號需有完整紀錄
		if (!scoreMap.containsKey(pn)) {
			InsertPostgrel(pn, Integer.parseInt(pid), 1, 0, pn, mfs, catalog, pn);
		}

		InsertAllWord(pid, 0, pn,mfs, catalog, scoreMap);

		// mfs
		scoreMap = segmentData(mfs);
		InsertAllWord(pid, 1, pn,mfs, catalog, scoreMap);

		// catalog
		scoreMap = segmentData(catalog);
		InsertAllWord(pid, 2, pn,mfs, catalog, scoreMap);

		// description
		scoreMap = segmentData(description);
		InsertAllWord(pid, 3, pn,mfs, catalog, scoreMap);

		// param
		scoreMap = segmentData(param);
		InsertAllWord(pid, 4, pn,mfs, catalog, scoreMap);
	}

	protected void InsertAllWord(String pid, int kind, String pn, String mfs,
			String catalog, Map<String, String> scoreMap) {
		Iterator it = scoreMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			// System.out.println(pair.getKey() + " = " + pair.getValue());

			String[] token = pair.getValue().toString().split(",");

			InsertPostgrel(pair.getKey().toString(), Integer.parseInt(pid),
					Float.parseFloat(token[0]), kind, pn, mfs, catalog, token[1]);

			it.remove(); // avoids a ConcurrentModificationException
		}
	}

}

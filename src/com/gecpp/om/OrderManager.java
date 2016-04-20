package com.gecpp.om;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.gecpp.fm.OrderResult;
import com.gecpp.fm.QueryResult;
import com.gecpp.fm.fuzzysearch;
import com.gecpp.fm.Dao.MultiKeyword;
import com.gecpp.fm.Dao.Product;
import com.gecpp.fm.Logic.OmSearchLogic;
import com.gecpp.fm.Util.CommonUtil;
import com.gecpp.fm.Util.SortUtil;

class OrdManaerComparator implements Comparator<String> {

    Map<String, Integer> base;
    public OrdManaerComparator(Map<String, Integer> base) {
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


public class OrderManager {
	
	
 static final boolean DEBUG_BUILD = true;
	
	private static String OmUrl;
	private static String OmUser;
	private static String OmPwd;
	
	private static String fmUrl;
	private static String fmUser;
	private static String fmPwd;
	
	private String []  pns = null;
	
	// 20160112 多料號搜尋
	private static final String getAllInfoByPn_headMulti = "SELECT a.inventory, a.offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs, b.supplier_id, c.abbreviation as supplier "
			+ "FROM pm_product b  LEFT JOIN pm_store_price a on a.product_id = b.id and (a.valid =1 OR a.valid IS NULL) "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.pn in(";
	
	private static final String getAllInfoByPn_head = "SELECT a.inventory, a.offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs, b.supplier_id "
			+ "FROM pm_product b  LEFT JOIN pm_store_price a on a.product_id = b.id and (a.valid =1 OR a.valid IS NULL) "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.pn in(";
	
	private static final String getAllInfoById_head = "SELECT a.inventory, a.offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs, b.supplier_id "
			+ "FROM pm_product b  LEFT JOIN pm_store_price a on a.product_id = b.id and (a.valid =1 OR a.valid IS NULL) "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.id in(";
	
	private static final String getAllInfoByPn_foot = ") and b.supplier_id = c.id AND b.status is null order by b.pn, c.TYPE";
	
	private Connection om_conn = null;
	
	//private Connection fm_conn = null;

	protected void loadParams() {
		
			Context envurl;
			String entry;

			try {
				
				if(DEBUG_BUILD == true)
				{
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.url.debug");
					OmUrl = entry;
		
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.user.debug");
					OmUser = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.pwd.debug");
					OmPwd = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.url.debug");
					fmUrl = entry;
		
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.user.debug");
					fmUser = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.pwd.debug");
					fmPwd = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.redis.debug");
				
				}
				else
				{
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.url");
					OmUrl = entry;
		
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.user");
					OmUser = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.pwd");
					OmPwd = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.url");
					fmUrl = entry;
		
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.user");
					fmUser = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("fm.param.pwd");
					fmPwd = entry;
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.redis");
				
				}

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		}
	}
	
	protected Connection getOmPgSqlConnection() throws Exception {
        String driver = "org.postgresql.Driver";
        String url = OmUrl;
        String username = OmUser;
        String password = OmPwd;
        Class.forName(driver); // load MySQL driver
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }
	
	protected Connection getfmPgSqlConnection() throws Exception {
        String driver = "org.postgresql.Driver";
        String url = fmUrl;
        String username = fmUser;
        String password = fmPwd;
        Class.forName(driver); // load MySQL driver
        Connection conn = DriverManager.getConnection(url, username, password);
        return conn;
    }


    protected void connectPostgrel(){
        try

        {

            String url = "";
            om_conn = getOmPgSqlConnection();

            om_conn.setAutoCommit(true);

            //fm_conn = getfmPgSqlConnection();

            //fm_conn.setAutoCommit(true);


        } catch (Exception ee)

        {
            System.out.print(ee.getMessage());

        }
    }
    
    public OrderResult getProductByGroupInStoreId(List<String> notRepeatPns) {
		
		if(notRepeatPns == null)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
			
		if(notRepeatPns.size() == 0)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
		
		loadParams();
		
		connectPostgrel();
		
		
		String pnsSql = createIdSql(notRepeatPns);
		
		List<Product> pkey = new ArrayList<>();
		
		pkey = getAllInforByIdLike(pnsSql);
        
		pkey = dealWithWebPListRepeat(pkey);
		
		OrderResult result = formatFromProductList(pkey);

        result = orderProductList(result);
        
        attemptClose(om_conn);
		
		return result;
	}
    
    /* 深度搜尋 by PN */
    public OrderResult getProductByGroupInStoreDeep(int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation, 
			List<String> notRepeatPns,
			int currentPage, 
			int pageSize)
    {
    	if(notRepeatPns == null)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			result.setPns(new String[0]);
			return result;
		}
			
		if(notRepeatPns.size() == 0)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			result.setPns(new String[0]);
			return result;
		}
		
		loadParams();
		
		connectPostgrel();
		
		List<Product> plist = new ArrayList<>();
		List<Product> OmList = new ArrayList<>();
		
		String pnsSql = OmSearchLogic.getFormatPn(notRepeatPns);
		String strSql = OmSearchLogic.getAllInforByPnList(pnsSql, inventory, lead, rohs, mfs, abbreviation);
		
		plist = formatToProductList(strSql, om_conn);
		
		//InsertQueryLog("getProductByGroupInStoreDeep", strSql, om_conn);
		
		plist = dealWithWebPListRepeat(plist);
		
		// 分頁在此做
		OmList = OmSearchLogic.pageData(plist, currentPage, pageSize);
		
		OrderResult result = formatFromProductList(OmList);

        result = orderProductList(result);
        result.setTotalCount(OmSearchLogic.pageCount(plist));
        
        
        attemptClose(om_conn);
		
		return result;
    }
    
    /* 深度搜尋 by Id */
    public OrderResult getProductByGroupInStoreIdDeep(int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation, 
			List<String> notRepeatPns,
			int currentPage, 
			int pageSize)
    {
    	if(notRepeatPns == null)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
			
		if(notRepeatPns.size() == 0)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
		
		loadParams();
		
		connectPostgrel();
		
		List<Product> plist = new ArrayList<>();
		List<Product> OmList = new ArrayList<>();
		
		String pnsSql = OmSearchLogic.getFormatId(notRepeatPns);
		String strSql = OmSearchLogic.getAllInforByIdList(pnsSql, inventory, lead, rohs, mfs, abbreviation);
		
		plist = formatToProductList(strSql, om_conn);
		
		//InsertQueryLog("getProductByGroupInStoreDeep", strSql, om_conn);
		
		plist = dealWithWebPListRepeat(plist);
		
		// 分頁在此做
		OmList = OmSearchLogic.pageData(plist, currentPage, pageSize);
		
		OrderResult result = formatFromProductList(OmList);

        result = orderProductList(result);
        result.setTotalCount(OmSearchLogic.pageCount(plist));
        
        attemptClose(om_conn);
		
		return result;
    }
    
    // 20160112 多料號搜尋
    public ArrayList<MultiKeyword> getProductByMultikeywordLike(ArrayList<MultiKeyword> notRepeatPns) {
		
		if(notRepeatPns == null)
		{
			return notRepeatPns;
		}
			
		if(notRepeatPns.size() == 0)
		{
			return notRepeatPns;
		}
		
		loadParams();
		
		connectPostgrel();
		
		
		// 先把所有料號給pm去搜尋
		for (int i = 0; i < notRepeatPns.size(); i++) {
			
			// 之前搜尋不到的才查詢
			if(notRepeatPns.get(i).getPkey().size() != 0)
			{
				continue;
			}
			
			List<Product> pkey = new ArrayList<>();
            String s = notRepeatPns.get(i).getKeyword();
            
            String pnkey = CommonUtil.parsePnKey(s);
 
            pkey = getAllInforByPnMultiLike(pnkey);
            
            if(pkey.size() != 0)
            	notRepeatPns.get(i).setSearchtype(2);
            
            notRepeatPns.get(i).setPkey(pkey);
        }
	
        
        attemptClose(om_conn);
		
		return notRepeatPns;
	}
    
    // 20160112 多料號搜尋
    public ArrayList<MultiKeyword> getProductByMultikeyword(ArrayList<MultiKeyword> notRepeatPns) {
		
		if(notRepeatPns == null)
		{
			return notRepeatPns;
		}
			
		if(notRepeatPns.size() == 0)
		{
			return notRepeatPns;
		}
		
		loadParams();
		
		connectPostgrel();
		
		
		// 先把所有料號給pm去搜尋
		for (int i = 0; i < notRepeatPns.size(); i++) {
			List<Product> pkey = new ArrayList<>();
            String s = notRepeatPns.get(i).getKeyword();
            
            String pnkey = CommonUtil.parsePnKeyNoLike(s);
 
            pkey = getAllInforByPnMulti(pnkey);
            
            if(pkey.size() != 0)
            	notRepeatPns.get(i).setSearchtype(1);
            else
            	notRepeatPns.get(i).setSearchtype(0);
            
            notRepeatPns.get(i).setPkey(pkey);
        }
	
        
        attemptClose(om_conn);
		
		return notRepeatPns;
	}
    
 // 20160112 多料號搜尋
    public List<Product> getProductByMultiRedis(String pn) {
		
		
		
		loadParams();
		
		connectPostgrel();
		
		
		List<Product> pkey = new ArrayList<>();
        String s = pn;
            
        String pnkey = CommonUtil.parsePnKeyNoLike(s);
 
        pkey = getAllInforByPnMulti(pnkey);
            
        
        attemptClose(om_conn);
		
		return pkey;
	}

	public OrderResult getProductByGroupInStore(List<String> notRepeatPns) {
		
		if(notRepeatPns == null)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
			
		if(notRepeatPns.size() == 0)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
		
		loadParams();
		
		connectPostgrel();
		
		List<Product> plist = new ArrayList<>();
		
		
		//String pnsSql = createPnSql(notRepeatPns);
		
		for (int i = 0; i < notRepeatPns.size(); i++) {
			List<Product> pkey = new ArrayList<>();
            String s = notRepeatPns.get(i);
 
            pkey = getAllInforByPnLike(s);
            
            plist.addAll(pkey);
        }
		
		//plist = getAllInforByPnFuzzy(pnsSql);
		
		plist = dealWithWebPListRepeat(plist);
		
		OrderResult result = formatFromProductList(plist);

        result = orderProductList(result);
        
        attemptClose(om_conn);
		
		return result;
	}
	
	// 2016/02/16 新增依照製造商排序
	

	private OrderResult orderProductList(OrderResult result)
    {
        LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = result.getPidList();
        returnMap = sortHashMapByValuesD(returnMap);

        result.setPidList(returnMap);
        
        // 20150908依照歡平所需的欄位給予
        // setup 当前页所有产品的id列表(List<PID>)
        List<Integer> lstPID = GetPID(returnMap);
        
        result.setIds(lstPID);
        result.setPns(pns);

        return result;
    }
	
	private List<Integer> GetPID(LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> passedMap)
	{
		List<Integer> returnPID = new ArrayList<Integer>();
		List<String> returnPns = new ArrayList<String>();
		

        for (Map.Entry<String, LinkedHashMap<String, List<Integer>>> entry : passedMap.entrySet()) {
            String key = entry.getKey();
            
            returnPns.add(key);
           
            Map<String, List<Integer>> value = entry.getValue();


            for(Map.Entry<String, List<Integer>> subentry : value.entrySet())
            {
                String subkey = subentry.getKey();

                List<Integer> subvalue = subentry.getValue();

                for(Integer listvalue:subvalue)
                {
                	if(!returnPID.contains(listvalue))
                		returnPID.add(listvalue);
                }
            }
        }
        
        pns = new String[returnPns.size()];
        
        for(int i=0; i<returnPns.size(); i++)
        {
        	pns[i] = returnPns.get(i);
        }
        
        return returnPID;
        
	}

    private LinkedHashMap sortHashMapByValuesD(LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> passedMap) {

        // 找各料號下面的項目多寡，多的排前面
        HashMap<String, Integer> PnOrderMap = new HashMap<String, Integer>();
        OrdManaerComparator ovc =  new OrdManaerComparator(PnOrderMap);
        TreeMap<String,Integer> ord_map = new TreeMap<String,Integer>(ovc);

        for (Map.Entry<String, LinkedHashMap<String, List<Integer>>> entry : passedMap.entrySet()) {
            String key = entry.getKey();
            int count = 0;
            Map<String, List<Integer>> value = entry.getValue();

            //System.out.println(key + ":");

            for(Map.Entry<String, List<Integer>> subentry : value.entrySet())
            {
                String subkey = subentry.getKey();

                //System.out.println("    " + subkey + ":");

                List<Integer> subvalue = subentry.getValue();
                
                Set<Integer> foo = new HashSet<Integer>(subvalue);

                count += foo.size();
            }

            PnOrderMap.put(key, count);

        }

        ord_map.putAll(PnOrderMap);

        LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();


        for(Map.Entry<String,Integer> entry : ord_map.entrySet()) {

        	LinkedHashMap<String, List<Integer>> value = passedMap.get(entry.getKey());
            
            // 整理一下 id(刪除重複的)
            for(Map.Entry<String, List<Integer>> listentry:value.entrySet()){
            	List<Integer> listvalue = listentry.getValue();
            	List<Integer> newList = new ArrayList<Integer>();
            	
            	for(Integer idvalue : listvalue)
            	{
            		if(!newList.contains(idvalue))
            			newList.add(idvalue);
            	}
            	
            	// 再把整理過的id list放回去
            	listentry.setValue(newList);
            }

            // 20160221 order by mfs
            returnMap.put(entry.getKey(), SortUtil.RegroupIndexResultByMfs(value));
        }

        return returnMap;
    }
    
    // 20160112 多料號搜尋
    public QueryResult formatFromMultiKeyword(ArrayList<MultiKeyword> notRepeatPns)
    {
    	QueryResult result = new QueryResult();
    	
    	/*
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapMfs1 = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapMfs2 = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapMfs3 = new LinkedHashMap<String, Map<String, List<Integer>>>();
   
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapSupplier1 = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapSupplier2 = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMapSupplier3 = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	
    	result.setPidListGroupMfs1(returnMapMfs1);
    	result.setPidListGroupMfs2(returnMapMfs2);
    	result.setPidListGroupMfs3(returnMapMfs3);
    	
    	result.setPidListGroupSupplier1(returnMapSupplier1);
    	result.setPidListGroupSupplier2(returnMapSupplier2);
    	result.setPidListGroupSupplier3(returnMapSupplier3);
    	*/
    	
    	
    	LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>> returnMapMfs1 = new LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>>();
    	LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>> returnMapMfs2 = new LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>>();
    	LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>> returnMapMfs3 = new LinkedHashMap<String, Map<String, Map<Integer, List<Integer>>>>();
   
    	result.setPidListGroupMfs1(returnMapMfs1);
    	result.setPidListGroupMfs2(returnMapMfs2);
    	result.setPidListGroupMfs3(returnMapMfs3);
    	
    	
    	for(MultiKeyword key : notRepeatPns)
    	{
    		int amount = key.getCount();
    		String pn = key.getKeyword();
    		List<Product> plist = key.getPkey();
    		
    		// 完全匹配
    		if(key.getSearchtype() == 1)
    		{
    			
    			List<Product> newList = new ArrayList<Product>();
    			
    			for(Product product :plist)
    			{
    				//Map<String, String> res = CommonUtil.ParsePrice(product.getPrice());
    				// 2016/03/14  也需有價格資訊
    				if(product.getInventory() >= amount && product.getPrice() != "" && product.getPrice() != null)
    					newList.add(product);
    			}
    			
    			// 符合庫存
    			if(newList.size() > 0)
    			{
    				//returnMapMfs1.put(pn, formatMapFromProductListMfs(pn, newList));
    				//returnMapSupplier1.put(pn, formatMapFromProductListSupplier(pn, newList));
    				returnMapMfs1.put(pn, formatMapFromProductListMfsSupplier(pn, newList));
    			}
    			else	// 部分匹配
    			{
    				//returnMapMfs2.put(pn, formatMapFromProductListMfs(pn, plist));
    				//returnMapSupplier2.put(pn, formatMapFromProductListSupplier(pn, plist));
    				returnMapMfs2.put(pn, formatMapFromProductListMfsSupplier(pn, plist));
    			}
    		}
    		
    		// 料號不符，部分匹配
    		if(key.getSearchtype() == 2)
    		{
    			//returnMapMfs2.put(pn, formatMapFromProductListMfs(pn, plist));
    			//returnMapSupplier2.put(pn, formatMapFromProductListSupplier(pn, plist));
    			returnMapMfs2.put(pn, formatMapFromProductListMfsSupplier(pn, plist));
    		}
    		
    		// 完全不匹配 
    		if(key.getSearchtype() == 0 || key.getSearchtype() == 3)
    		{
    			//returnMapMfs3.put(pn, formatMapFromProductListMfs(pn, plist));
    			//returnMapSupplier3.put(pn, formatMapFromProductListSupplier(pn, plist));
    			returnMapMfs3.put(pn, formatMapFromProductListMfsSupplier(pn, plist));
    		}
    		
    	}
    	
    	return result;
    }
    
    // 20160415 多料號搜尋(Leo更改規格 to Map<pn,Map<mfs,Map<supplier_id,List<pid>>>>)
    private Map<String, Map<Integer, List<Integer>>> formatMapFromProductListMfsSupplier(String pnkey, List<Product> plist) {
    	
    	Map<String, Map<Integer, List<Integer>>> mfsGroupMapInt = new  LinkedHashMap<String, Map<Integer, List<Integer>>>();
    
    	for (Product pro : plist) {
    		
    		String mfs = pro.getMfs();
    		Integer supplier_id = pro.getSupplierid();

    		if (mfs != null && mfs.trim().length() > 0) {

                if(mfsGroupMapInt == null) {
                	mfsGroupMapInt = new LinkedHashMap<String, Map<Integer, List<Integer>>>();
                	
                }
                
                Map<Integer, List<Integer>> supplierMap = mfsGroupMapInt.get(mfs);
                if (supplierMap == null) {
                	supplierMap = new HashMap<Integer, List<Integer>>();
                	mfsGroupMapInt.put(mfs, supplierMap);
                }
                
                List<Integer> idlist = supplierMap.get(supplier_id);
                if(idlist == null)
                {
                	idlist = new ArrayList<Integer>();
                	supplierMap.put(supplier_id, idlist);
                }
            
                boolean existFlat = false;
                for (Integer id : idlist) {
                    int productId = pro.getId();
                    
                    if (productId == id) {
                        existFlat = true;
                        break;
                    }
                }
                //add if not exist
                if (!existFlat) 
                	idlist.add(pro.getId());
                
            }
    	}
    	
    	return mfsGroupMapInt;
    }
    
    // 20160112 多料號搜尋
    private Map<String, List<Integer>> formatMapFromProductListMfs(String pnkey, List<Product> plist) {
    	
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	
    	Map<String, List<Integer>> mfsGroupMapInt = new  LinkedHashMap<String, List<Integer>>();
    
    	for (Product pro : plist) {
    		
    		String mfs = pro.getMfs();
    		
    		
    		if (mfs != null && mfs.trim().length() > 0) {
                
                
                if(mfsGroupMapInt == null) {
                	mfsGroupMapInt = new LinkedHashMap<String, List<Integer>>();
                	returnMap.put(pnkey, mfsGroupMapInt);
                }
                
                List<Integer> listInt = mfsGroupMapInt.get(mfs);
                if (listInt == null) {
                	listInt = new ArrayList<Integer>();
                	mfsGroupMapInt.put(mfs, listInt);
                }
            
                listInt.add(pro.getId());
            }
    	}
    	
    	return mfsGroupMapInt;
    }
    
 // 20160112 多料號搜尋Supplier
    private Map<String, List<Integer>> formatMapFromProductListSupplier(String pnkey, List<Product> plist) {
    	
    	LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();
    	
    	Map<String, List<Integer>> mfsGroupMapInt = new  LinkedHashMap<String, List<Integer>>();
    
    	for (Product pro : plist) {
    		
    		String supplier = pro.getSupplier();
    		
    		
    		if (supplier != null && supplier.trim().length() > 0) {
                
                
                if(mfsGroupMapInt == null) {
                	mfsGroupMapInt = new LinkedHashMap<String, List<Integer>>();
                	returnMap.put(pnkey, mfsGroupMapInt);
                }
                
                List<Integer> listInt = mfsGroupMapInt.get(supplier);
                if (listInt == null) {
                	listInt = new ArrayList<Integer>();
                	mfsGroupMapInt.put(supplier, listInt);
                }
            
                listInt.add(pro.getId());
            }
    	}
    	
    	return mfsGroupMapInt;
    }
    
    private List<Product> orderFromProductList(List<Product> plist) {
		OrderResult result = new OrderResult();
		// 20160407 以supplier數量排序  
		//儲存supplier數量
        LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> supplierMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();
        LinkedHashMap<String, LinkedHashMap<String, List<Product>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Product>>>();
        List<Product> orderReturnList = new ArrayList<Product>();

        Map<Integer, Product> pnProductMap = new HashMap<Integer, Product>();

        LinkedHashMap<String, String> pnMap = new LinkedHashMap<String, String>();

//        List<Product> needUpdatedProducts = new ArrayList<>();
     // 根据pn进行存储
        for (Product pro : plist) {
            String pnkey = pro.getPn();
            if (pnMap.get(pnkey) == null) {
                pnMap.put(pnkey, pnkey);
                
            }

            Integer id = pro.getId();
            boolean addToListflag = true;

            pnProductMap.put(id, pro);


            if (addToListflag) {

            	LinkedHashMap<String, List<Integer>> groupSupplierMap = supplierMap.get(pnkey);
            	LinkedHashMap<String, List<Product>> mfsGroupMapInt = returnMap.get(pnkey);
                
                String mfs = pro.getMfs();
                if (mfs == null) {
                    mfs = pro.getMfs();
                }

                if (mfs != null && mfs.trim().length() > 0) {
                    if (groupSupplierMap == null) {
                    	groupSupplierMap = new LinkedHashMap<String, List<Integer>>();
                        supplierMap.put(pnkey, groupSupplierMap);
                    }
                    
                    if(mfsGroupMapInt == null) {
                    	mfsGroupMapInt = new LinkedHashMap<String, List<Product>>();
                    	returnMap.put(pnkey, mfsGroupMapInt);
                    }

                    List<Integer> listSupplier = groupSupplierMap.get(mfs);
                    if (listSupplier == null) {
                    	listSupplier = new ArrayList<Integer>();
                        groupSupplierMap.put(mfs, listSupplier);
                    }
                    
                    List<Product> listInt = mfsGroupMapInt.get(mfs);
                    if (listInt == null) {
                    	listInt = new ArrayList<Product>();
                    	mfsGroupMapInt.put(mfs, listInt);
                    }
                
                    boolean addSupplier = true;
                    for(Integer supplierId : listSupplier)
                    {
                    	//if(supplierId == pro.getSupplierid())
                    	if(supplierId == pro.getId())
                    	{
                    		addSupplier = false;
                    		break;
                    	}
                    }
                    if(addSupplier)
                    {
                    	//listSupplier.add(pro.getSupplierid());
                    	listSupplier.add(pro.getId());
                    }
                    
                    listInt.add(pro);
                }
            }

        }
        
        supplierMap = sortHashMapByValuesD(supplierMap);
        
        // ReOrder returnMap by supplier order
        for (Map.Entry<String, LinkedHashMap<String, List<Integer>>> entry : supplierMap.entrySet()) {
            String key = entry.getKey();
          
            Map<String, List<Product>> value = returnMap.get(key);


            for(Map.Entry<String, List<Product>> subentry : value.entrySet())
            {
                String subkey = subentry.getKey();

  
                List<Product> subvalue = subentry.getValue();

                orderReturnList.addAll(subvalue);
                
            }
        }

        return orderReturnList;
    }

	private OrderResult formatFromProductList(List<Product> plist) {
		OrderResult result = new OrderResult();
        LinkedHashMap<String, LinkedHashMap<String, List<Product>>> resultMap = new LinkedHashMap<String, LinkedHashMap<String, List<Product>>>();
        LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> returnMap = new LinkedHashMap<String, LinkedHashMap<String, List<Integer>>>();

        Map<Integer, Product> pnProductMap = new HashMap<Integer, Product>();

        LinkedHashMap<String, String> pnMap = new LinkedHashMap<String, String>();

//        List<Product> needUpdatedProducts = new ArrayList<>();
        // 根据pn进行存储
        for (Product pro : plist) {
            String pnkey = pro.getPn();
            if (pnMap.get(pnkey) == null) {
                pnMap.put(pnkey, pnkey);
                
            }

            Integer id = pro.getId();
            boolean addToListflag = true;

            pnProductMap.put(id, pro);


            if (addToListflag) {

            	LinkedHashMap<String, List<Product>> mfsGroupMap = resultMap.get(pnkey);
            	LinkedHashMap<String, List<Integer>> mfsGroupMapInt = returnMap.get(pnkey);
                
                String mfs = pro.getMfs();
                if (mfs == null) {
                    mfs = pro.getMfs();
                }

                if (mfs != null && mfs.trim().length() > 0) {
                    if (mfsGroupMap == null) {
                        mfsGroupMap = new LinkedHashMap<String, List<Product>>();
                        resultMap.put(pnkey, mfsGroupMap);
                    }
                    
                    if(mfsGroupMapInt == null) {
                    	mfsGroupMapInt = new LinkedHashMap<String, List<Integer>>();
                    	returnMap.put(pnkey, mfsGroupMapInt);
                    }

                    List<Product> list = mfsGroupMap.get(mfs);
                    if (list == null) {
                        list = new ArrayList<Product>();
                        mfsGroupMap.put(mfs, list);
                    }
                    
                    List<Integer> listInt = mfsGroupMapInt.get(mfs);
                    if (listInt == null) {
                    	listInt = new ArrayList<Integer>();
                    	mfsGroupMapInt.put(mfs, listInt);
                    }
                
                    list.add(pro);
                    listInt.add(pro.getId());
                }
            }

        }
        
        result.setPidList(returnMap);
        
        Set<String> keySet = pnMap.keySet();
       

        return result;
    }
	
	private String createIdSql(List<String> pns) {
        String pnSql = "";

        int pnsCount = pns.size();

        if (pns != null && pnsCount > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < pnsCount; i++) {
                String s = pns.get(i);
                stringBuilder.append(s).append(",");
            }
            pnSql = stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            pnSql = "";
        }

        return pnSql;
    }
	
	private String createPnSql(List<String> pns) {
        String pnSql = "";

        int pnsCount = pns.size();

        if (pns != null && pnsCount > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            //只取前面的20个料号
            pnsCount = pnsCount > 20 ? 20 : pnsCount;
            for (int i = 0; i < pnsCount; i++) {
                String s = pns.get(i);
                stringBuilder.append("'").append(s).append("',");
            }
            pnSql = stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            pnSql = "''";
        }

        return pnSql;
    }
	
	private List<Product> getAllInforByPnFuzzy(String pnkey) {
		 
		String strSql = getAllInfoByPn_head +  "( SELECT pn FROM pm_supplier_pn WHERE supplier_pn_key in (" + pnkey + ") LIMIT 20 ) "
						+ "UNION ( SELECT pn FROM pm_pn WHERE pn_key in (" + pnkey + ")  LIMIT 20 ) ORDER BY pn LIMIT 20"  
						+ getAllInfoByPn_foot;
		
		
		long startSqlTime = System.currentTimeMillis();
		
		List<Product> plist = formatToProductList(strSql, om_conn);
		
		long stopSqlTime = System.currentTimeMillis();
		long elapsedSqlTime = stopSqlTime - startSqlTime;
		
		//InsertQueryLog("getAllInforByPnFuzzy", "Time:" + elapsedSqlTime + strSql, fm_conn);
        
		return plist;
	}
	
	// 20160112 多料號搜尋
	private List<Product> getAllInforByPnMulti(String pnkey) {
		 
		String strSql = getAllInfoByPn_headMulti +  "'" + pnkey + "' "  
				+ getAllInfoByPn_foot;
		
		
		long startSqlTime = System.currentTimeMillis();
		
		List<Product> plist = formatToProductListMulti(strSql, om_conn);
		
		long stopSqlTime = System.currentTimeMillis();
		long elapsedSqlTime = stopSqlTime - startSqlTime;
		
		//InsertQueryLog("getAllInforByPnFuzzy", "Time:" + elapsedSqlTime + strSql, fm_conn);
        
		return plist;
	}
	
	// 20160112 多料號搜尋
		private List<Product> getAllInforByPnMultiLike(String pnkey) {
			 
			String strSql = getAllInfoByPn_headMulti +  "( SELECT pn FROM pm_supplier_pn WHERE supplier_pn_key like ('" + pnkey + "') LIMIT 20 ) "
							+ "UNION ( SELECT pn FROM pm_pn WHERE pn_key like ('" + pnkey + "')  LIMIT 20 ) ORDER BY pn LIMIT 20"  
							+ getAllInfoByPn_foot;
			
			
			long startSqlTime = System.currentTimeMillis();
			
			List<Product> plist = formatToProductListMulti(strSql, om_conn);
			
			long stopSqlTime = System.currentTimeMillis();
			long elapsedSqlTime = stopSqlTime - startSqlTime;
			
			//InsertQueryLog("getAllInforByPnFuzzy", "Time:" + elapsedSqlTime + strSql, fm_conn);
	        
			return plist;
		}
	
	
	private List<Product> getAllInforByPnLike(String pnkey) {
		 
		String strSql = getAllInfoByPn_head +  "'" + pnkey + "' "  
						+ getAllInfoByPn_foot;
		
		
		long startSqlTime = System.currentTimeMillis();
		
		List<Product> plist = formatToProductList(strSql, om_conn);
		
		long stopSqlTime = System.currentTimeMillis();
		long elapsedSqlTime = stopSqlTime - startSqlTime;
		
		//InsertQueryLog("getAllInforByPnFuzzy", "Time:" + elapsedSqlTime + strSql, fm_conn);
        
		return plist;
	}
	
	private List<Product> getAllInforByIdLike(String pnkey) {
		 
		String strSql = getAllInfoById_head +  pnkey
						+ getAllInfoByPn_foot;
		
		
		long startSqlTime = System.currentTimeMillis();
		
		List<Product> plist = formatToProductList(strSql, om_conn);
		
		long stopSqlTime = System.currentTimeMillis();
		long elapsedSqlTime = stopSqlTime - startSqlTime;
		
		//InsertQueryLog("getAllInforByPnFuzzy", "Time:" + elapsedSqlTime + strSql, fm_conn);
        
		return plist;
	}
	
	
	private boolean IsNullOrEmpty(String value)
	{
	  if (value != null)
	    return value.length() == 0;
	  else
	    return true;
	}
	
	// 20160416 deprecated
	private List<Product> dealWithWebPListRepeat(List<Product> productList) {
		
		/*
        List<Product> resultProductList = new ArrayList<>();

        Map<String, List<Product>> productMap = new HashMap<>();

        for (Product product : productList) {
            String mfs = product.getMfs();
            if (IsNullOrEmpty(mfs)) {
                continue;
            }
            mfs = mfs.toUpperCase();
            String pn = product.getPn();
            int supplierId = product.getSupplierid();
            String sPn = product.getSupplierpn();
            
            sPn = sPn == null ? "" : sPn;
      
            int productId = product.getId();

            String mapKey = pn + mfs + supplierId + sPn;

            List<Product> productStoreList = productMap.get(mapKey);
            if (productStoreList == null) {
                productStoreList = new ArrayList<>();
                productStoreList.add(product);
                productMap.put(mapKey, productStoreList);
                resultProductList.add(product);
            } else {
                boolean existFlat = false;
                for (Product sProduct : productStoreList) {
                    int sProductId = sProduct.getId();
                    //产品id不同，即为重复。
                    if (productId != sProductId) {
                        existFlat = true;
                        break;
                    }
                }
                //若不存在该产品，则加入
                if (!existFlat) {
                    productStoreList.add(product);
                    resultProductList.add(product);
                }
            }
        }

        return resultProductList;
        */
		return orderFromProductList(productList);
    }
	
	
	private List<Product> formatToProductList(String strSql, Connection conn) {

		List<Product> sList = new ArrayList<>();

		try {

			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new Product(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7)));
	
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
	
	// 20160112 多料號搜尋
	private List<Product> formatToProductListMulti(String strSql, Connection conn) {

		List<Product> sList = new ArrayList<>();

		try {

			Statement stmt = null;
			ResultSet rs = null;

			try {
				stmt = conn.createStatement();
				rs = stmt.executeQuery(strSql);
				while (rs.next())
					sList.add(new Product(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6), rs.getInt(7), rs.getString(8)));
	
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

}


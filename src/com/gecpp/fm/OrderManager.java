package com.gecpp.fm;

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
	
	private String OmUrl;
	private String OmUser;
	private String OmPwd;
	
	private String fmUrl;
	private String fmUser;
	private String fmPwd;
	
	private String []  pns = null;
	
	private static final String getAllInfoByPn_head = "SELECT a.inventory, a.offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs "
			+ "FROM pm_product b  LEFT JOIN pm_store_price a on a.product_id = b.id and (a.valid =1 OR a.valid IS NULL) "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.pn in(";
	
	private static final String getAllInfoByPn_foot = ") and b.supplier_id = c.id AND b.status is null order by b.pn, c.TYPE";
	
	private Connection om_conn = null;
	
	private Connection fm_conn = null;

	protected void loadParams() {
		
		//InsertQueryLog("fuzzysearch", "loadParams()");

		Context envurl, envusr, envpwd, envbase;

		String entryomurl = null, entryomusr = null, entryompwd = null;
		
		String entryfmurl = null, entryfmusr = null, entryfmpwd = null;

		try {
			
			envurl = (Context) new InitialContext().lookup("java:comp/env");
			entryomurl = (String) envurl.lookup("om.param.url");

			envusr = (Context) new InitialContext().lookup("java:comp/env");
			entryomusr = (String) envusr.lookup("om.param.user");

			envpwd = (Context) new InitialContext().lookup("java:comp/env");
			entryompwd = (String) envpwd.lookup("om.param.pwd");
			
			envurl = (Context) new InitialContext().lookup("java:comp/env");
			entryfmurl = (String) envurl.lookup("fm.param.url");

			envusr = (Context) new InitialContext().lookup("java:comp/env");
			entryfmusr = (String) envusr.lookup("fm.param.user");

			envpwd = (Context) new InitialContext().lookup("java:comp/env");
			entryfmpwd = (String) envpwd.lookup("fm.param.pwd");

		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		OmUrl = entryomurl;
		OmUser = entryomusr;
		OmPwd = entryompwd;
		
		fmUrl = entryfmurl;
		fmUser = entryfmusr;
		fmPwd = entryfmpwd;
		
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

            fm_conn = getfmPgSqlConnection();

            fm_conn.setAutoCommit(true);


        } catch (Exception ee)

        {
            System.out.print(ee.getMessage());

        }
    }

	public OrderResult getProductByGroupInStore(List<String> notRepeatPns) {
		
		if(notRepeatPns == null)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
			
		if(notRepeatPns.size() == 0)
		{
			OrderResult result = new OrderResult();
			LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();
			result.setPidList(returnMap);
			return result;
		}
		
		loadParams();
		
		connectPostgrel();
		
		List<Product> plist = new ArrayList<>();
		
		String pnsSql = createPnSql(notRepeatPns);
		
		plist = getAllInforByPnFuzzy(pnsSql);
		
		plist = dealWithWebPListRepeat(plist);
		
		OrderResult result = formatFromProductList(plist);

        result = orderProductList(result);
        
        attemptClose(om_conn);
		
		return result;
	}

	private OrderResult orderProductList(OrderResult result)
    {
        LinkedHashMap<String, Map<String, List<Integer>>> returnMap = result.getPidList();
        returnMap = sortHashMapByValuesD(returnMap);

        result.setPidList(returnMap);
        
        // 20150908依照歡平所需的欄位給予
        // setup 当前页所有产品的id列表(List<PID>)
        List<Integer> lstPID = GetPID(returnMap);
        
        result.setIds(lstPID);
        result.setPns(pns);

        return result;
    }
	
	private List<Integer> GetPID(LinkedHashMap<String, Map<String, List<Integer>>> passedMap)
	{
		List<Integer> returnPID = new ArrayList<Integer>();
		List<String> returnPns = new ArrayList<String>();
		

        for (Map.Entry<String, Map<String, List<Integer>>> entry : passedMap.entrySet()) {
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

    private LinkedHashMap sortHashMapByValuesD(LinkedHashMap<String, Map<String, List<Integer>>> passedMap) {

        // 找各料號下面的項目多寡，多的排前面
        HashMap<String, Integer> PnOrderMap = new HashMap<String, Integer>();
        OrdManaerComparator ovc =  new OrdManaerComparator(PnOrderMap);
        TreeMap<String,Integer> ord_map = new TreeMap<String,Integer>(ovc);

        for (Map.Entry<String, Map<String, List<Integer>>> entry : passedMap.entrySet()) {
            String key = entry.getKey();
            int count = 0;
            Map<String, List<Integer>> value = entry.getValue();

            //System.out.println(key + ":");

            for(Map.Entry<String, List<Integer>> subentry : value.entrySet())
            {
                String subkey = subentry.getKey();

                //System.out.println("    " + subkey + ":");

                List<Integer> subvalue = subentry.getValue();

                for(Integer listvalue:subvalue)
                {
                    //System.out.println("        " + listvalue);

                    count++;
                }
            }

            PnOrderMap.put(key, count);

        }

        ord_map.putAll(PnOrderMap);

        LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();


        for(Map.Entry<String,Integer> entry : ord_map.entrySet()) {

            Map<String, List<Integer>> value = passedMap.get(entry.getKey());
            
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

            returnMap.put(entry.getKey(), value);
        }

        return returnMap;
    }

	private OrderResult formatFromProductList(List<Product> plist) {
		OrderResult result = new OrderResult();
        LinkedHashMap<String, Map<String, List<Product>>> resultMap = new LinkedHashMap<String, Map<String, List<Product>>>();
        LinkedHashMap<String, Map<String, List<Integer>>> returnMap = new LinkedHashMap<String, Map<String, List<Integer>>>();

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

                Map<String, List<Product>> mfsGroupMap = resultMap.get(pnkey);
                Map<String, List<Integer>> mfsGroupMapInt = returnMap.get(pnkey);
                
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
		 
		String strSql = getAllInfoByPn_head +  pnkey  
						+ getAllInfoByPn_foot;
		
		InsertQueryLog("", strSql, fm_conn);
		
		List<Product> plist = formatToProductList(strSql, om_conn);
		return plist;
	}
	
	private boolean IsNullOrEmpty(String value)
	{
	  if (value != null)
	    return value.length() == 0;
	  else
	    return true;
	}
	
	private List<Product> dealWithWebPListRepeat(List<Product> productList) {
        List<Product> resultProductList = new ArrayList<>();

        Map<String, List<Product>> productMap = new HashMap<>();

        for (Product product : productList) {
            String mfs = product.getMfs();
            if (IsNullOrEmpty(mfs)) {
                continue;
            }
            mfs = mfs.toUpperCase();
            String pn = product.getPn();
      
            int productId = product.getId();

            String mapKey = pn + mfs;

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
					sList.add(new Product(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getString(4), rs.getString(5), rs.getString(6)));
	
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


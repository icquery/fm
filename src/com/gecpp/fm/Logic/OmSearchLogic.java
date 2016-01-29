package com.gecpp.fm.Logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gecpp.fm.Dao.IndexResult;
import com.gecpp.fm.Dao.Product;

public class OmSearchLogic {
	
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
	
	private static final String getAllInfoByPn_foot = ") and b.supplier_id = c.id AND b.status is null "; //order by b.pn, c.TYPE";
	
	public static String getFormatPn(List<String> notRepeatPns)
	{
		String pnSql = "";
		
		if(notRepeatPns == null)
			return pnSql;

        int pnsCount = notRepeatPns.size();

        if (notRepeatPns != null && pnsCount > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append("'");
            for (int i = 0; i < pnsCount; i++) {
                String s = notRepeatPns.get(i);
                stringBuilder.append(s).append("','");
            }
            pnSql = stringBuilder.substring(0, stringBuilder.length() - 2);
        } else {
            pnSql = "";
        }

        return pnSql;
	}
	
	public static String getFormatId(List<String> pns)
	{
		String pnSql = "";
		
		if(pns == null)
			return pnSql;

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
	
	
	private static String getListId(List<Integer> pns)
	{
		String pnSql = "";

		if(pns == null)
			return pnSql;
		
        int pnsCount = pns.size();

        if (pns != null && pnsCount > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < pnsCount; i++) {
                String s = pns.get(i).toString();
                stringBuilder.append(s).append(",");
            }
            pnSql = stringBuilder.substring(0, stringBuilder.length() - 1);
        } else {
            pnSql = "";
        }

        return pnSql;
	}
	
	
	public static String getAllInforByPnList(String strPn, int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation)
	{
		String strSql = getAllInfoByPn_head +  strPn  
				+ getAllInfoByPn_foot;
		
		if(inventory > 0)
			strSql += " and inventory > 0 ";
			
		if(lead > 0)
			strSql += " and lead = 'f' ";
		
		if(rohs > 0)
			strSql += " and rohs = 't' ";
		
		if(!getListId(mfs).trim().isEmpty())
			strSql += " and mfs_id in(" + getListId(mfs) + ") ";
		
		if(!getListId(abbreviation).trim().isEmpty())
			strSql += " and b.supplier_id in(" + getListId(abbreviation) + ") ";
		
		strSql += " order by b.pn, c.TYPE ";
	
		return strSql;
	}
	
	public static String getAllInforByIdList(String strPn, int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation)
	{
		String strSql = getAllInfoById_head +  strPn 
				+ getAllInfoByPn_foot;
		
		if(inventory > 0)
			strSql += " and inventory > 0 ";
			
		if(lead > 0)
			strSql += " and lead = 'f' ";
		
		if(rohs > 0)
			strSql += " and rohs = 't' ";
		
		if(!getListId(mfs).trim().isEmpty())
			strSql += " and mfs_id in(" + getListId(mfs) + ") ";
		
		if(!getListId(abbreviation).trim().isEmpty())
			strSql += " and b.supplier_id in(" + getListId(abbreviation) + ") ";
		
		strSql += " order by b.pn, c.TYPE ";
	
		return strSql;
	}
	
	public static int pageCountId(List<Product> plist)
	{
    	
    	int product_id = 0;
   
    	int gPage = -1;
    	for(Product product : plist)
    	{
    		if(product_id != product.getId())
    		{
    			gPage++;

    			product_id = product.getId();
    		}
    	
    	}
    	
    	
    	return gPage + 1;
	}
	
	public static int pageCount(List<Product> plist)
	{
    	
    	String product_id = "";
   
    	int gPage = -1;
    	for(Product product : plist)
    	{
    		if(!product_id.equalsIgnoreCase(product.getPn()))
    		{
    			gPage++;

    			product_id = product.getPn();
    		}
    	
    	}
    	
    	
    	return gPage + 1;
	}
	
	public static List<Product> pageDataId(List<Product> plist, int currentPage, 
			int pageSize)
	{
		List<Product> pageList = new ArrayList<Product> ();
    	Map<Integer, List<Product>> uniqPn = new HashMap<Integer, List<Product>>();
    	
    	int product_id = 0;
   
    	int gPage = -1;
    	for(Product product : plist)
    	{
    		if(product_id != product.getId())
    		{
    			gPage++;
    			
    			List<Product> id_product = new ArrayList<Product>();
    			id_product.add(product);
    			
    			uniqPn.put(gPage, id_product);
    			
    			product_id = product.getId();
    		}
    		else
    		{
    			List<Product> id_product = uniqPn.get(gPage);
    			
    			id_product.add(product);
    			
    			uniqPn.put(gPage, id_product);
    		}
    	}
    	
    	// set area by weight
    	for(int i=(currentPage - 1) * pageSize; i < currentPage * pageSize; i++)
        {
        	if(i<gPage+1)
        	{
        		List<Product> pList = uniqPn.get(i);
        		pageList.addAll(pList);
        	}
        }
    	
    	return pageList;
	}
	
	public static List<Product> pageData(List<Product> plist, int currentPage, 
			int pageSize)
	{
		List<Product> pageList = new ArrayList<Product> ();
    	Map<Integer, List<Product>> uniqPn = new HashMap<Integer, List<Product>>();
    	
    	String product_id = "";
   
    	int gPage = -1;
    	for(Product product : plist)
    	{
    		if(!product_id.equalsIgnoreCase(product.getPn()))
    		{
    			gPage++;
    			
    			List<Product> id_product = new ArrayList<Product>();
    			id_product.add(product);
    			
    			uniqPn.put(gPage, id_product);
    			
    			product_id = product.getPn();
    		}
    		else
    		{
    			List<Product> id_product = uniqPn.get(gPage);
    			
    			id_product.add(product);
    			
    			uniqPn.put(gPage, id_product);
    		}
    	}
    	
    	// set area by weight
    	for(int i=(currentPage - 1) * pageSize; i < currentPage * pageSize; i++)
        {
        	if(i<gPage+1)
        	{
        		List<Product> pList = uniqPn.get(i);
        		pageList.addAll(pList);
        	}
        }
    	
    	return pageList;
	}
}

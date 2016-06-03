package com.gecpp.fm.Logic;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.gecpp.fm.MultipleParam;
import com.gecpp.fm.Dao.IndexPrice;
import com.gecpp.fm.Dao.IndexResult;
import com.gecpp.fm.Dao.Product;
import com.gecpp.fm.model.OrderManagerModel;

public class OmSearchLogic {
	
	// 20160513價格庫存另外查
	/*
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
	*/
	
	private static final String getAllInfoByPn_head = "SELECT 0 as inventory, '' as offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs, b.supplier_id, b.lead, b.rohs, b.mfs_id "
			+ "FROM pm_product b "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.pn in(";
	
	private static final String getAllInfoById_head = "SELECT 0 as inventory, '' as offical_price, b.id, b.pn, b.supplier_pn, CASE WHEN trim(d.NAME) <> '' THEN d.NAME ELSE b.mfs END as mfs, b.supplier_id, b.lead, b.rohs, b.mfs_id "
			+ "FROM pm_product b "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.id in(";
	
	private static final String getAllInfoByPn_foot = ") and b.supplier_id = c.id AND b.status is null "; //order by b.pn, c.TYPE";
	
	// 20160526 use pm_store_price_select
	/*
	private static final String getAllPrice_head = "SELECT product_id, inventory, offical_price FROM pm_store_price where id in (SELECT max(id) FROM pm_store_price where product_id IN (";
	
	private static final String getAllPrice_foot = ") GROUP BY product_id)";
	*/
	
	private static final String getAllPrice_head = "SELECT product_id, inventory, offical_price FROM pm_store_price_select where product_id in (";
	
	private static final String getAllPrice_foot = ") ";
	
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
	
	public static String getFormatIdFromProdcut(List<Product> pns)
	{
		String pnSql = "";
		
		if(pns == null)
			return pnSql;

        int pnsCount = pns.size();

        if (pns != null && pnsCount > 0) {
            StringBuilder stringBuilder = new StringBuilder();

            for (int i = 0; i < pnsCount; i++) {
            	String s = "";
            	try{
                	s = Integer.toString(pns.get(i).getId());
            	}
            	catch(Exception e)
            	{
            		continue;
            	}
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
		
		// 20160518 為了深度查詢的效能，篩選全不做在sql
		//if(inventory > 0)
		//	strSql += " and inventory > 0 ";
			
		//if(lead > 0)
		//	strSql += " and lead = 'f' ";
		
		//if(rohs > 0)
		//	strSql += " and rohs = 't' ";
		
		//if(!getListId(mfs).trim().isEmpty())
		//	strSql += " and mfs_id in(" + getListId(mfs) + ") ";
		
		//if(!getListId(abbreviation).trim().isEmpty())
		//	strSql += " and b.supplier_id in(" + getListId(abbreviation) + ") ";
		
		strSql += " order by b.pn, c.TYPE  ";
	
		return strSql;
	}
	
	public static List<Product> getPriceByProductList(List<Product> unPriceProductList)
	{		
		if(unPriceProductList == null)
			return unPriceProductList;
		
		if(unPriceProductList.size() == 0)
			return unPriceProductList;
		
		String strIds = getFormatIdFromProdcut(unPriceProductList);
		String strSql = getAllPrice_head +  strIds  
				+ getAllPrice_foot;
		
		List<IndexPrice> priceList = OrderManagerModel.getPriceByProdcut(strSql);
		
		
		for(IndexPrice plist : priceList)
		{
			for(Product pro : unPriceProductList)
			{
				if(pro.getId() == plist.getId())
				{
					
					pro.setInventory(plist.getInventory());
					pro.setPrice(plist.getPrice());

					break;
				}
			}
		}
		
		return unPriceProductList;
		
	}
	
	public static List<Product> getPriceByProductList(List<Product> unPriceProductList, int inventory)
	{
		if(inventory == 0)
			return unPriceProductList;
		
		if(unPriceProductList == null)
			return unPriceProductList;
		
		if(unPriceProductList.size() == 0)
			return unPriceProductList;
		
		String strIds = getFormatIdFromProdcut(unPriceProductList);
		String strSql = getAllPrice_head +  strIds  
				+ getAllPrice_foot;
		
		List<IndexPrice> priceList = OrderManagerModel.getPriceByProdcut(strSql);
		
		List<Product> newProductList = new ArrayList<Product>();
		
		for(IndexPrice plist : priceList)
		{
			for(Product pro : unPriceProductList)
			{
				if(pro.getId() == plist.getId())
				{
					if(inventory != 0)
					{
						if(plist.getInventory() >= inventory)
						{
							pro.setInventory(plist.getInventory());
							pro.setPrice(plist.getPrice());
							newProductList.add(pro);
						}
					}
					else
					{
						pro.setInventory(plist.getInventory());
						pro.setPrice(plist.getPrice());

					}
					
					break;
				}
			}
		}
		
		return newProductList;
		
	}
	
	public static List<Product> getPriceByProductList(List<Product> unPriceProductList, int lead, int rohs, List<Integer> mfs_id, List<Integer> supplier_id)
	{
		List<Product> newProductList = new ArrayList<Product>();
		
		for (Product pro : unPriceProductList) {
			
			String sLead = "";
            String sRohs = "";
            int mfsId = 0;
            int supplierId = 0;
            
            boolean bAddToNewList = true;
            
            try
            {
            	sLead = pro.getLead().trim();
            }
            catch(Exception e)
            {}
            
            try
            {
            	sRohs = pro.getRohs().trim();
            }
            catch(Exception e)
            {}
            
            try
            {
            	mfsId = pro.getMfs_id();
            }
            catch(Exception e)
            {}
            
            try
            {
            	supplierId = pro.getSupplierid();
            }
            catch(Exception e)
            {}
            
            if(lead > 0)
            {
            	if(!sLead.equalsIgnoreCase("f"))
            		bAddToNewList = false;
            }
            
            if(rohs > 0)
            {
            	if(!sRohs.equalsIgnoreCase("t"))
            		bAddToNewList = false;
            }
            
            if(mfs_id != null)
            {
            	boolean bFound = false;
            	
            	for(Integer id:mfs_id)
            	{
            		if(mfsId == id)
            		{
            			bFound = true;
            			break;
            		}
            	}
            	
            	if(mfs_id.size() == 0)
            		bFound = true;
            	
            	if(bFound == false)
            		bAddToNewList = false;
            }
            
            if(supplier_id != null)
            {
            	boolean bFound = false;
            	
            	for(Integer id:supplier_id)
            	{
            		if(supplierId == id)
            		{
            			bFound = true;
            			break;
            		}
            	}
            	
            	if(supplier_id.size() == 0)
            		bFound = true;
            	
            	if(bFound == false)
            		bAddToNewList = false;
            }
            
            if(bAddToNewList == true)
            	newProductList.add(pro);
        }
		
		
		return newProductList;
		
	}
	
	public static String getAllInforByIdList(String strPn, int inventory, 
			int lead, 
			int rohs, 
			List<Integer> mfs, 
			List<Integer> abbreviation)
	{
		String strSql = getAllInfoById_head +  strPn 
				+ getAllInfoByPn_foot;
		
		// 20160518 為了深度查詢的效能，篩選全不做在sql
		//if(inventory > 0)
		//	strSql += " and inventory > 0 ";
			
		//if(lead > 0)
		//	strSql += " and lead = 'f' ";
		
		//if(rohs > 0)
		//	strSql += " and rohs = 't' ";
		
		//if(!getListId(mfs).trim().isEmpty())
		//	strSql += " and mfs_id in(" + getListId(mfs) + ") ";
		
		//if(!getListId(abbreviation).trim().isEmpty())
		//	strSql += " and b.supplier_id in(" + getListId(abbreviation) + ") ";
		
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

package com.gecpp.fm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class OrderManager {
	
	private static final String getAllInfoByPn_head = "SELECT a.inventory, a.offical_price, b.id, b.pn, b.supplier_pn, b.mfs "
			+ "FROM pm_product b  LEFT JOIN pm_store_price a on a.product_id = b.id and (a.valid =1 OR a.valid IS NULL) "
			+ "LEFT JOIN pm_product_config e on(e.supplier_id=b.supplier_id) "
			+ "LEFT JOIN pm_mfs_standard d on (b.mfs_id = d.id),  pm_supplier c  "
			+ " where b.pn in(";
	
	private static final String getAllInfoByPn_foot = ") and b.supplier_id = c.id AND b.status is null order by b.pn desc limit 400";

	
	public OrderResult getProductByGroupInStore(List<String> notRepeatPns, Connection conn) {
		
		List<Product> plist = new ArrayList<>();
		
		String pnsSql = createPnSql(notRepeatPns);
		
		plist = getAllInforByPnFuzzy(pnsSql, conn);
		
		plist = dealWithWebPListRepeat(plist);
		
		OrderResult result = formatFromProductList(plist);
		
		return null;
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
	
	private List<Product> getAllInforByPnFuzzy(String pnkey, Connection conn) {
		 
		String strSql = getAllInfoByPn_head +  pnkey  
						+ getAllInfoByPn_foot;
		
		List<Product> plist = formatToProductList(strSql, conn);
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
					sList.add(new Product(rs.getString(1), rs.getInt(2), rs.getString(3), rs.getInt(4)));
	
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

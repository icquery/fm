package com.gecpp.fm;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;

public class OrderResultDetail implements Serializable{
	public int getTotalCount() {
		return TotalCount;
	}
	public void setTotalCount(int totalCount) {
		TotalCount = totalCount;
	}
	public String getHighLight() {
		return HighLight;
	}
	public void setHighLight(String highLight) {
		HighLight = highLight;
	}
	public LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> getPidList() {
		return PidList;
	}
	public void setPidList(LinkedHashMap<String, LinkedHashMap<String, List<Integer>>> pidList) {
		PidList = pidList;
	}
	public List<Integer> getIds() {
		return ids;
	}
	public void setIds(List<Integer> ids) {
		this.ids = ids;
	}
	public String[] getPns() {
		return pns;
	}
	public void setPns(String[] pns) {
		this.pns = pns;
	}

	public String [] getPkg() {
		return pkg;
	}

	public void setPkg(String [] pkg) {
		this.pkg = pkg;
	}

	public String [] getSupplier() {
		return supplier;
	}

	public void setSupplier(String [] supplier) {
		this.supplier = supplier;
	}


	private int TotalCount;			// 全部資料量
	
	private String HighLight;		// 關鍵字，以逗號區分(TI, OP, LM358)
	
	private LinkedHashMap<String, LinkedHashMap<String,List<Integer>>>  PidList;	// 結構  (料號, Map<供應商, List<PID>>)
	
	private List<Integer> ids;		// 当前页所有产品的id列表(List<PID>)
	
	private String [] pns;			// 当前页所有产品的pn列表(String[])
	
	private String [] pkg;			// 当前页所有产品的pkg列表(String[])
	
	private String [] supplier;			// 当前页所有产品的pkg列表(String[])

}

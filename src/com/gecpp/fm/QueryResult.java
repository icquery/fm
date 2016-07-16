package com.gecpp.fm;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class QueryResult implements Serializable {
	
	/*

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupMfs1() {
		return PidListGroupMfs1;
	}

	public void setPidListGroupMfs1(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupMfs1) {
		PidListGroupMfs1 = pidListGroupMfs1;
	}

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupMfs2() {
		return PidListGroupMfs2;
	}

	public void setPidListGroupMfs2(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupMfs2) {
		PidListGroupMfs2 = pidListGroupMfs2;
	}

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupMfs3() {
		return PidListGroupMfs3;
	}

	public void setPidListGroupMfs3(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupMfs3) {
		PidListGroupMfs3 = pidListGroupMfs3;
	}

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupSupplier1() {
		return PidListGroupSupplier1;
	}

	public void setPidListGroupSupplier1(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupSupplier1) {
		PidListGroupSupplier1 = pidListGroupSupplier1;
	}

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupSupplier2() {
		return PidListGroupSupplier2;
	}

	public void setPidListGroupSupplier2(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupSupplier2) {
		PidListGroupSupplier2 = pidListGroupSupplier2;
	}

	public LinkedHashMap<String, Map<String, List<Integer>>> getPidListGroupSupplier3() {
		return PidListGroupSupplier3;
	}

	public void setPidListGroupSupplier3(
			LinkedHashMap<String, Map<String, List<Integer>>> pidListGroupSupplier3) {
		PidListGroupSupplier3 = pidListGroupSupplier3;
	}

	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupMfs1;	// 結構  (料號, Map<mfs, List<PID>>)
	
	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupMfs2;	// 結構  (料號, Map<mfs, List<PID>>)
	
	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupMfs3;	// 結構  (料號, Map<mfs, List<PID>>)
	
	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupSupplier1;	// 結構  (料號, Map<Supplier, List<PID>>)
	
	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupSupplier2;	// 結構  (料號, Map<Supplier, List<PID>>)
	
	private LinkedHashMap<String, Map<String,List<Integer>>>  PidListGroupSupplier3;	// 結構  (料號, Map<Supplier, List<PID>>)
	
	*/
	
	
	
	// 20160415 change structure for leo
	private LinkedHashMap<String, Map<String,Map<Integer, Integer>>>  PidListGroupMfs1;	// 結構  (料號, Map<pn,Map<mfs,Map<supplier_id,pid>>>)
	
	private LinkedHashMap<String, Map<String,Map<Integer, Integer>>>  PidListGroupMfs2;	// 結構  (料號, Map<pn,Map<mfs,Map<supplier_id,pid>>>)
	
	private LinkedHashMap<String, Map<String,Map<Integer, Integer>>>  PidListGroupMfs3;	// 結構  (料號, Map<pn,Map<mfs,Map<supplier_id,pid>>>)

	/**
	 * @return the pidListGroupMfs1
	 */
	public LinkedHashMap<String, Map<String,Map<Integer, Integer>>> getPidListGroupMfs1() {
		return PidListGroupMfs1;
	}

	/**
	 * @param pidListGroupMfs1 the pidListGroupMfs1 to set
	 */
	public void setPidListGroupMfs1(LinkedHashMap<String, Map<String,Map<Integer, Integer>>> pidListGroupMfs1) {
		PidListGroupMfs1 = pidListGroupMfs1;
	}

	/**
	 * @return the pidListGroupMfs2
	 */
	public LinkedHashMap<String, Map<String,Map<Integer, Integer>>> getPidListGroupMfs2() {
		return PidListGroupMfs2;
	}

	/**
	 * @param pidListGroupMfs2 the pidListGroupMfs2 to set
	 */
	public void setPidListGroupMfs2(LinkedHashMap<String, Map<String,Map<Integer, Integer>>> pidListGroupMfs2) {
		PidListGroupMfs2 = pidListGroupMfs2;
	}

	/**
	 * @return the pidListGroupMfs3
	 */
	public LinkedHashMap<String, Map<String,Map<Integer, Integer>>> getPidListGroupMfs3() {
		return PidListGroupMfs3;
	}

	/**
	 * @param pidListGroupMfs3 the pidListGroupMfs3 to set
	 */
	public void setPidListGroupMfs3(LinkedHashMap<String, Map<String,Map<Integer, Integer>>> pidListGroupMfs3) {
		PidListGroupMfs3 = pidListGroupMfs3;
	}
	
	
}

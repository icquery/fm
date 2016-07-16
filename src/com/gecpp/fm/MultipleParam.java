package com.gecpp.fm;

import java.io.Serializable;
import java.util.List;

public class MultipleParam  implements Serializable{
	
	
	private List<String> descriptions;
	private List<String> pkgs;
	/**
	 * @return the descriptions
	 */
	public List<String> getDescriptions() {
		return descriptions;
	}
	/**
	 * @param descriptions the descriptions to set
	 */
	public void setDescriptions(List<String> descriptions) {
		this.descriptions = descriptions;
	}
	/**
	 * @return the pkgs
	 */
	public List<String> getPkgs() {
		return pkgs;
	}
	/**
	 * @param pkgs the pkgs to set
	 */
	public void setPkgs(List<String> pkgs) {
		this.pkgs = pkgs;
	}

}

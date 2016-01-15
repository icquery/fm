package com.gecpp.fm.Dao;

import java.util.List;

public class Keyword {
	
	public static enum KeywordKind {
	    IsPn, NotPn 
	} 
	
	public static enum NLP {
	    IsNLP, NotNLP 
	} 

	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public List<String> getKeyword() {
		return keyword;
	}
	public void setKeyword(List<String> keyword) {
		this.keyword = keyword;
	}
	public List<KeywordKind> getKind() {
		return kind;
	}
	public void setKind(List<KeywordKind> kind) {
		this.kind = kind;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getInputdata() {
		return inputdata;
	}
	public void setInputdata(String inputdata) {
		this.inputdata = inputdata;
	}
	
	

	public List<NLP> getNlp() {
		return nlp;
	}
	public void setNlp(List<NLP> nlp) {
		this.nlp = nlp;
	}



	private int count;
	private List<String> keyword;
	private List<KeywordKind> kind;
	private String uuid;
	private String inputdata;
	private List<NLP> nlp;
}

package com.gecpp.fm.Dao;

public class IndexShort {
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public String getAlterword() {
		return alterword;
	}
	public void setAlterword(String alterword) {
		this.alterword = alterword;
	}
	public IndexShort(String word, String alterword) {

		this.word = word;
		this.alterword = alterword;
	}
	private String word;
    private String alterword;
}

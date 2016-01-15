package com.gecpp.fm.Dao;

public class IndexAdj {
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public float getAdjust() {
		return adjust;
	}
	public void setAdjust(float adjust) {
		this.adjust = adjust;
	}
	public String getAlterword() {
		return alterword;
	}
	public void setAlterword(String alterword) {
		this.alterword = alterword;
	}
	public int getKind() {
		return kind;
	}
	public void setKind(int kind) {
		this.kind = kind;
	}
	
	private String word;
    private float adjust;
    private String alterword;
    private int kind;
    
	public IndexAdj(String word, String alterword, int kind, float adjust) {
		this.word = word;
		this.adjust = adjust;
		this.alterword = alterword;
		this.kind = kind;
	}

}

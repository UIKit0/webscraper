package com.websqrd.catbot.setting;

public class ShowField {
	private String value;
	private String caption;
	private boolean isID;
	
	public ShowField(String value, String caption){
		this(value, caption, false);
	}
	
	public ShowField(String value, String caption, boolean isID){
		this.value = value;
		this.caption = caption;
		this.isID = isID;
	}
	
	public String getValue() {
		return value;
	}
	public String getCaption() {
		return caption;
	}
	public boolean isID() {
		return isID;
	}
}

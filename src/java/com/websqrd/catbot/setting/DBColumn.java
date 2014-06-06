package com.websqrd.catbot.setting;

import org.jdom.Element;

public class DBColumn{
	public String name;
	public boolean notNull;
	
	public DBColumn(Element e){
		name = e.getValue();
		String notNull = e.getAttributeValue("notNull");
		if(notNull != null && notNull.equals("true")){
			this.notNull = true;
		}
	}
}
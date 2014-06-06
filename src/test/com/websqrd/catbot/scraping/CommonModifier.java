package com.websqrd.catbot.scraping;
import java.util.Map;

import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.scraping.modifier.AbstractFieldModifier;


public class CommonModifier extends AbstractFieldModifier {

	@Override
	public String modify(String s, Map<String, String> map) throws CatbotException {
		if(s.equalsIgnoreCase("title")){
			String str = map.get("title");
			return "[modified]"+str;
		}else if(s.equalsIgnoreCase("link")){
			String str = map.get("link");
			return "[modified]"+str;
		}else if(s.equalsIgnoreCase("contents")){
			String str = map.get("contents");
			return "[modified]"+str;
		}
		
		return s;
	}

}

package com.websqrd.catbot.scraping.modifier;

import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.CatbotSettings;

public class ScrapModifierLoader {
	protected final static Logger logger = LoggerFactory.getLogger(ScrapModifierLoader.class);
	
	public static void load(Map<String, String> modifierMap){
		Iterator<String> iter = modifierMap.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String modifierClass = modifierMap.get(key);
			logger.trace("key={} class= {}", key, modifierClass);			
			
			Object object = CatbotSettings.classLoader.loadObject(modifierClass);
			if(object != null){
				ScrapModifier.put(key, (ScrapModifier)object);
			}else{
				logger.error("모디파이어 클래스 로딩 실패 >> {}", modifierClass);
			}
			
		}
	}
}

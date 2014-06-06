package com.websqrd.catbot.scraping.modifier;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.setting.CatbotSettings;

public abstract class ScrapModifier {
	protected final static Logger logger = LoggerFactory.getLogger(ScrapModifier.class);
	//모디파이어 맵
	private static Map<String, ScrapModifier> modifierMap = new HashMap<String, ScrapModifier>();
	
	//기본 모디파이어들.
	private final static String prefixModifierClass = "com.websqrd.catbot.scraping.modifier.PrefixModifier";
	private final static String suffixModifierClass = "com.websqrd.catbot.scraping.modifier.SuffixModifier";
	
	static{
		Object object = CatbotSettings.classLoader.loadObject(prefixModifierClass);
		ScrapModifier.put(PrefixModifier.key, (ScrapModifier)object);
		
		object = CatbotSettings.classLoader.loadObject(suffixModifierClass);
		ScrapModifier.put(SuffixModifier.key, (ScrapModifier)object);
	}
	
	public abstract String modify(String key, String value);
	
	//모디파이어를 추가한다.
	public static void put(String id, ScrapModifier modifier){
		modifierMap.put(id, modifier);
	}
	
	public static ScrapModifier get(String id) {
		return modifierMap.get(id);
	}
	
	//특수값 셋팅이 필요할때 사용,평시는 공백으로 구현.
	public abstract void init(Object... obj1);
	
}

package com.websqrd.catbot.scraping.modifier;

public class WeightModifier extends ScrapModifier {

	@Override
	public String modify(String key, String value) {
		
		return value.replaceAll("aa：", "") + "KG";
	}

	@Override
	public void init(Object... obj) { 	}

}

package com.websqrd.catbot.scraping.modifier;

import java.util.Map;

import com.websqrd.catbot.exception.CatbotException;

public abstract class AbstractFieldModifier {
	public AbstractFieldModifier(){ }

    public abstract String modify(String s, Map<String, String> map) throws CatbotException;

}

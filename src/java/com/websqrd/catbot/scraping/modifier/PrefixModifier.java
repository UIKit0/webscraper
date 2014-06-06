package com.websqrd.catbot.scraping.modifier;

/**
 * 접두어를 붙여주는 모디파이어 
 * @author swsong
 *
 */
public class PrefixModifier extends ScrapModifier {
	
	public final static String key = "prefixModifier";
	private String prefix;
	
	@Override
	public String modify(String key, String value) {
		return prefix + value;
	}

	@Override
	public void init(Object... obj) {
		this.prefix = (String)obj[0];
	}

}

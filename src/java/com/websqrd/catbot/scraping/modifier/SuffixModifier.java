package com.websqrd.catbot.scraping.modifier;

/**
 * 접미어를 붙여주는 모디파이어 
 * @author swsong
 *
 */
public class SuffixModifier extends ScrapModifier {
	
	public final static String key = "suffixModifier";
	private String suffix;
	
	@Override
	public String modify(String key, String value) {
		return value + suffix;
	}

	@Override
	public void init(Object... obj) {
		this.suffix = (String)obj[0];
	}

}

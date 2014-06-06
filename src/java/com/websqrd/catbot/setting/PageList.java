package com.websqrd.catbot.setting;

import java.util.ArrayList;

public class PageList {
	private ArrayList<Page> list = new ArrayList<Page>();

	public ArrayList<Page> getList() {
		return list;
	}

	public void addPage(Page page) {
		this.list.add(page);
	}
}

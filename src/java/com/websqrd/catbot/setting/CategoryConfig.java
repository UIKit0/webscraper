/*
 * Copyright (C) 2011 WebSquared Inc. http://websqrd.com
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package com.websqrd.catbot.setting;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CategoryConfig {
	
	private final static Logger logger = LoggerFactory.getLogger(CategoryConfig.class);
	
	public static final String SCRAPGETALL="GETALL";
	public static final String SCRAPGETNEW="GETNEW";
	public static final String BREAK_WHEN_DUPLICATION="BREAK_WHEN_DUPLICATION";
	public static final String SKIP_WHEN_DUPLICATION="SKIP_WHEN_DUPLICATION";
	public static final String OVERWRITE_WHEN_DUPLICATION="OVERWRITE_WHEN_DUPLICATION";
	
	public static final String BREAK_WHEN_PK_LESSER_THAN="BREAK_WHEN_PK_LESSER_THAN";
	public static final String CONTINUE_WHEN_PK_LESSER_THAN="CONTINUE_WHEN_PK_LESSER_THAN";
	
	private String categoryName;
	private String categoryCode;
	private String categoryDescription;
	private String categoryScrapAction;
	private String duplicationAction;
	private String pkCompareAction;
	private List<Page> pageList = new ArrayList<Page>();
	private Process process;
	
	public CategoryConfig(String site, String category, Element root) {
		if(root == null){
			return;
		}
		
		if(!root.getName().equals("category")){
			logger.error("셋팅파일의 Root엘리먼트는 category이어야 합니다.");
			return;
		}
		
		this.categoryName = root.getAttributeValue("name");		
		this.categoryCode = root.getAttributeValue("code");
		this.categoryDescription = root.getAttributeValue("description");
		this.categoryScrapAction = root.getAttributeValue("ScrapAction");
		this.duplicationAction = root.getAttributeValue("duplicationAction");
		this.pkCompareAction = root.getAttributeValue("pkCompareAction");
		
		if (  SCRAPGETNEW.equalsIgnoreCase(this.categoryScrapAction) )
			this.categoryScrapAction = SCRAPGETNEW;;
			
		if (this.categoryScrapAction == null || ("").equals(this.categoryScrapAction)
				|| SCRAPGETALL.equalsIgnoreCase(this.categoryScrapAction)) {
			this.categoryScrapAction = SCRAPGETALL;
		}
		
		if ( this.duplicationAction == null || this.duplicationAction == "") {
			this.duplicationAction = BREAK_WHEN_DUPLICATION;
		} else if ( !this.duplicationAction.equals(SKIP_WHEN_DUPLICATION) ) {
			this.duplicationAction = BREAK_WHEN_DUPLICATION;
		}
		
		if ( this.pkCompareAction == null || this.pkCompareAction == "") {
			this.pkCompareAction = BREAK_WHEN_PK_LESSER_THAN;
		} else if ( !this.pkCompareAction.equals(CONTINUE_WHEN_PK_LESSER_THAN) ) {
			this.pkCompareAction = BREAK_WHEN_PK_LESSER_THAN;
		}
				
		Element processEl = root.getChild("process");
		process = new Process(processEl);
		
		Element pageListEl = root.getChild("pagelist");
		List<Element> pages = (List<Element>) pageListEl.getChildren();
		
		for(Element pnode : pages) {
			pageList.add(new Page(pnode));
		}
	}
	
	public String getCategoryName() {
		return categoryName;
	}
	
	public String getCategoryCode() {
		return categoryCode;
	}
	
	public String getDescription() {
		return categoryDescription;
	}
	
	public List<Page> getPageList(){
		return pageList;
	}
	public Process getProcess(){
		return process;
	}
	public String getScrapAction(){
		return categoryScrapAction;
	}
	public String getDuplicationAction(){
		return duplicationAction;
	}
	public String getPKCompareAction() {
		return pkCompareAction;
	}
	public void setScrapAction(String scrapAction){
		this.categoryScrapAction = scrapAction; 
	}
}
package com.websqrd.catbot.setting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jdom.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Process {
	private final static Logger logger = LoggerFactory.getLogger(Process.class);
	
	private List<Block> blockList = new ArrayList<Block>();
	private String rootPath;
	private String pkFieldId;
	private boolean isPkNumeric;
	private String referenceTemplate;
	private boolean hasDocument;
	
	public Process(Element root) {
		
		if(!root.getName().equals("process")){
			logger.error("셋팅파일의 Root엘리먼트는 process이어야 합니다.");
			return;
		}
		
		rootPath = root.getAttributeValue("rootPath");
		if (rootPath == null) {
			referenceTemplate = root.getAttributeValue("template");
			if(referenceTemplate != null){
				//템플릿을 지정했다면 rootPath가 없어도 OK!
				return;
			}
			logger.error("셋팅파일의 RootPath는 반드시 셋팅되어야 합니다.");
			return;
		}
		
		List<Element> blockListEl = (List<Element>)root.getChildren();
		
		
		for(Element blockEl : blockListEl) {
			String name = blockEl.getName();
			if(name.equals("block") || name.equals("field") || name.equals("document")){				
				blockList.add(new Block(blockEl));
			}
		}//for
		
		//
		//PK를 확인한다.
		//
		hasDocument = false;
		for(Block b : blockList){
			if(checkPk(b)){
//				logger.debug("PK id >> {}", pkFieldId);
//				logger.debug("PK numeric? >> {}", isPkNumeric);
			}
			if ( checkDocument(b))			 
				hasDocument = true;
		}
		
	}
	
	private boolean checkPk(Block block){
		if(block.isPk){
			pkFieldId = block.id;
			if(block.isNumeric){
				isPkNumeric = true; 
			}
			//pk는 하나라는 가정하에 종료한다.
			return true;
		}
		List<Block> children = block.getChildren();
		for(Block b : children){
			if(checkPk(b)){
				return true;
			}
		}
		return false;
	}
	
	private boolean checkDocument(Block block){
		if(block.isDocument){			
			return true;
		}
		List<Block> children = block.getChildren();
		for(Block b : children){
			if(checkDocument(b)){
				return true;
			}
		}
		return false;
	}
	
	public boolean getHasDocument()
	{
		return hasDocument;
	}
	
	public String getPkFieldId() {
		return pkFieldId;
	}
	public boolean isPkNumeric() {
		return isPkNumeric;
	}
	public List<Block> getBlockList() {
		return blockList;
	}
	
	//여러 depth로 되어있는 block구조를 일차원으로 풀어서 리턴한다.
	public List<Block> getBlockArrayList(){
		List<Block> list = new ArrayList<Block>();
		fill(blockList, list);
		return list;
	}
	
	private void fill(List<Block> fromList, List<Block> toList){
		for (int i = 0; i < fromList.size(); i++) {
			Block block = fromList.get(i);
			toList.add(block);
			List<Block> children = block.getChildren();
			
			if(children != null){
				fill(children, toList);
			}
		}
	}
	
	public String getRootPath() {
		return rootPath;
	}

	/*
	 * 템플릿을 사용하는 process이라면 site.xml에 설정된 템플릿내용을 가져온다.
	 * */
	public void referenceTemplate(Map<String, Process> processTemplateList) {
		if(referenceTemplate != null){
			Process processTemplate = processTemplateList.get(referenceTemplate);
			if(processTemplate != null){
				this.blockList = processTemplate.blockList;
				this.isPkNumeric = processTemplate.isPkNumeric;
				this.pkFieldId = processTemplate.pkFieldId;
				this.rootPath = processTemplate.rootPath;
			}else{
				logger.error("Process 템플릿이 site.xml에 정의되어 있지 않습니다. template = {}",referenceTemplate);
			}
		}
		
	}

}

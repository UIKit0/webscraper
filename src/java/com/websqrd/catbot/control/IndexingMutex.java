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

package com.websqrd.catbot.control;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.websqrd.catbot.job.WebScrapingJob;
import com.websqrd.catbot.job.Job;


public class IndexingMutex {

	private Map<Long, String> jobIdMap;
	private Set<String> monitors;
	private Map<String, String> jobTypeMap;
	
	public IndexingMutex(){
		jobIdMap = new HashMap<Long, String>();
		monitors = new HashSet<String>();
		jobTypeMap = new HashMap<String, String>();
	}
	
	public Collection<String> getIndexingList(){
		return jobTypeMap.values();
	}
	public synchronized void release(long jobId) {
		String scrapingKey = jobIdMap.remove(jobId);
		if(scrapingKey != null){
			monitors.remove(scrapingKey);
			jobTypeMap.remove(scrapingKey);
		}
	}

	public synchronized void access(long myJobId, Job job) {
//		if(job instanceof FullScrapingJob || job instanceof IncScrapingJob){
		if(job instanceof WebScrapingJob){
			String collection = job.getStringArgs(0);
			if(monitors.contains(collection)){
				return;
			}
		}else{
			//not an indexing job
			return;
		}
		
		String scrapingKey = getScrapingKey((WebScrapingJob)job);
		String type = null;
//		if(job instanceof FullScrapingJob){
//			type = "full";
//		}else if(job instanceof IncScrapingJob){
//			type = "add";
//		}
		type = "add";
		monitors.add(scrapingKey);
		jobIdMap.put(myJobId, scrapingKey);
		jobTypeMap.put(scrapingKey, scrapingKey+"."+type);
	}

	public synchronized boolean isLocked(Job job) {
//		if(job instanceof FullScrapingJob || job instanceof IncScrapingJob){
		if(job instanceof WebScrapingJob){
			String scrapingKey = getScrapingKey((WebScrapingJob)job);
			if(monitors.contains(scrapingKey)){
				return true;
			}
		}		
		return false;
	}
	
	public String getScrapingKey(WebScrapingJob job){
		String site = job.getStringArgs(0);
		String category = job.getStringArgs(1);
		return site+":"+category;
	}

}

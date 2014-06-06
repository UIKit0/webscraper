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

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.db.DBHandler;
import com.websqrd.catbot.db.ScrapingSchedule;
import com.websqrd.catbot.job.Job;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.SettingException;

public class JobScheduler {
	private static Logger logger = LoggerFactory.getLogger(JobScheduler.class);
	private Timer timer;
	private Map<String,TimerTask> taskMap;
	private List<String> siteList;
	
	public JobScheduler(List<String> siteList) {
//		props = IRSettings.getSchedule(true);
		taskMap = new HashMap<String, TimerTask>();
		this.siteList = siteList;
	}
	public void start(){
		try {
			//데몬 쓰레드로 시작해야 메인쓰레드가 종료할때 같이 종료됨.
			
			load();
		} catch (SettingException e) {
			logger.error("JobScheduler cannot be started!. "+e.getStackTrace(),e);
		}
	}
	private void load() throws SettingException{
		timer = new Timer(true);
		//Indexing Schedule
		List<ScrapingSchedule> schedules = DBHandler.getInstance().getScrapingSchedule().selectAll();
		for (int i = 0; i < schedules.size(); i++) {
			ScrapingSchedule schedule = schedules.get(i);
			if(siteList.contains(schedule.site)){
				if (schedule.site.startsWith("test")) {
					setIndexSchedule(schedule.site ,schedule.category, schedule.startTime, 2, schedule.isActive);
				}else{
					setIndexSchedule(schedule.site ,schedule.category, schedule.startTime, schedule.period, schedule.isActive);
				}
			}
		}
	}
	
	private String getKey(String site, String category){
//		if(type.equalsIgnoreCase("F")){
//			return site +"."+ category + ".FULL";
//		}else if(type.equalsIgnoreCase("I")){
//			return site +"."+ category + ".INC";
//		}
//		return null;
		return site +"."+ category;
	}
	
	public boolean setIndexSchedule(String site, String category, Timestamp startTime, int period, boolean isActive) {
//		if(type.equalsIgnoreCase("F")){
//			String key = getKey(site, category, type);
//			String className = "com.websqrd.catbot.job.FullScrapingJob"; 
//			String args = site+" "+category;
//			schedule(key, className, args, startTime, period, isActive);
//		}else if(type.equalsIgnoreCase("I")){
			String key = getKey(site, category);
			String className = "com.websqrd.catbot.job.WebScrapingJob"; 
			String args = site+" "+category +" GETNEW";
			schedule(key, className, args, startTime, period, isActive);
//		}else{
//			return false;
//		}
		return true;
	}
	public boolean setSchedule(String key, String jobClassName, String args, Timestamp startTime, int period, boolean isActive) {
		schedule(key, jobClassName, args, startTime, period, isActive);
		return true;
	}
	
	private void schedule(String key, String jobClassName, String args, Timestamp startTime, int period, boolean isActive) {
		TimerTask task = new JobTask(jobClassName, args);
		TimerTask oldTask = taskMap.put(key, task);
		
		if(oldTask != null){
			oldTask.cancel();
		}
		
		if(isActive){
			if(startTime.getTime() >= System.currentTimeMillis()){
				timer.scheduleAtFixedRate(task, new Date(startTime.getTime()), period * 1000L);
			}else{
				//avoid catch up of scheduleAtFixedRate
				long newFirstTime = startTime.getTime();
				while(newFirstTime < System.currentTimeMillis())
					newFirstTime += (period * 1000L); //increase by period
				logger.debug("newFirstTime = "+new Date(newFirstTime)+" period="+period+", task ="+task);
				timer.scheduleAtFixedRate(task, new Date(newFirstTime), period * 1000L);
			}
		}
		logger.debug("timer = "+timer);
	}
	
	public boolean reloadIndexingSchedule(String site, String category, boolean isActive){
		if(isActive){
			ScrapingSchedule schedules = DBHandler.getInstance().getScrapingSchedule();
			if(schedules.select(site, category) > 0){
				setIndexSchedule(schedules.site, schedules.category, schedules.startTime, schedules.period, schedules.isActive);
			}
		}else{
			String key = getKey(site, category);
			TimerTask oldTask = taskMap.remove(key);
			if(oldTask != null){
				oldTask.cancel();
			}
		}
		return false;
	}
	
	public boolean reload() throws SettingException{
		stop();
		load();
		return true;
	}
	
	public void stop(){
		timer.cancel();
		taskMap.clear();
	}
	
	class JobTask extends TimerTask{
		private Job job;
		
		public JobTask(String jobClassName, String args){
			logger.info("load Dynamic class");
			job = (Job) CatbotSettings.classLoader.loadObject(jobClassName);
			String[] arglist = args.split(" ");
			job.setArgs(arglist);
			job.setScheduled(true); //scheduled job.
		}
		
		@SuppressWarnings("unused")
		public void run(){
			JobResult jobResult = null;
			
			try {
				jobResult = JobController.getInstance().offer(job);
			} catch (NullPointerException e) {
				logger.error(e.getMessage(),e);
			}
			
			if(jobResult == null){
				//ignore
				logger.debug("jobResult = "+jobResult);
				logger.debug("Scheduled job "+job+" is ignored.");
			}else{
				logger.debug("jobResult = "+jobResult);
				//여기에서 result를 기다리면서 길어지면 다음 Job Task이 실행이 안된다.
				//그러므로 결과를 받지 않도록 한다.
				//swsong 2012-11-10
//				Object result = jobResult.take();
//				logger.debug("Schedule Job Result = "+result);
			}
		}
	}
	
}

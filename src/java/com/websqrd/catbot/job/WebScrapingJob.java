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

package com.websqrd.catbot.job;

import com.websqrd.catbot.CatBot;
import com.websqrd.catbot.control.JobException;
import com.websqrd.catbot.exception.CatbotException;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.CategoryConfig;

public class WebScrapingJob extends Job {

	private static final long serialVersionUID = -8711569878991720083L;

	public static void main(String[] args) throws JobException, ServiceException {
		String homePath = args[0];
		String site = args[1];
		String category = args[2];
		String scrapAction=args[3];
		CatbotSettings.setHome(homePath);
		
		WebScrapingJob job = new WebScrapingJob();
		job.setArgs(new String[]{site, category, scrapAction});
		job.run();
	}
	
	@Override
	public JobResultScraping run0() throws JobException, ServiceException {
		String[] args = getStringArrayArgs();
		String site = (String)args[0];
		String category = (String)args[1];
		String scraptionAction = CategoryConfig.SCRAPGETNEW;
		boolean test = false;
		if ( args.length > 2)
			scraptionAction = (String)args[2];
		
		if ( args.length > 3){
			if(args[3].equals("test")){
				test = true;
			}
		}
		logger.info("["+site+"] "+ "[Action : " + scraptionAction + " ] "  + category+ (test?" Test":"")+ " Scraping Start!");
		
		long st = System.currentTimeMillis();
		int count = 0;
		CatBot catbot = new CatBot();
		try {
			count = catbot.doWork(site, category, scraptionAction, test);
		} catch (CatbotException e) {
			logger.error("수집중 에러발생.",e);
		}
		
		int duration = (int) (System.currentTimeMillis() - st);
		if(test){
//			logger.debug("catbot.getTestBuffer() >> {}",catbot.getTestBuffer());
			return new JobResultScraping(site, count, duration, catbot.getTestBuffer());
		}else{
			return new JobResultScraping(site, count, duration);
		}
	}

}

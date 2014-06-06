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

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.control.JobController;
import com.websqrd.catbot.control.JobException;
import com.websqrd.catbot.service.ServiceException;


public abstract class Job implements Runnable, Serializable{
	private static final long serialVersionUID = 6296052043270199612L;

	protected static Logger logger = LoggerFactory.getLogger(Job.class);
	
	protected long jobId = -1;
	protected Object args;
	protected boolean isScheduled;
	protected boolean noResult; //결과가 필요없는 단순 호출 작업
	
	public String toString(){
		return "[Job] jobId = "+jobId+", args = "+args+", isScheduled="+isScheduled+", noResult="+noResult;
//		return "[Job] jobId = "+jobId+", args = "+args+", isScheduled="+isScheduled;
	}
	public void setId(long jobId) {
		this.jobId = jobId;
	}

	public long getId(){
		return jobId;
	}
	
	public void setArgs(Object args){
		this.args = args;
	}
	
	public Object getArgs(){
		return args;
	}
	public String getStringArgs(int i){
		return ((String[])args)[i];
	}
	public String getStringArgs(){
		return (String)args;
	}
	public String[] getStringArrayArgs(){
		return (String[])args;
	}
	public void setScheduled(boolean isScheduled) {
		this.isScheduled = isScheduled;
	}

	public boolean isScheduled(){
		return isScheduled;
	}
	
	public void setNoResult(){
		noResult = true; 
	}
	
	public boolean isNoResult(){
		return noResult;
	}
	
	public abstract Object run0() throws JobException, ServiceException;

	public final void run(){
		long st = System.currentTimeMillis();
		try {
			Object result = run0();
//			if(jobId != -1 && !noResult){
			if(jobId != -1){
				logger.debug("Job_{} result = {}", jobId, result);
				JobController.getInstance().result(jobId, this, result, true, st, System.currentTimeMillis());
			}
			
		} catch (JobException e){
			JobController.getInstance().result(jobId, this, e.getMessage(), false, st, System.currentTimeMillis());
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.error("#############################################\n"+w.toString()+"\\n#############################################");
		} catch (OutOfMemoryError e){
			JobController.getInstance().result(jobId, this, e.getMessage(), false, st, System.currentTimeMillis());
			StringWriter w = new StringWriter();
			e.printStackTrace();
			e.printStackTrace(new PrintWriter(w));
			logger.error("#############################################\n"+w.toString()+"\n#############################################");
		} catch (Exception e){
			JobController.getInstance().result(jobId, this, e.getMessage(), false, st, System.currentTimeMillis());
			StringWriter w = new StringWriter();
			e.printStackTrace(new PrintWriter(w));
			logger.error("#############################################\n"+w.toString()+"\n#############################################");
		} catch(Error err){
			JobController.getInstance().result(jobId, this, err.getMessage(), false, st, System.currentTimeMillis());
			StringWriter w = new StringWriter();
			err.printStackTrace(new PrintWriter(w));
			logger.error("#############################################\n"+w.toString()+"\n#############################################");
		}
	}
	
	protected void logError(Throwable e){
		StringWriter sw = new StringWriter();
		PrintWriter writer = new PrintWriter(sw);
		writer.println("#############################################");
		e.printStackTrace(writer);
		writer.println("#############################################");
		writer.close();
		logger.error(sw.toString());
	}
	
}

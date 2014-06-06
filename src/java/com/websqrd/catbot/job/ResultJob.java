package com.websqrd.catbot.job;

import com.websqrd.catbot.control.JobException;
import com.websqrd.catbot.service.ServiceException;

public class ResultJob extends Job {
	
	private static final long serialVersionUID = -2003755021642747642L;
	
	public Object getResult(){
		return args;
	}
	public Object run0() throws JobException, ServiceException {
		return null;
	}

}

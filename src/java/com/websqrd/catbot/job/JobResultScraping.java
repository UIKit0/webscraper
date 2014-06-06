package com.websqrd.catbot.job;

public class JobResultScraping {
	public String site;
	public int docSize;
	public int duration;
	public String testResultBuffer;
	
	public JobResultScraping(String site, int docSize, int duration) {
		this(site, docSize, duration, null);
	}
	public JobResultScraping(String site, int docSize, int duration, String testResultBuffer) {
		this.site = site;
		this.docSize = docSize;
		this.duration = duration;
		this.testResultBuffer = testResultBuffer;
	}

	public String toString(){
		return "[JobResultScraping] site = "+site+", docSize = "+docSize+", duration = "+duration;
	}
}

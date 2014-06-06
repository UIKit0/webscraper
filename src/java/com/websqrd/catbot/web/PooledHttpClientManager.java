package com.websqrd.catbot.web;

import java.util.concurrent.TimeUnit;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreConnectionPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PooledHttpClientManager {
	private static PooledHttpClientManager instance = null;
	private static PoolingClientConnectionManager pcm = null;
	private static Logger logger = LoggerFactory.getLogger(PooledHttpClientManager.class);
	private static Logger httpPoolLoger = LoggerFactory.getLogger("HTTP_POOL");
	private static IdleConnectionMonitorThread monitor=null;
	
	public static PooledHttpClientManager getInstance() {
		
		if ( instance == null )
			{
			instance = new PooledHttpClientManager();
			monitor = new IdleConnectionMonitorThread(pcm);
			monitor.start();
			}
		
		return instance;
	}
	
	public static void releaseInstance() {
		monitor.shutdown();
		pcm.shutdown();
		instance = null;
	}
	
	private PooledHttpClientManager() {
		pcm = new PoolingClientConnectionManager();
		pcm.setMaxTotal(100);
		pcm.setDefaultMaxPerRoute(20);		
		logger.info("HttpClient Pool Created");		
	}
	
	public HttpClient getHttpClient(int timeOut) {
		HttpClient httpClient = HttpClientWrapper.wrapPoolClient(new DefaultHttpClient(pcm));
		httpClient.getParams().setIntParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		httpClient.getParams().setIntParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);		
		logger.info("Pool Status : {}", pcm.getTotalStats().toString());
		httpPoolLoger.info("Pool Status : {}", pcm.getTotalStats().toString());
		return httpClient;		
	}
	
	public void getStatus()
	{
		logger.info("Pool Status : {}", pcm.getTotalStats().toString());
	}
	
	public void releaseClosedConnection()
	{
		if ( pcm != null )
		{
			pcm.closeExpiredConnections();
			pcm.closeIdleConnections(30, TimeUnit.SECONDS);
		}
	}
}

package com.websqrd.catbot.web;

import java.util.concurrent.TimeUnit;

import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class IdleConnectionMonitorThread extends Thread {
	private static Logger httpPoolLoger = LoggerFactory.getLogger("HTTP_POOL");	
	private final ClientConnectionManager connMgr;
	    private volatile boolean shutdown;
	    
	    public IdleConnectionMonitorThread(ClientConnectionManager connMgr) {
	        super();
	        this.connMgr = connMgr;
	    }

	    @Override
	    public void run() {
	        try {
	            while (!shutdown) {
	                synchronized (this) {
	                    wait(5000);
	                    // Close expired connections
	                    connMgr.closeExpiredConnections();
	                    // Optionally, close connections
	                    // that have been idle longer than 30 sec
	                    connMgr.closeIdleConnections(30, TimeUnit.SECONDS);                    
	                    
//	                    httpPoolLoger.info("Pool Status : {}", ((PoolingClientConnectionManager)connMgr).getTotalStats().toString());
	                }
	            }
	        } catch (InterruptedException ex) {
	            // terminate
	        }
	    }
	    
	    public void shutdown() {
	        shutdown = true;
	        synchronized (this) {
	            notifyAll();
	        }
	    }

}

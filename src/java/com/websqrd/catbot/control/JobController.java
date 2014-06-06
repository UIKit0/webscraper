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
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.websqrd.catbot.db.DBHandler;
import com.websqrd.catbot.db.ScrapingHistory;
import com.websqrd.catbot.db.ScrapingResult;
import com.websqrd.catbot.job.WebScrapingJob;
import com.websqrd.catbot.job.Job;
import com.websqrd.catbot.job.JobResultScraping;
import com.websqrd.catbot.service.CatBotServiceComponent;
import com.websqrd.catbot.service.ServiceException;
import com.websqrd.catbot.setting.CatbotConfig;
import com.websqrd.catbot.setting.CatbotServerConfig;
import com.websqrd.catbot.setting.CatbotSettings;
import com.websqrd.catbot.setting.SettingException;

/**
 * 
 * @author sangwook.song
 * 
 */

public class JobController extends CatBotServiceComponent {
	private static Logger logger = LoggerFactory.getLogger(JobController.class);

	private BlockingQueue<Job> jobQueue;
	private static Map<Long, JobResult> resultMap;
	private static AtomicLong jobId = new AtomicLong();

	private static JobController instance = new JobController();
	private ThreadPoolExecutor jobExecutor;
	private JobControllerWorker worker;
	private JobScheduler jobScheduler;
	private IndexingMutex indexingMutex;

	// private boolean isManager;

	private JobController() {
	}

	public static JobController getInstance() {
		return instance;
	}

	public Collection<String> getIndexingList() {
		return indexingMutex.getIndexingList();
	}

	public void setSchedule(String key, String jobClassName, String args, Timestamp startTime, int period, boolean isYN) throws SettingException {
		jobScheduler.setSchedule(key, jobClassName, args, startTime, period, isYN);
	}

	public boolean reloadSchedules() throws SettingException {
		return jobScheduler.reload();
	}

	public boolean toggleIndexingSchedule(String site, String category, boolean isActive) {
		return jobScheduler.reloadIndexingSchedule(site, category, isActive);
	}

	public ThreadPoolExecutor getJobExecutor() {
		return jobExecutor;
	}

	public JobResult offer(Job job) {
		if (job instanceof WebScrapingJob) {
			if (indexingMutex.isLocked(job)) {
				logger.info("The collection [" + indexingMutex.getScrapingKey((WebScrapingJob) job) + "]  has already started an scraping job.");
				return null;
			}
		}

		long myJobId = jobId.getAndIncrement();
		logger.debug("### OFFER Job-" + myJobId);

		if (job instanceof WebScrapingJob) {
			indexingMutex.access(myJobId, job);
			DBHandler dbHandler = DBHandler.getInstance();
			String site = job.getStringArgs(0);
			String category = job.getStringArgs(1);
			logger.debug("job=" + job + ", " + site + ", " + category);

			boolean isTest = false;
			String[] args = (String[]) job.getArgs();
			if (args.length > 3) {
				if (args[3].equals("test")) {
					isTest = true;
				}
			}
			//테스트일 경우 DB에 쓰지도 않는다.
			if (!isTest) {
				dbHandler.getScrapingResult().updateOrInsert(site, category, ScrapingResult.STATUS_RUNNING, -1, -1, -1, job.isScheduled(), new Timestamp(System.currentTimeMillis()), null, 0);
			}
		}

		if (job.isNoResult()) {
			job.setId(myJobId);
			jobQueue.offer(job);
			return null;
		} else {
			JobResult jobResult = new JobResult();
			resultMap.put(myJobId, jobResult);
			job.setId(myJobId);
			jobQueue.offer(job);
			return jobResult;
		}
	}

	public void result(long jobId, Job job, Object result, boolean isSuccess, long st, long et) {

		JobResult jobResult = resultMap.remove(jobId);
		logger.debug("### JobResult = " + jobResult + " / " + resultMap.size() + " / " + result);

		if (job instanceof WebScrapingJob) {
			indexingMutex.release(jobId);
			DBHandler dbHandler = DBHandler.getInstance();
			String site = job.getStringArgs(0);
			String category = job.getStringArgs(1);
			logger.debug("job=" + job + ", " + site + ", " + category);

			ScrapingResult scrapingResult = dbHandler.getScrapingResult();
			ScrapingHistory scrapingHistory = dbHandler.getScrapingHistory();

			int status = isSuccess ? ScrapingResult.STATUS_SUCCESS : ScrapingResult.STATUS_FAIL;

			boolean isTest = false;
			if (result instanceof JobResultScraping) {
				// 테스트일경우 기록을 남기지 않는다.
				String[] args = (String[]) job.getArgs();
				if (args.length > 3) {
					if (args[3].equals("test")) {
						isTest = true;
					}
				}
				if (!isTest) {
					JobResultScraping jobResultScraping = (JobResultScraping) result;
					scrapingResult.update(site, category, status, jobResultScraping.docSize, 0, 0, job.isScheduled(), new Timestamp(st), new Timestamp(et), (int) (et - st));
					scrapingHistory.insert(site, category, isSuccess, jobResultScraping.docSize, 0, 0, job.isScheduled(), new Timestamp(st), new Timestamp(et), (int) (et - st));
				}
			} else {
				scrapingResult.update(site, category, status, 0, 0, 0, job.isScheduled(), new Timestamp(st), new Timestamp(et), (int) (et - st));
				scrapingHistory.insert(site, category, isSuccess, 0, 0, 0, job.isScheduled(), new Timestamp(st), new Timestamp(et), (int) (et - st));
			}

		}

		if (result != null) {
			if (jobResult != null) {
				try {
					logger.debug("jobResult.put(" + result + ")");
					jobResult.put(result, isSuccess);
				} catch (InterruptedException e) {
				}
			} else {
				logger.info("cannot find where to send a result.");
			}

		} else {
			logger.warn("Job-" + jobId + " has no return value.");
			if (jobResult != null) {
				try {
					jobResult.put(new Object(), isSuccess);
				} catch (InterruptedException e) {
				}
			}
		}

	}

	protected boolean start0() throws ServiceException {
		resultMap = new ConcurrentHashMap<Long, JobResult>();
		jobQueue = new LinkedBlockingQueue<Job>();
		indexingMutex = new IndexingMutex();
		CatbotConfig config = CatbotSettings.getGlobalConfig();
		jobScheduler = new JobScheduler(config.getSiteList());

		CatbotServerConfig irconfig = CatbotSettings.getCatbotServerConfig();

		int executorCorePoolSize = irconfig.getInt("jobExecutor.core.poolsize");
		int executorMaxPoolSize = irconfig.getInt("jobExecutor.max.poolsize");
		int executorKeepAliveTime = irconfig.getInt("jobExecutor.keepAliveTime");
		jobExecutor = new ThreadPoolExecutor(executorCorePoolSize, executorMaxPoolSize, executorKeepAliveTime, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(executorCorePoolSize),
		                new ThreadPoolExecutor.DiscardPolicy());

		worker = new JobControllerWorker();
		worker.start();

		jobScheduler.start();
		logger.debug("JobController started!");

		return true;
	}

	protected boolean shutdown0() {
		logger.debug("JobController shutdown requested.");
		worker.interrupt();
		resultMap.clear();
		jobQueue.clear();
		jobExecutor.shutdownNow();
		jobScheduler.stop();
		logger.debug("JobController shutdown OK!");
		return true;
	}

	class JobControllerWorker extends Thread {

		public void run() {
			try {
				while (!Thread.interrupted()) {
					Job job = jobQueue.take();
					logger.debug("Execute = " + job);
					jobExecutor.execute(job);
				}
			} catch (InterruptedException e) {
				logger.debug(this.getClass().getName() + " is interrupted.");
			}

		}
	}
}

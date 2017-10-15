package com.moomanow.miner.job;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.dao.MinerControlDao;
import com.moomanow.miner.utiles.DownloadUtils;

public class DownloadMinerJob extends QuartzJobBean {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(DownloadMinerJob.class.getName());
	
	private MinerControlDao minerControlDao;
	@Autowired
	@Required
	public void setMinerControlDao(MinerControlDao minerControlDao) {
		this.minerControlDao = minerControlDao;
	}

	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - start"); //$NON-NLS-1$
		}

		// download miner
		Set<String> listAlg = minerControlDao.getListAlg();
		Map<String, Set<IAppMiner>> mapAlgAppMiner = minerControlDao.getMapAlgAppMiner();
		Map<String, FutureTask<Boolean>> minerDoenloadIng = minerControlDao.getMinerDoenloadIng();
		ExecutorService executorDownload = minerControlDao.getExecutorDownload();
		listAlg.stream().forEach((String alg) -> {
			Set<IAppMiner> appMiners = mapAlgAppMiner.get(alg);
			if (appMiners == null)
				return;
			appMiners.parallelStream().filter((appMiner) -> !appMiner.hasDownloaded()&&!minerDoenloadIng.containsKey(appMiner.getConfigMinerBean().getMinerName())).forEach((appMiner) -> {
				FutureTask<Boolean> futureTaskDownload = new FutureTask<>(() -> {
					DownloadUtils.download(appMiner.getConfigMinerBean().getUrlDownload(), "./miner/" + appMiner.getConfigMinerBean().getMinerName());
					return null;
				});
				minerDoenloadIng.put(appMiner.getConfigMinerBean().getMinerName(), futureTaskDownload);
				executorDownload.execute(futureTaskDownload);
			});
		});

		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}

}

package com.moomanow.miner.job;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;
import com.moomanow.miner.dao.MinerControlDao;

public class CheckMinerJob extends QuartzJobBean {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(CheckMinerJob.class.getName());

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

		Set<String> listAlg = minerControlDao.getListAlg();
		Map<String, Set<IAppMiner>> mapAlgAppMiner = minerControlDao.getMapAlgAppMiner();
		Map<String, FutureTask<Boolean>> minerDoenloadIng = minerControlDao.getMinerDoenloadIng();
		ExecutorService executorDownload = minerControlDao.getExecutorDownload();
		List<ConfigMinerBean> configMinerBeans = minerControlDao.getConfigMinerBeans();
		ConfigUserBean configUserBean = minerControlDao.getConfigUserBean();
		List<ConfigPoolBean> configPoolBeans = minerControlDao.getConfigPoolBeans();
		Map<String, IPoolApi> allPools = minerControlDao.getAllPools();
		Map<String, Class<? extends IPoolApi>> mapPoolApi = minerControlDao.getMapPoolApi();
		Map<String, IAppMiner> allMiners = minerControlDao.getAllMiners();
		Map<String, Class<? extends IAppMiner>> mapAppMiner = minerControlDao.getMapAppMiner();
		Map<String, Set<IPoolApi>> mapAlgPoolApi = minerControlDao.getMapAlgPoolApi();
		Map<String, FutureTask<Boolean>> futureTaskPools = minerControlDao.getFutureTaskPools();
		Set<RevenueBean> revenueBeans = minerControlDao.getRevenueBeans();
		Map<RevenueBean, IAppMiner> runing = minerControlDao.getRuning();
		runing.entrySet().removeIf((revenueBeanEntry) -> {
			IAppMiner miner = revenueBeanEntry.getValue();
			if (miner.isRun()) {
				RevenueBean revenueBeanCheck = revenueBeanEntry.getKey();
				if(revenueBeanCheck.getPrice()==null) {
					Long time = miner.getConfigMinerBean().getTotalTimeBenchSec();
					if(time ==null)
						time = 60L;
					long totalTime = Calendar.getInstance().getTimeInMillis()-miner.getTimeStartLong();
					if(totalTime>time*1000) {
						miner.stopBench();
						return true;
					}else {
						miner.check();
					}
				}else {
					miner.check();
				}
			}
			return false;
		});

		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}

}

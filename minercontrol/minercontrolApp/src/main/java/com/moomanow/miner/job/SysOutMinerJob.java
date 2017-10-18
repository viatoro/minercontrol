package com.moomanow.miner.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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

public class SysOutMinerJob extends QuartzJobBean {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(SysOutMinerJob.class.getName());

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
				try {
		            InputStreamReader isr = new InputStreamReader(miner.getStdIn());
		            BufferedReader br = new BufferedReader(isr);
		            String line=null;
		            while ( (line = br.readLine()) != null)
		                System.out.println(line);    
		        } catch (IOException ioe) {
		            ioe.printStackTrace();  
		        }
			}
			return false;
		});

		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}

}

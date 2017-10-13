package com.moomanow.miner.job;

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
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.bean.RatePrice;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;
import com.moomanow.miner.dao.MinerControlDao;

public class MathPoolToMinerJob extends QuartzJobBean {

	private MinerControlDao minerControlDao;
	@Autowired
	@Required
	public void setMinerControlDao(MinerControlDao minerControlDao) {
		this.minerControlDao = minerControlDao;
	}
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {
		// cal
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
		listAlg.stream().forEach((String alg) -> {
			Set<IAppMiner> appMiners = mapAlgAppMiner.get(alg);
			if (appMiners == null)
				return;
			appMiners.stream().filter((appMiner) -> appMiner.hasDownloaded()).forEach((appMiner) -> {
				Set<IPoolApi> poolapis = mapAlgPoolApi.get(alg);
				if (poolapis == null)
					return;
				poolapis.forEach((pool) -> {
					HashRate hashRate = appMiner.getHashRateBenchmarked(alg);
					RatePrice price = pool.getMapRatePrices().get(alg);
					RevenueBean revenueBean = new RevenueBean();
					revenueBean.setAlg(alg);
					revenueBean.setPool(pool);
					revenueBean.setMiner(appMiner);
					if(hashRate!=null) {
						revenueBean.setPrice(hashRate.getRate().multiply(price.getPrice()));
					}
					revenueBeans.add(revenueBean);
				});

			});
		});
	}

}

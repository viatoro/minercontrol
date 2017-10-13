package com.moomanow.miner.job;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.FutureTask;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.dao.MinerControlDao;

public class DataPoolJob extends QuartzJobBean {

	
	@Autowired
	private MinerControlDao minerControlDao;
	@Override
	protected void executeInternal(JobExecutionContext arg0) throws JobExecutionException {

		Map<String, IPoolApi> allPools = minerControlDao.getAllPools();
		Set<String> listAlg = minerControlDao.getListAlg();
		Map<String, Set<IPoolApi>> mapAlgPoolApi = minerControlDao.getMapAlgPoolApi();
		Map<String, FutureTask<Boolean>> futureTaskPools = minerControlDao.getFutureTaskPools();
		allPools.values().stream().forEach((IPoolApi iPoolApi) -> {
			String poolName = iPoolApi.getConfigPoolBean().getName();
			FutureTask<Boolean> futureTaskPool = futureTaskPools.get(poolName);
			if(futureTaskPool!=null&&!futureTaskPool.isDone())
				return;
			futureTaskPool = new FutureTask<>(() -> {
				Boolean check = iPoolApi.checkRate();
				listAlg.addAll(iPoolApi.getAlg());
				for (String alg : iPoolApi.getAlg()) {
					Set<IPoolApi> iPoolApis = mapAlgPoolApi.get(alg);
					if (iPoolApis == null) {
						iPoolApis = new HashSet<>();
						mapAlgPoolApi.put(alg, iPoolApis);
					}
					iPoolApis.add(iPoolApi);
				}
				return check;
			});
			futureTaskPools.put(poolName,futureTaskPool);
			minerControlDao.getExecutorPool().execute(futureTaskPool);
		});
	}

}

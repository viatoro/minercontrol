package com.moomanow.miner.dao;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;

public interface MinerControlDao {
	

	public Map<String, IAppMiner> getAllMiners();

	public void setAllMiners(Map<String, IAppMiner> allMiners);

	public Map<RevenueBean, IAppMiner> getRuning();

	public void setRuning(Map<RevenueBean, IAppMiner> runing);

	public List<Process> getProcessBenchmarking();

	public void setProcessBenchmarking(List<Process> processBenchmarking);

	public List<Process> getProcessMining();

	public void setProcessMining(List<Process> processMining);

	public Map<String, IPoolApi> getAllPools();

	public void setAllPools(Map<String, IPoolApi> allPools);

	public Set<String> getListAlg();

	public void setListAlg(Set<String> listAlg);

	public Map<String, Class<? extends IPoolApi>> getMapPoolApi();

	public void setMapPoolApi(Map<String, Class<? extends IPoolApi>> mapPoolApi);

	public Map<String, Class<? extends IAppMiner>> getMapAppMiner();

	public void setMapAppMiner(Map<String, Class<? extends IAppMiner>> mapAppMiner);

	public Map<String, Set<IAppMiner>> getMapAlgAppMiner();

	public void setMapAlgAppMiner(Map<String, Set<IAppMiner>> mapAlgAppMiner);

	public Map<String, Set<IPoolApi>> getMapAlgPoolApi();

	public void setMapAlgPoolApi(Map<String, Set<IPoolApi>> mapAlgPoolApi);

	public List<ConfigMinerBean> getConfigMinerBeans();

	public void setConfigMinerBeans(List<ConfigMinerBean> configMinerBeans);

	public List<ConfigPoolBean> getConfigPoolBeans();

	public void setConfigPoolBeans(List<ConfigPoolBean> configPoolBeans);

	public ConfigUserBean getConfigUserBean();

	public void setConfigUserBean(ConfigUserBean configUserBean);

	public Set<RevenueBean> getRevenueBeans();

	public void setRevenueBeans(Set<RevenueBean> revenueBeans);

	public boolean isBending();

	public void setBending(boolean bending);

	public Long getBenTimeEnd();

	public void setBenTimeEnd(Long benTimeEnd);
	
	public ExecutorService getExecMain();

	public void setExecMain(ExecutorService execMain);

	public ExecutorService getExecutorPool();

	public void setExecutorPool(ExecutorService executorPool);

	public ExecutorService getExecutorDownload();

	public void setExecutorDownload(ExecutorService executorDownload);

	public ExecutorService getExecutorCheckHashRate();

	public void setExecutorCheckHashRate(ExecutorService executorCheckHashRate);

	public Map<String, FutureTask<Boolean>> getFutureTaskPools();

	public void setFutureTaskPools(Map<String, FutureTask<Boolean>> futureTaskPools);

	public Map<String, FutureTask<Boolean>> getMinerDoenloadIng();

	public void setMinerDoenloadIng(Map<String, FutureTask<Boolean>> minerDoenloadIng);
	

}

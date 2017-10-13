package com.moomanow.miner.dao.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;
import com.moomanow.miner.dao.MinerControlDao;

public class MinerControlDaoMemImpl implements MinerControlDao {
	
	private Map<String, IAppMiner> allMiners = new ConcurrentHashMap<>();
	private Map<RevenueBean,IAppMiner> runing = new ConcurrentHashMap<>();
	
	private List<Process> processBenchmarking = new ArrayList<>();
	private List<Process> processMining = new ArrayList<>();
	private Map<String, IPoolApi> allPools = new ConcurrentHashMap<>();
	private Set<String> listAlg = Collections.newSetFromMap(new ConcurrentHashMap<>());

	private Map<String, Class<? extends IPoolApi>> mapPoolApi = new ConcurrentHashMap<>();

	private Map<String, Class<? extends IAppMiner>> mapAppMiner = new ConcurrentHashMap<>();

	private Map<String, Set<IAppMiner>> mapAlgAppMiner = new ConcurrentHashMap<>();
	private Map<String, Set<IPoolApi>> mapAlgPoolApi = new ConcurrentHashMap<>();

	private List<ConfigMinerBean> configMinerBeans;
	private List<ConfigPoolBean> configPoolBeans;
	private ConfigUserBean configUserBean;
	private Set<RevenueBean> revenueBeans = Collections.newSetFromMap(new ConcurrentHashMap<>());
	private boolean bending;

//	private Long benTimeEnd;
	
	private ExecutorService execMain, executorPool, executorDownload, executorCheckHashRate;

	private Map<String,FutureTask<Boolean>> futureTaskPools= new ConcurrentHashMap<>();
	
	private Map<String,FutureTask<Boolean>> minerDoenloadIng = new ConcurrentHashMap<>();

	public Map<String, IAppMiner> getAllMiners() {
		return allMiners;
	}

	public void setAllMiners(Map<String, IAppMiner> allMiners) {
		this.allMiners = allMiners;
	}

	public Map<RevenueBean, IAppMiner> getRuning() {
		return runing;
	}

	public void setRuning(Map<RevenueBean, IAppMiner> runing) {
		this.runing = runing;
	}

	public List<Process> getProcessBenchmarking() {
		return processBenchmarking;
	}

	public void setProcessBenchmarking(List<Process> processBenchmarking) {
		this.processBenchmarking = processBenchmarking;
	}

	public List<Process> getProcessMining() {
		return processMining;
	}

	public void setProcessMining(List<Process> processMining) {
		this.processMining = processMining;
	}

	public Map<String, IPoolApi> getAllPools() {
		return allPools;
	}

	public void setAllPools(Map<String, IPoolApi> allPools) {
		this.allPools = allPools;
	}

	public Set<String> getListAlg() {
		return listAlg;
	}

	public void setListAlg(Set<String> listAlg) {
		this.listAlg = listAlg;
	}

	public Map<String, Class<? extends IPoolApi>> getMapPoolApi() {
		return mapPoolApi;
	}

	public void setMapPoolApi(Map<String, Class<? extends IPoolApi>> mapPoolApi) {
		this.mapPoolApi = mapPoolApi;
	}

	public Map<String, Class<? extends IAppMiner>> getMapAppMiner() {
		return mapAppMiner;
	}

	public void setMapAppMiner(Map<String, Class<? extends IAppMiner>> mapAppMiner) {
		this.mapAppMiner = mapAppMiner;
	}

	public Map<String, Set<IAppMiner>> getMapAlgAppMiner() {
		return mapAlgAppMiner;
	}

	public void setMapAlgAppMiner(Map<String, Set<IAppMiner>> mapAlgAppMiner) {
		this.mapAlgAppMiner = mapAlgAppMiner;
	}

	public Map<String, Set<IPoolApi>> getMapAlgPoolApi() {
		return mapAlgPoolApi;
	}

	public void setMapAlgPoolApi(Map<String, Set<IPoolApi>> mapAlgPoolApi) {
		this.mapAlgPoolApi = mapAlgPoolApi;
	}

	public List<ConfigMinerBean> getConfigMinerBeans() {
		return configMinerBeans;
	}

	public void setConfigMinerBeans(List<ConfigMinerBean> configMinerBeans) {
		this.configMinerBeans = configMinerBeans;
	}

	public List<ConfigPoolBean> getConfigPoolBeans() {
		return configPoolBeans;
	}

	public void setConfigPoolBeans(List<ConfigPoolBean> configPoolBeans) {
		this.configPoolBeans = configPoolBeans;
	}

	public ConfigUserBean getConfigUserBean() {
		return configUserBean;
	}

	public void setConfigUserBean(ConfigUserBean configUserBean) {
		this.configUserBean = configUserBean;
	}

	public Set<RevenueBean> getRevenueBeans() {
		return revenueBeans;
	}

	public void setRevenueBeans(Set<RevenueBean> revenueBeans) {
		this.revenueBeans = revenueBeans;
	}

	public boolean isBending() {
		return bending;
	}

	public void setBending(boolean bending) {
		this.bending = bending;
	}

//	public Long getBenTimeEnd() {
//		return benTimeEnd;
//	}
//
//	public void setBenTimeEnd(Long benTimeEnd) {
//		this.benTimeEnd = benTimeEnd;
//	}

	public ExecutorService getExecMain() {
		return execMain;
	}

	public void setExecMain(ExecutorService execMain) {
		this.execMain = execMain;
	}

	public ExecutorService getExecutorPool() {
		return executorPool;
	}

	public void setExecutorPool(ExecutorService executorPool) {
		this.executorPool = executorPool;
	}

	public ExecutorService getExecutorDownload() {
		return executorDownload;
	}

	public void setExecutorDownload(ExecutorService executorDownload) {
		this.executorDownload = executorDownload;
	}

	public ExecutorService getExecutorCheckHashRate() {
		return executorCheckHashRate;
	}

	public void setExecutorCheckHashRate(ExecutorService executorCheckHashRate) {
		this.executorCheckHashRate = executorCheckHashRate;
	}

	public Map<String, FutureTask<Boolean>> getFutureTaskPools() {
		return futureTaskPools;
	}

	public void setFutureTaskPools(Map<String, FutureTask<Boolean>> futureTaskPools) {
		this.futureTaskPools = futureTaskPools;
	}

	public Map<String, FutureTask<Boolean>> getMinerDoenloadIng() {
		return minerDoenloadIng;
	}

	public void setMinerDoenloadIng(Map<String, FutureTask<Boolean>> minerDoenloadIng) {
		this.minerDoenloadIng = minerDoenloadIng;
	}
	
	
	
	
	

}

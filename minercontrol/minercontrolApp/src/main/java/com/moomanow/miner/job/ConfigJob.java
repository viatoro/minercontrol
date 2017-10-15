package com.moomanow.miner.job;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.quartz.QuartzJobBean;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;
import com.moomanow.miner.config.dao.ConfigMinerDao;
import com.moomanow.miner.dao.MinerControlDao;

public class ConfigJob extends QuartzJobBean {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(ConfigJob.class.getName());

	
	private ConfigMinerDao configMinerDao;
	@Autowired
	@Required
	public void setConfigMinerDao(ConfigMinerDao configMinerDao) {
		this.configMinerDao = configMinerDao;
	}
	
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

		loadConfig();
		List<ConfigMinerBean> configMinerBeans = minerControlDao.getConfigMinerBeans();
//		ConfigUserBean configUserBean = minerControlDao.getConfigUserBean();
		List<ConfigPoolBean> configPoolBeans = minerControlDao.getConfigPoolBeans();
		Map<String, IPoolApi> allPools = minerControlDao.getAllPools();
		Map<String, Class<? extends IPoolApi>> mapPoolApi = minerControlDao.getMapPoolApi();
		Map<String, IAppMiner> allMiners = minerControlDao.getAllMiners();
		Map<String, Class<? extends IAppMiner>> mapAppMiner = minerControlDao.getMapAppMiner();
		Map<String, Set<IAppMiner>> mapAlgAppMiner = minerControlDao.getMapAlgAppMiner();
		
		


		for (ConfigPoolBean configPoolBean : configPoolBeans) {
			if (allPools.containsKey(configPoolBean.getName())) {
				IPoolApi iPoolApi = allPools.get(configPoolBean.getName());
				iPoolApi.setConfigPoolBean(configPoolBean);
				continue;
			}
			String poolType = configPoolBean.getPoolType();
			Class<? extends IPoolApi> classPoolApi = mapPoolApi.get(poolType);
			try {
				IPoolApi poolApi = classPoolApi.newInstance();
				poolApi.setConfigPoolBean(configPoolBean);
				allPools.put(configPoolBean.getName(), poolApi);
			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("executeInternal(JobExecutionContext)", e); //$NON-NLS-1$

				e.printStackTrace();
			}
		}
		

		for (ConfigMinerBean configMinerBean : configMinerBeans) {
			if (allMiners.containsKey(configMinerBean.getMinerName())) {
				IAppMiner iAppMiner = allMiners.get(configMinerBean.getMinerName());
				iAppMiner.setConfigMinerBean(configMinerBean);
				continue;
			}
			String appMinerType = configMinerBean.getAppMinerType();

			Class<? extends IAppMiner> classIAppMiner = mapAppMiner.get(appMinerType);
			try {
				IAppMiner appMiner = classIAppMiner.newInstance();
				appMiner.setConfigMinerBean(configMinerBean);
				allMiners.put(configMinerBean.getMinerName(), appMiner);
				for (String alg : configMinerBean.getAlg()) {
					Set<IAppMiner> appMiners = mapAlgAppMiner.get(alg);
					if (appMiners == null) {
						appMiners = new HashSet<>();
						mapAlgAppMiner.put(alg, appMiners);
					}
					appMiners.add(appMiner);
				}

			} catch (InstantiationException | IllegalAccessException e) {
				logger.error("executeInternal(JobExecutionContext)", e); //$NON-NLS-1$

				e.printStackTrace();
			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}
	private void loadConfig() {
		if (logger.isDebugEnabled()) {
			logger.debug("loadConfig() - start"); //$NON-NLS-1$
		}

		List<ConfigMinerBean> configMinerBeans = minerControlDao.getConfigMinerBeans();
		ConfigUserBean configUserBean = minerControlDao.getConfigUserBean();
		List<ConfigPoolBean> configPoolBeans = minerControlDao.getConfigPoolBeans();
//		ConfigMiner configMiner = new ConfigMinerJson();

		configMinerBeans = configMinerDao.loadConfigMiner();
		if (configMinerBeans == null) {
			configMinerBeans = new ArrayList<ConfigMinerBean>();
			ConfigMinerBean configMinerBean = new ConfigMinerBean();
			Set<String> alg = new HashSet<String>();
			alg.add("bitcore");
			alg.add("blake2s");
			alg.add("blakecoin");
			alg.add("vanilla");
			alg.add("c11");
			alg.add("cryptonight");
			alg.add("decred");
			alg.add("equihash");
			// alg.add("ethash");
			alg.add("groestl");
			alg.add("hmq1725");
			alg.add("jha");
			alg.add("keccak");
			alg.add("lbry");
			alg.add("lyra2v2");
			alg.add("lyra2z");
			alg.add("myr-gr");
			alg.add("neoscrypt");
			alg.add("nist5");
			alg.add("pascal");
			alg.add("quark");
			alg.add("qubit");
			alg.add("scrypt");
			alg.add("sia");
			alg.add("sib");
			alg.add("skein");
			alg.add("skunk");
			alg.add("timetravel");
			alg.add("tribus");
			alg.add("veltor");
			alg.add("x11");
			alg.add("x11evo");
			// alg.add("x17");
			alg.add("yescrypt");
			configMinerBean.setAlg(alg);
			configMinerBean.setAppMinerType("ccminer");
			configMinerBean.setMinerName("TPruvot");
			configMinerBean.setProgame("ccminer-X64.exe");
			configMinerBean.setUrlDownload("https://github.com/tpruvot/ccminer/releases/download/v2.2-tpruvot/ccminer-x64-2.2.7z");
			configMinerBeans.add(configMinerBean);

			configMinerBean = new ConfigMinerBean();
			alg = new HashSet<String>();
			// alg.add("bitcore");
			// alg.add("blake2s");
			// alg.add("blakecoin");
			// alg.add("vanilla");
			// alg.add("c11");
			// alg.add("cryptonight");
			// alg.add("decred");
			// alg.add("equihash");
			// alg.add("ethash");
			// alg.add("groestl");
			// alg.add("hmq1725");
			// alg.add("jha");
			// alg.add("keccak");
			// alg.add("lbry");
			// alg.add("lyra2v2");
			// alg.add("lyra2z");
			// alg.add("myr-gr");
			// alg.add("neoscrypt");
			// alg.add("nist5");
			// alg.add("pascal");
			// alg.add("quark");
			// alg.add("qubit");
			// alg.add("scrypt");
			// alg.add("sia");
			// alg.add("sib");
			// alg.add("skein");
			alg.add("skunk");
			// alg.add("timetravel");
			// alg.add("tribus");
			// alg.add("veltor");
			// alg.add("x11");
			// alg.add("x11evo");
			// alg.add("x17");
			// alg.add("yescrypt");
			configMinerBean.setAlg(alg);
			configMinerBean.setAppMinerType("ccminer");
			configMinerBean.setMinerName("Skunk");
			configMinerBean.setProgame("ccminer.exe");
			configMinerBean.setUrlDownload("https://github.com/scaras/ccminer-2.2-mod-r1/releases/download/2.2-r1/2.2-mod-r1.zip");
			configMinerBeans.add(configMinerBean);

			configMinerBean = new ConfigMinerBean();
			alg = new HashSet<String>();
			// alg.add("bitcore");
			// alg.add("blake2s");
			// alg.add("blakecoin");
			// alg.add("vanilla");
			// alg.add("c11");
			// alg.add("cryptonight");
			// alg.add("decred");
			// alg.add("equihash");
			// alg.add("ethash");
			// alg.add("groestl");
			// alg.add("hmq1725");
			// alg.add("jha");
			// alg.add("keccak");
			// alg.add("lbry");
			// alg.add("lyra2v2");
			// alg.add("lyra2z");
			// alg.add("myr-gr");
			// alg.add("neoscrypt");
			// alg.add("nist5");
			// alg.add("pascal");
			// alg.add("quark");
			// alg.add("qubit");
			// alg.add("scrypt");
			// alg.add("sia");
			alg.add("sib");
			// alg.add("skein");
			// alg.add("skunk");
			// alg.add("timetravel");
			// alg.add("tribus");
			// alg.add("veltor");
			// alg.add("x11");
			// alg.add("x11evo");
			// alg.add("x17");
			// alg.add("yescrypt");
			configMinerBean.setAlg(alg);
			configMinerBean.setAppMinerType("ccminer");
			configMinerBean.setMinerName("Skunk");
			configMinerBean.setProgame("ccminer_x11gost.exe");
			configMinerBean.setUrlDownload("https://github.com/nicehash/ccminer-x11gost/releases/download/ccminer-x11gost_windows/ccminer_x11gost.7z");
			configMinerBeans.add(configMinerBean);

			configMinerBean = new ConfigMinerBean();
			alg = new HashSet<String>();
			// alg.add("bitcore");
			alg.add("blake2s");
			alg.add("blakecoin");
			// alg.add("vanilla");
			// alg.add("c11");
			// alg.add("cryptonight");
			// alg.add("decred");
			// alg.add("equihash");
			// alg.add("ethash");
			// alg.add("groestl");
			// alg.add("hmq1725");
			// alg.add("jha");
			alg.add("keccak");
			alg.add("lbry");
			alg.add("lyra2v2");
			// alg.add("lyra2z");
			alg.add("myr-gr");
			// alg.add("neoscrypt");
			alg.add("nist5");
			// alg.add("pascal");
			// alg.add("quark");
			// alg.add("qubit");
			// alg.add("scrypt");
			// alg.add("sia");
			alg.add("sib");
			alg.add("skein");
			// alg.add("skunk");
			// alg.add("timetravel");
			// alg.add("tribus");
			alg.add("veltor");
			// alg.add("x11");
			alg.add("x11evo");
			alg.add("x17");
			// alg.add("yescrypt");
			configMinerBean.setAlg(alg);
			configMinerBean.setAppMinerType("ccminer");
			configMinerBean.setMinerName("Palgin");
			configMinerBean.setProgame("ccminer.exe");
			configMinerBean.setUrlDownload("https://github.com/palginpav/ccminer/releases/download/1.1.1/palginmod_1.1_x64.zip");
			configMinerBeans.add(configMinerBean);

			configMinerDao.saveConfigMiner(configMinerBeans);
		}

		configPoolBeans = configMinerDao.loadConfigPool();
		if (configPoolBeans == null) {
			configPoolBeans = new ArrayList<ConfigPoolBean>();
			ConfigPoolBean configPoolBean = new ConfigPoolBean();

			configPoolBean.setPoolApi("http://www.zpool.ca/api/status");
			configPoolBean.setName("zpool");
			configPoolBean.setPoolType("yaamp");
			configPoolBean.setHostFormat("pool.data[alg].name+\"mine.zpool.ca\"");
			configPoolBean.setUserFormat("user.btcAddress");
			configPoolBean.setPortFormat("pool.data[alg].port");
			configPoolBean.setPasswordFormat("user.workerName+\",c=BTC\"");
			configPoolBeans.add(configPoolBean);

			configPoolBean = new ConfigPoolBean();

			configPoolBean.setPoolApi("http://pool.hashrefinery.com/api/status");
			configPoolBean.setName("hashrefinery");
			configPoolBean.setPoolType("yaamp");
			configPoolBean.setHostFormat("pool.data[alg].name+\"us.hashrefinery.com\"");
			configPoolBean.setUserFormat("user.btcAddress");
			configPoolBean.setPortFormat("pool.data[alg].port");
			configPoolBean.setPasswordFormat("user.workerName+\",c=BTC\"");
			configPoolBeans.add(configPoolBean);
			configMinerDao.saveConfigPool(configPoolBeans);
		}

		configUserBean = configMinerDao.loadConfigUser();

		if (configUserBean == null) {
			configUserBean = new ConfigUserBean();
			configUserBean.setBtcAddress("3B3uQUKrR4EHNpd56zkrkQFAWB6Erq3bTB");
			configUserBean.setUsernameMiningPoolHub("viatoro");
			configUserBean.setWorkername("worker");
			Set<String> type = new HashSet<>();
			type.add("nvida");
			configUserBean.setType(type);
			configMinerDao.saveConfigUser(configUserBean);
		}

		minerControlDao.setConfigMinerBeans(configMinerBeans);
		minerControlDao.setConfigUserBean(configUserBean);
		minerControlDao.setConfigPoolBeans(configPoolBeans);
		// List<IAppMiner> list = new LinkedList<IAppMiner>();
		//// list.add(new CcminerAppMiner());
		// allMiners.put("", list );
		// allPools.add(new YaampPool("http://www.zpool.ca/api/status"));

		if (logger.isDebugEnabled()) {
			logger.debug("loadConfig() - end"); //$NON-NLS-1$
		}
	}

}

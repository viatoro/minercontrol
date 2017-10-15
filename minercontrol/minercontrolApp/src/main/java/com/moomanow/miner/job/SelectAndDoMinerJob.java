package com.moomanow.miner.job;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.lang.reflect.Member;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
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

import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlException;

public class SelectAndDoMinerJob extends QuartzJobBean {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(SelectAndDoMinerJob.class.getName());

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

		// find non bench
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
		List<Process> processMining = minerControlDao.getProcessMining();
		RevenueBean revenueBean = revenueBeans.parallelStream().filter((rev) -> rev.getPrice() == null).sorted((rev1, rev2) -> {
			Integer rev1int = new Integer(rev1.hashCode());
			Integer rev2int = new Integer(rev2.hashCode());
			return rev1int.compareTo(rev2int);
		}).findFirst().orElse(null);

		// cal mining
		if (revenueBean == null) {
			RevenueBean revenueBeanCheck = revenueBeans.stream().filter((rev) -> !runing.containsKey(rev)).sorted((rev1, rev2) -> rev1.getPrice().compareTo(rev2.getPrice())).findFirst().orElse(null);
			if (revenueBeanCheck != null) {
				long s = runing.keySet().parallelStream().filter((rev) -> {
					return rev.getPrice().compareTo(revenueBeanCheck.getPrice()) == 1;
				}).count();
				if (s == 0) {
					revenueBean = revenueBeanCheck;
				}
			}

		}

		// case switch new miner
		if (revenueBean != null && !runing.containsKey(revenueBean)) {
			runing.entrySet().removeIf((revenueBeanEntry) -> {
				IAppMiner miner = revenueBeanEntry.getValue();
				miner.destroy();
				return true;
			});
			long numberPriceNull = runing.keySet().stream().filter((rev) -> {
				return rev.getPrice() == null;
			}).count();
			if (numberPriceNull == 0) {
				Map<String, Object> root = new ConcurrentHashMap<>();
				try {
					IPoolApi pool = revenueBean.getPool();
					IAppMiner appMiner = revenueBean.getMiner();
					root.put("pool", pool);
					root.put("user", configUserBean);
					root.put("alg", revenueBean.getAlg());
					Map context = Ognl.createDefaultContext(root, mem);
					String host = (String) Ognl.getValue(pool.getConfigPoolBean().getHostFormat(), context, root, String.class);
					String port = (String) Ognl.getValue(pool.getConfigPoolBean().getPortFormat(), context, root, String.class);
					String user = (String) Ognl.getValue(pool.getConfigPoolBean().getUserFormat(), context, root, String.class);
					String password = (String) Ognl.getValue(pool.getConfigPoolBean().getPasswordFormat(), context, root, String.class);

					processMining.add(appMiner.run(revenueBean.getAlg(), host, port, user, password));
					// appMiner.setBendIng(false);
					runing.put(revenueBean, appMiner);
				} catch (OgnlException e) {
					logger.error("executeInternal(JobExecutionContext)", e); //$NON-NLS-1$

					e.printStackTrace();
				}
			}

		}

		if (logger.isDebugEnabled()) {
			logger.debug("executeInternal(JobExecutionContext) - end"); //$NON-NLS-1$
		}
	}

	private MemberAccess mem = new MemberAccess() {

		@Override
		public Object setup(Map context, Object target, Member member, String propertyName) {
			return null;
		}

		@Override
		public void restore(Map context, Object target, Member member, String propertyName, Object state) {
		}

		@Override
		public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
			return true;
		}
	};

}

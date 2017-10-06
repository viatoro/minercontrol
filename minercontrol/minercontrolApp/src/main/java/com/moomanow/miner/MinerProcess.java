package com.moomanow.miner;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.function.Consumer;

import com.moomanow.miner.api.miner.IMinerReaderApi;
import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.api.pool.impl.YaampPool;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.appminer.impl.CcminerAppMiner;
import com.moomanow.miner.bean.CompareMiner;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.bean.RatePrice;
import com.moomanow.miner.bean.RevenueBean;
import com.moomanow.miner.config.ConfigMiner;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;
import com.moomanow.miner.config.impl.ConfigMinerJson;
import com.moomanow.miner.utiles.DownloadUtils;

import ognl.MemberAccess;
import ognl.Ognl;
import ognl.OgnlException;

public class MinerProcess {

	// private Map<String,List<IAppMiner>> allMiners = new
	// HashMap<String,List<IAppMiner>>();
	private Set<IAppMiner> allMiners = new HashSet<>();
	private List<IAppMiner> runing = new ArrayList<>();
	private Set<IPoolApi> allPools = new HashSet<>();
	private Set<String> listAlg;

	private Map<String, Class<? extends IPoolApi>> mapPoolApi = new HashMap<>();

	private Map<String, Class<? extends IAppMiner>> mapAppMiner = new HashMap<>();

	private Map<String, Set<IAppMiner>> mapAlgAppMiner = new HashMap<>();
	private Map<String, Set<IPoolApi>> mapAlgPoolApi = new HashMap<>();

	private List<ConfigMinerBean> configMinerBeans;
	private List<ConfigPoolBean> configPoolBeans;
	private ConfigUserBean configUserBean;
	private Set<RevenueBean> revenueBeans = new HashSet<>();
	private boolean bending;

	private ExecutorService executorPool, executorDownload, executorCheckHashRate;

	public MinerProcess() {
		executorPool = Executors.newFixedThreadPool(2);
		executorDownload = Executors.newFixedThreadPool(2);
		executorCheckHashRate = Executors.newFixedThreadPool(2);
		listAlg = new HashSet<>();
	}

	public void start() {

		mapPoolApi.put("yaamp", YaampPool.class);
		mapAppMiner.put("ccminer", CcminerAppMiner.class);
		loadConfig();

		for (ConfigPoolBean configPoolBean : configPoolBeans) {
			String poolType = configPoolBean.getPoolType();
			Class<? extends IPoolApi> classPoolApi = mapPoolApi.get(poolType);
			try {
				IPoolApi poolApi = classPoolApi.newInstance();
				poolApi.setConfigPoolBean(configPoolBean);
				allPools.add(poolApi);
				// listAlg.addAll(poolApi.getAlg());
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Callable<String> callable = new Callable<String>() {
		//
		// @Override
		// public String call() throws Exception {
		// // TODO Auto-generated method stub
		// return null;
		// }
		// };
		
		allPools.parallelStream().forEach((IPoolApi iPoolApi) -> {
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
		} );
//		for (IPoolApi iPoolApi : allPools) {
//
//			FutureTask<Boolean> futureTask = new FutureTask<>(() -> {
//				Boolean check = iPoolApi.checkRate();
//				listAlg.addAll(iPoolApi.getAlg());
//				for (String alg : iPoolApi.getAlg()) {
//					Set<IPoolApi> iPoolApis = mapAlgPoolApi.get(alg);
//					if (iPoolApis == null) {
//						iPoolApis = new HashSet<>();
//						mapAlgPoolApi.put(alg, iPoolApis);
//					}
//					iPoolApis.add(iPoolApi);
//				}
//				return check;
//			});
//			
//			executorPool.execute(futureTask);
//		}

//		futureTask.isDone();

		for (ConfigMinerBean configMinerBean : configMinerBeans) {
			String appMinerType = configMinerBean.getAppMinerType();

			Class<? extends IAppMiner> classIAppMiner = mapAppMiner.get(appMinerType);
			try {
				IAppMiner appMiner = classIAppMiner.newInstance();
				appMiner.setConfigMinerBean(configMinerBean);
				allMiners.add(appMiner);
				for (String alg : configMinerBean.getAlg()) {
					Set<IAppMiner> appMiners = mapAlgAppMiner.get(alg);
					if (appMiners == null) {
						appMiners = new HashSet<>();
						mapAlgAppMiner.put(alg, appMiners);
					}
					appMiners.add(appMiner);
				}

				// mapAlgAppMiner.put
			} catch (InstantiationException | IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		
		listAlg.stream().forEach((String alg) -> {
			Set<IAppMiner> appMiners = mapAlgAppMiner.get(alg);
			if(appMiners==null)
				return ;
			appMiners.parallelStream().filter((appMiner)->!appMiner.hasDownloaded()).forEach((appMiner)->{
				DownloadUtils.download(appMiner.getConfigMinerBean().getUrlDownload(), "./miner/" +appMiner.getConfigMinerBean().getMinerName());
			});
			if(appMiners.parallelStream().filter((appMiner)->appMiner.hasDownloaded()&&appMiner.getHashRate(alg)==null).count()>0) {
				bending = true;
				runing.stream().filter((miner)->!miner.isBendIng()).forEach((miner) ->{miner.destroy();} );
			}else {
				bending = false;
				runing.stream().filter((miner)->miner.isBendIng()).forEach((miner) ->{miner.destroy();} );
			}
			appMiners.stream().filter((appMiner)->appMiner.hasDownloaded()&&appMiner.getHashRate(alg)==null).forEach((appMiner)->{
				Set<IPoolApi> apis = mapAlgPoolApi.get(alg);
				if(apis==null)
					return ;
				apis.forEach((api)->{
					if(appMiner.getHashRate(alg)!=null)
						return;
					Map<String,Object> root = new HashMap<>();
					try {
						root.put("pool", api);
						root.put("user", configUserBean);
						root.put("alg", alg);
						Map context = Ognl.createDefaultContext(root, mem);
						String host = (String) Ognl.getValue(api.getConfigPoolBean().getHostFormat(), context,root,String.class);
						String port = (String) Ognl.getValue(api.getConfigPoolBean().getPortFormat(), context,root,String.class);
						String user = (String) Ognl.getValue(api.getConfigPoolBean().getUserFormat(), context,root,String.class);
						String password = (String) Ognl.getValue(api.getConfigPoolBean().getPasswordFormat(), context,root,String.class);
						
						appMiner.run(alg, host, port, user, password);
						appMiner.setBendIng(true);
						runing.add(appMiner);
//						appMiner.check();
					} catch (OgnlException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				});
				
				
			});
			
			
			
			
			appMiners.stream().filter((appMiner)->appMiner.hasDownloaded()&&appMiner.getHashRate(alg)!=null).forEach((appMiner)->{
				Set<IPoolApi> poolapis = mapAlgPoolApi.get(alg);
				if(poolapis==null)
					return ;
				poolapis.forEach((pool)->{
					HashRate hashRate = appMiner.getHashRate(alg);
					RatePrice price = pool.getMapRatePrices().get(alg);
					RevenueBean revenueBean = new RevenueBean();
					revenueBean.setAlg(alg);
					revenueBean.setPool(pool);
					revenueBean.setMiner(appMiner);
					revenueBean.setPrice(hashRate.getRate().multiply(price.getPrice()));
					revenueBeans.add(revenueBean );
				});
				
				
			});
			
			
		});
		
		if(!bending) {
			RevenueBean revenueTopBean = revenueBeans.stream().sorted((rev1,rev2)->rev1.getPrice().compareTo(rev2.getPrice())).findFirst().get();
			
			
			Map<String,Object> root = new HashMap<>();
			try {
				IPoolApi pool = revenueTopBean.getPool();
				IAppMiner appMiner = revenueTopBean.getMiner();
				root.put("pool", pool);
				root.put("user", configUserBean);
				root.put("alg", revenueTopBean.getAlg());
				Map context = Ognl.createDefaultContext(root, mem);
				String host = (String) Ognl.getValue(pool.getConfigPoolBean().getHostFormat(), context,root,String.class);
				String port = (String) Ognl.getValue(pool.getConfigPoolBean().getPortFormat(), context,root,String.class);
				String user = (String) Ognl.getValue(pool.getConfigPoolBean().getUserFormat(), context,root,String.class);
				String password = (String) Ognl.getValue(pool.getConfigPoolBean().getPasswordFormat(), context,root,String.class);
				
				appMiner.run(revenueTopBean.getAlg(), host, port, user, password);
				runing.add(appMiner);
			} catch (OgnlException e) {
				e.printStackTrace();
			}
		}

		
		
		runing.parallelStream().forEach((miner) -> {
			if(miner.isRun())
				miner.check();
		});
		// Object root = null;
		// MemberAccess securityMemberAccess = new
		// MemberAccess(allowStaticMethodAccess);
		// Map context = Ognl.createDefaultContext(root);
		// try {
		// Object s = Ognl.getValue("top", context, root, String.class);
		// } catch (OgnlException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// TODO Auto-generated method stub

		// check miner
		// for (Entry<String, List<IMiner>> allMiner : allMiners.entrySet()) {
		//
		// for (IMiner miner : allMiner.getValue()) {
		// if(!miner.hasBenched()){
		// miner.bench();
		// }
		// }
		//
		// }

		// Compar Price

		// for (IPool pool : allPools) {
		//
		//
		// List<RatePrice> ratePrices = pool.getRatePrices();
		// for (RatePrice ratePrice : ratePrices) {
		// List<IAppMiner> miners = allMiners.get(ratePrice.getAlg());
		//
		// miners.sort(compareHashRateMiner);
		// CompareMiner compareMiner = new CompareMiner(pool,miners.get(0));
		// compares.add(compareMiner);
		// }
		//
		//
		//// IMiner miner = allMiners.get(ratePrice.getAlg());
		//// ratePrice.calPrice(miner.getHashRate());
		// }
		//
//		List<MinerRun> minerRuns = new ArrayList<>();
//
//		for (MinerRun minerRun : minerRuns) {
//			IPoolApi pool = minerRun.getPool();
//			IAppMiner miner = minerRun.getMiner();
//			Map<String, Object> root = new HashMap<>();
//			root.put("pool", pool);
//			root.put("miner", miner);
//			root.put("alg", minerRun.getAlg());
//
//			Map context = Ognl.createDefaultContext(root, mem);
//			try {
//				Object s = Ognl.getValue("top", context, root, String.class);
//				System.out.println(s);
//			} catch (OgnlException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			// miner.run(host, port, user, password);
//		}
//
//		Map<String, Object> root = new HashMap<>();
//		root.put("top", "wefwefwe");
//
//		Map context = Ognl.createDefaultContext(root, mem);
//		try {
//			Object s = Ognl.getValue("top", context, root, String.class);
//			System.out.println(s);
//		} catch (OgnlException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

		// compares.sort(compareMiner);
		// compares.get(0);

		// check miner
		// for (IAppMiner miner : runing) {
		// if(miner.isRun()){
		// HashRate hashRate = miner.getHashRate();
		// }else{
		//
		// }
		// }

		// FutureTask<BigDecimal> futureTask = new FutureTask<>(callable);

		// executor.execute(futureTask);
	}

	private MemberAccess mem = new MemberAccess() {

		@Override
		public Object setup(Map context, Object target, Member member, String propertyName) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void restore(Map context, Object target, Member member, String propertyName, Object state) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean isAccessible(Map context, Object target, Member member, String propertyName) {
			// TODO Auto-generated method stub
			return true;
		}
	};

	private void loadConfig() {

		ConfigMiner configMiner = new ConfigMinerJson();

		configMinerBeans = configMiner.loadConfigMiner();
		if (configMinerBeans == null) {
			configMinerBeans = new ArrayList<ConfigMinerBean>();
			ConfigMinerBean configMinerBean = new ConfigMinerBean();
			Set<String> alg = new HashSet<String>();
			alg.add("neoscrypt");
			configMinerBean.setAlg(alg);
			configMinerBean.setAppMinerType("ccminer");
			configMinerBean.setMinerName("TPruvot");
			configMinerBean.setProgame("ccminer-X64.exe");
			configMinerBean.setUrlDownload("https://github.com/tpruvot/ccminer/releases/download/v2.2-tpruvot/ccminer-x64-2.2.7z");
			configMinerBeans.add(configMinerBean);
			configMiner.saveConfigMiner(configMinerBeans);
		}

		configPoolBeans = configMiner.loadConfigPool();
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
			configMiner.saveConfigPool(configPoolBeans);
		}
		
		configUserBean = configMiner.loadConfigUser();
		
		if(configUserBean==null) {
			configUserBean = new ConfigUserBean();
			configUserBean.setBtcAddress("3B3uQUKrR4EHNpd56zkrkQFAWB6Erq3bTB");
			configUserBean.setUsernameMiningPoolHub("viatoro");
			configUserBean.setWorkername("worker");
			Set<String> type = new HashSet<>();
			type.add("nvida");
			configUserBean.setType(type );
			configMiner.saveConfigUser(configUserBean);
		}
		
		
		// List<IAppMiner> list = new LinkedList<IAppMiner>();
		//// list.add(new CcminerAppMiner());
		// allMiners.put("", list );
		// allPools.add(new YaampPool("http://www.zpool.ca/api/status"));
	}

	// private Comparator<IAppMiner> compareHashRateMiner =new
	// Comparator<IAppMiner>() {
	//
	// public int compare(IAppMiner miner1, IAppMiner miner2) {
	// if(!miner1.hasBenched()){
	// miner1.bench();
	// }
	// if(!miner2.hasBenched()){
	// miner2.bench();
	// }
	//
	// return miner2.getHashRate().compareTo(miner2.getHashRate());
	// }
	// } ;
	// private Comparator<CompareMiner> compareMiner =new Comparator<CompareMiner>()
	// {
	//
	// public int compare(CompareMiner compareMiner1, CompareMiner compareMiner2) {
	// return compareMiner1.compareTo(compareMiner2);
	// }
	// } ;

}

package com.moomanow.miner;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
//import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.api.pool.impl.YaampPool;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.appminer.impl.CcminerAppMiner;
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

public class MinerProcess implements Runnable {

	// private Map<String,List<IAppMiner>> allMiners = new
	// HashMap<String,List<IAppMiner>>();
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

	private Long benTimeEnd;

	boolean stop = false;

	private ExecutorService execMain, executorPool, executorDownload, executorCheckHashRate;

	private Map<String,FutureTask<Boolean>> futureTaskPools= new ConcurrentHashMap<>();
	
	private Map<String,FutureTask<Boolean>> minerDoenloadIng = new ConcurrentHashMap<>();

	public MinerProcess() {
		execMain = Executors.newFixedThreadPool(2);
		executorPool = Executors.newFixedThreadPool(2);
		executorDownload = Executors.newFixedThreadPool(2);
		executorCheckHashRate = Executors.newFixedThreadPool(2);
//		futureTaskPools = new ArrayList<>();
//		listAlg = Collections.newSetFromMap(new ConcurrentHashMap<>());
		
		Thread Thread = new Thread(()->{ 
			try {
				java.lang.Thread.sleep(200);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
            System.out.println("Shouting down ...");
			runing.entrySet().parallelStream().forEach((miner) -> {
				try {
					miner.getValue().destroy();
				} catch (Exception e) {
					e.printStackTrace();
				}
			});
			
			processBenchmarking.parallelStream().forEach((process) ->{
				try {
					process.destroy();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
			processMining.parallelStream().forEach((process) ->{
				try {
					process.destroy();
				}catch (Exception e) {
					e.printStackTrace();
				}
			});
		}, "Shutdown-thread");
		Runtime.getRuntime().addShutdownHook(Thread );
	}

	public void start() {
		try {

			mapPoolApi.put("yaamp", YaampPool.class);
			mapAppMiner.put("ccminer", CcminerAppMiner.class);

			do {
				try {

					// load config

					loadConfig();

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
							e.printStackTrace();
						}

					}

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
						executorPool.execute(futureTaskPool);
					});

					// call data pool

					// download miner
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

					// download miner

					// main controler

					// cal
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

					// cal

					// main controler
					
//					find non bench
					RevenueBean revenueBean = revenueBeans.parallelStream().filter((rev)->rev.getPrice()==null).sorted().findFirst().get();
					
					
//					cal mining
					if (revenueBean==null) {
						RevenueBean revenueBeanCheck = revenueBeans.stream().filter((rev) -> !runing.containsKey(rev)).sorted((rev1, rev2) -> rev1.getPrice().compareTo(rev2.getPrice())).findFirst().get();
						long s = runing.keySet().parallelStream().filter((rev)->{
							return rev.getPrice().compareTo(revenueBeanCheck.getPrice())==1;
						}).count();
						if(s==0) {
							revenueBean = revenueBeanCheck;
						}
					}

//					case switch new miner
					if (revenueBean!=null&&!runing.containsKey(revenueBean)) {
						runing.entrySet().removeIf((revenueBeanEntry) -> {
							IAppMiner miner = revenueBeanEntry.getValue();
							miner.destroy();
							return true;
						});
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
							appMiner.setBendIng(false);
							runing.put(revenueBean, appMiner);
						} catch (OgnlException e) {
							e.printStackTrace();
						}
					}

					
					runing.entrySet().removeIf((revenueBeanEntry) -> {
						IAppMiner miner = revenueBeanEntry.getValue();
						RevenueBean revenueBeanCheck = revenueBeanEntry.getKey();
						if(revenueBeanCheck.getPrice()==null&&miner.getTimeStartLong()-Calendar.getInstance().getTimeInMillis()>miner.getConfigMinerBean().getTotalTimeBenchSec()*1000) {
							miner.stopBench();
							return true;
						}else {
							if (miner.isRun()) {
								miner.check();
							}
						}
						return false;
					});
					
					
					Thread.sleep(10000);

				} catch (Exception e) {
					e.printStackTrace();
				}

			} while (!stop);
		}catch (Exception e) {
			// TODO: handle exception
		} 
		finally {

//			runing.parallelStream().forEach((miner) -> {
//				try {
//					miner.destroy();
//				} catch (Exception e) {
//					// TODO: handle exception
//				}
//			});

		}
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

			configPoolBean = new ConfigPoolBean();

			configPoolBean.setPoolApi("http://pool.hashrefinery.com/api/status");
			configPoolBean.setName("hashrefinery");
			configPoolBean.setPoolType("yaamp");
			configPoolBean.setHostFormat("pool.data[alg].name+\"us.hashrefinery.com\"");
			configPoolBean.setUserFormat("user.btcAddress");
			configPoolBean.setPortFormat("pool.data[alg].port");
			configPoolBean.setPasswordFormat("user.workerName+\",c=BTC\"");
			configPoolBeans.add(configPoolBean);
			configMiner.saveConfigPool(configPoolBeans);
		}

		configUserBean = configMiner.loadConfigUser();

		if (configUserBean == null) {
			configUserBean = new ConfigUserBean();
			configUserBean.setBtcAddress("3B3uQUKrR4EHNpd56zkrkQFAWB6Erq3bTB");
			configUserBean.setUsernameMiningPoolHub("viatoro");
			configUserBean.setWorkername("worker");
			Set<String> type = new HashSet<>();
			type.add("nvida");
			configUserBean.setType(type);
			configMiner.saveConfigUser(configUserBean);
		}

		// List<IAppMiner> list = new LinkedList<IAppMiner>();
		//// list.add(new CcminerAppMiner());
		// allMiners.put("", list );
		// allPools.add(new YaampPool("http://www.zpool.ca/api/status"));
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		start();
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

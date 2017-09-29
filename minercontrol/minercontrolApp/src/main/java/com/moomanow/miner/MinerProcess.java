package com.moomanow.miner;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.appminer.impl.CcminerAppMiner;
import com.moomanow.miner.bean.CompareMiner;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.bean.RatePrice;
import com.moomanow.miner.pool.IPool;
import com.moomanow.miner.pool.impl.YaampPool;

public class MinerProcess {

	private Map<String,List<IAppMiner>> allMiners = new HashMap<String,List<IAppMiner>>();
//	private List<IMiner> allMiners = new ArrayList<IMiner>();
	private List<IAppMiner> runing = new ArrayList<IAppMiner>();
	private List<IPool> allPools = new ArrayList<IPool>();
	private List<CompareMiner> compares = new LinkedList<CompareMiner>();
	public void start() {
		load();
		// TODO Auto-generated method stub
		
		//check miner
//		for (Entry<String, List<IMiner>> allMiner : allMiners.entrySet()) {
//			
//			for (IMiner miner : allMiner.getValue()) {
//				if(!miner.hasBenched()){
//					miner.bench();
//				}
//			}
//			
//		}
		
		//Compar Price
		
		for (IPool pool : allPools) {
			
			
			List<RatePrice> ratePrices = pool.getRatePrices();
			for (RatePrice ratePrice : ratePrices) {
				List<IAppMiner> miners = allMiners.get(ratePrice.getAlg());
				
				miners.sort(compareHashRateMiner);
				CompareMiner compareMiner = new CompareMiner(pool,miners.get(0));
				compares.add(compareMiner);
			}
			
			
//			IMiner miner = allMiners.get(ratePrice.getAlg());
//			ratePrice.calPrice(miner.getHashRate());
		}
		
		

		compares.sort(compareMiner);
		compares.get(0);
		
		
		//check miner
		for (IAppMiner miner : runing) {
			if(miner.isRun()){
				HashRate hashRate = miner.getHashRate();
			}else{
				
			}
		}
	}
	
	
	private void load(){
		List<IAppMiner> list = new LinkedList<IAppMiner>();
		list.add(new CcminerAppMiner());
		allMiners.put("", list );
		allPools.add(new YaampPool("http://www.zpool.ca/api/status"));
	}
	
	private Comparator<IAppMiner> compareHashRateMiner =new Comparator<IAppMiner>() {
		
		public int compare(IAppMiner miner1, IAppMiner miner2) {
			if(!miner1.hasBenched()){
				miner1.bench();
			}
			if(!miner2.hasBenched()){
				miner2.bench();
			}
			
			return miner2.getHashRate().compareTo(miner2.getHashRate());
		}
	} ;
	private Comparator<CompareMiner> compareMiner =new Comparator<CompareMiner>() {
		
		public int compare(CompareMiner compareMiner1, CompareMiner compareMiner2) {
			return compareMiner1.compareTo(compareMiner2);
		}
	} ;

}

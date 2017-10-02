package com.moomanow.miner.dao;

import com.moomanow.miner.bean.HashRate;

public interface HashRateDao {
	
	public HashRate findHashRate(String minerName,String alg);
	
	public void saveHashRate(String minerName,String alg,HashRate hashRate);

}

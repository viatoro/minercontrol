package com.moomanow.miner.dao.impl;

import java.math.BigDecimal;

import org.junit.Test;

import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.dao.HashRateDao;

public class HashRateDaoImplTest {

	@Test
	public void test_method_1() {

		HashRateDao hashRateDao = new HashRateDaoImpl();
		HashRate hashRate =new HashRate();
		hashRate.setRate(new BigDecimal(299992929));
		hashRateDao.saveHashRate("Test", "alg",hashRate );
		
		HashRate hashRateCheck = hashRateDao.findHashRate("Test", "alg");
		
		System.out.println(hashRate.compareTo(hashRateCheck));
	}
}

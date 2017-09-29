package com.moomanow.miner.pool.impl;

import org.junit.Test;

import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.bean.RatePrice;

public class YaampPoolTestLive {

	
    @Test
    public void test_method_1() {
    	
    	YaampPool pool = new YaampPool("http://www.zpool.ca/api/status");
    	pool.checkRate();
    	for (RatePrice ratePrice : pool.getRatePrices()) {
    		System.out.println(ratePrice.getAlg());
    		
    		System.out.println(ratePrice.calPrice(new HashRate()));
		}
//        System.out.println("@Test - test_method_1");
    }

    @Test
    public void test_method_2() {
//        System.out.println("@Test - test_method_2");
    }
}

package com.moomanow.miner.appminer.impl;

import java.io.File;
import java.net.URISyntaxException;

import org.junit.Test;

import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.HashRate;

public class CcminerAppMinerTestLive {
	
    @Test
    public void test_method_1() {
    	String path = "./"; 
    	try {
    		path = new File(CcminerAppMinerTestLive.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()).getAbsolutePath();
			System.out.println(path);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	IAppMiner appMiner = new CcminerAppMiner(path+"/../../miner/NVIDIA-TPruvot/ccminer-X64.exe");
    	appMiner.run("neoscrypt", "neoscrypt.mine.zpool.ca", "4233", "3B3uQUKrR4EHNpd56zkrkQFAWB6Erq3bTB", "multipoolminer,c=BTC");
//    	do {
//    		appMiner.check();
//    		HashRate s = appMiner.getHashRate();
//    		System.out.println(s.getRate());
//    		try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		} while (true);
//        System.out.println("@Test - test_method_1");
    }
    @Test
    public void test_method_2() {
//        System.out.println("@Test - test_method_2");
    }

}

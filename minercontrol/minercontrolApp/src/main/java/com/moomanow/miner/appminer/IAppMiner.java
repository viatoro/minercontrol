package com.moomanow.miner.appminer;

import com.moomanow.miner.bean.HashRate;

public interface IAppMiner {

	public boolean isRun();
	public boolean run(String alg,String host,String port,String user,String password);
	
	public void check();

	public HashRate getHashRate();

	public boolean hasBenched();

	public void bench();

}

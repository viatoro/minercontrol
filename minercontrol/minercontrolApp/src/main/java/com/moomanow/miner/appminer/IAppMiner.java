package com.moomanow.miner.appminer;

import com.moomanow.miner.bean.HashRate;

public interface IAppMiner {

	public boolean isRun();
	public boolean run();

	public HashRate getHashRate();

	public boolean hasBenched();

	public void bench();

}

package com.moomanow.miner.appminer;

import java.io.InputStream;
import java.io.OutputStream;

import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.config.bean.ConfigMinerBean;

public interface IAppMiner {

	public void setConfigMinerBean(ConfigMinerBean configMinerBean);
	public void init();
	public boolean isRun();
	public HashRate getHashRate(String alg);
	public HashRate getHashRateBenchmarked(String alg);
	public void check();
	public Process run(String alg, String host, String port, String user, String password);
	public void destroy();
	public boolean hasDownloaded();
	public ConfigMinerBean getConfigMinerBean();
	public boolean isBendIng();
	public void setBendIng(boolean b);
	public boolean stopBench();
	public Long getTimeStartLong();
	public OutputStream getStdOut();
	public InputStream getStdIn();

}

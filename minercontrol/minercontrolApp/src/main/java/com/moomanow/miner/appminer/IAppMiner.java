package com.moomanow.miner.appminer;

import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.config.bean.ConfigMinerBean;

public interface IAppMiner {

	public void setConfigMinerBean(ConfigMinerBean configMinerBean);
	public void init();
	public boolean isRun();
	public HashRate getHashRate(String alg);
	public void check();
	public boolean run(String alg, String host, String port, String user, String password);
	public void destroy();
	public boolean hasDownloaded();
	public ConfigMinerBean getConfigMinerBean();
	public boolean isBendIng();
	public void setBendIng(boolean b);

}

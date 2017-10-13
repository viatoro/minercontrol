package com.moomanow.miner.config.dao;

import java.util.List;

import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;

public interface ConfigMinerDao {
	
	
	public List<ConfigMinerBean> loadConfigMiner();
	public List<ConfigPoolBean> loadConfigPool();
	public ConfigUserBean loadConfigUser();
	
	public void saveConfigMiner(List<ConfigMinerBean> configMinerBeans);
	public void saveConfigPool(List<ConfigPoolBean> configPoolBeans);
	public void saveConfigUser(ConfigUserBean configUserBean);

}

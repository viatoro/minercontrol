package com.moomanow.miner.api.pool;

import java.util.List;
import java.util.Map;

import com.moomanow.miner.bean.RatePrice;
import com.moomanow.miner.config.bean.ConfigPoolBean;

public interface IPoolApi {

	public boolean checkRate();
	public String getName();
	public List<RatePrice> getRatePrices();
	public void setConfigPoolBean(ConfigPoolBean configPoolBean);
	public ConfigPoolBean getConfigPoolBean();
	public List<String> getAlg();
	Object getData();
	void setData(Object data);
	Map<String, RatePrice> getMapRatePrices();

}

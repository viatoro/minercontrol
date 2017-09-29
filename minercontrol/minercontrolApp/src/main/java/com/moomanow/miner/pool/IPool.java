package com.moomanow.miner.pool;

import java.util.List;

import com.moomanow.miner.bean.RatePrice;

public interface IPool {

	public boolean checkRate();
	public List<RatePrice> getRatePrices();

}

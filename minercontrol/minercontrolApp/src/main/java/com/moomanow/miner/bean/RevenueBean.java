package com.moomanow.miner.bean;

import java.math.BigDecimal;

import com.moomanow.miner.api.pool.IPoolApi;
import com.moomanow.miner.appminer.IAppMiner;

public class RevenueBean {
	
	private IPoolApi pool;
	private String alg;
	private IAppMiner miner;
	private BigDecimal price;
	
	public String getAlg() {
		return alg;
	}
	public void setAlg(String alg) {
		this.alg = alg;
	}
	public BigDecimal getPrice() {
		return price;
	}
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	public IPoolApi getPool() {
		return pool;
	}
	public void setPool(IPoolApi pool) {
		this.pool = pool;
	}
	public IAppMiner getMiner() {
		return miner;
	}
	public void setMiner(IAppMiner miner) {
		this.miner = miner;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((alg == null) ? 0 : alg.hashCode());
		result = prime * result + ((miner == null||miner.getConfigMinerBean()==null||miner.getConfigMinerBean().getMinerName()==null) ? 0 : miner.getConfigMinerBean().getMinerName().hashCode());
		result = prime * result + ((pool == null||pool.getConfigPoolBean()==null||pool.getConfigPoolBean().getName()==null) ? 0 : pool.getConfigPoolBean().getName().hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RevenueBean other = (RevenueBean) obj;
		if (alg == null) {
			if (other.alg != null)
				return false;
		} else if (!alg.equals(other.alg))
			return false;
		if (miner == null||miner.getConfigMinerBean()==null||miner.getConfigMinerBean().getMinerName()==null) {
			if (!(other.miner == null||other.miner.getConfigMinerBean()==null||other.miner.getConfigMinerBean().getMinerName()==null))
				return false;
		} else if (!miner.getConfigMinerBean().getMinerName().equals(other.miner.getConfigMinerBean().getMinerName()))
			return false;
		if (pool == null||pool.getConfigPoolBean()==null||pool.getConfigPoolBean().getName()==null) {
			if (!(other.pool == null||other.pool.getConfigPoolBean()==null||other.pool.getConfigPoolBean().getName()==null))
				return false;
		} else if (!pool.getConfigPoolBean().getName().equals(other.pool.getConfigPoolBean().getName()))
			return false;
		return true;
	}
	
	

}

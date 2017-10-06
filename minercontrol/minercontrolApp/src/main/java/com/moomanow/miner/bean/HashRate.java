package com.moomanow.miner.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class HashRate implements Comparable<HashRate> ,Serializable{

	
	private BigDecimal rate = BigDecimal.ZERO;
	
	public int compareTo(HashRate hashRate) {
		return rate.compareTo(hashRate.getRate());
	}

	public BigDecimal getRate() {
		return rate;
	}
	
	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

}

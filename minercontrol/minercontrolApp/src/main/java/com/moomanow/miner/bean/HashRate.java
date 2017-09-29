package com.moomanow.miner.bean;

import java.math.BigDecimal;

public class HashRate implements Comparable<HashRate>{

	
	private BigDecimal rate = BigDecimal.ONE;
	
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

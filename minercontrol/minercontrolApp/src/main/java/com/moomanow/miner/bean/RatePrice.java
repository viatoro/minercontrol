package com.moomanow.miner.bean;

import java.math.BigDecimal;

public class RatePrice {
	
	private String alg;
	private BigDecimal price;

	
	public RatePrice(String alg) {
		this();
		this.alg =alg;
	}
	private RatePrice() {
	}
	
	public String getAlg() {
		return alg;
	}

	public BigDecimal calPrice(HashRate hashRate) {
		return price.multiply(hashRate.getRate());
		
	}

	public void setPrice(BigDecimal btcPerMh) {
		this.price = btcPerMh;
		
	}
	
	public BigDecimal getPrice() {
		return price;
	}

}

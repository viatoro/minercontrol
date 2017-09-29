package com.moomanow.miner.pool.impl;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moomanow.miner.bean.RatePrice;
import com.moomanow.miner.pool.IPool;

public class YaampPool implements IPool,Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4122260543096561071L;

	private HttpComponentsClientHttpRequestFactory httpFactory;
	
	private List<RatePrice> ratePrices = new LinkedList<RatePrice>();
	private Map<String, RatePrice> mapRatePrices = new HashMap<String, RatePrice>();
	private String url;
	
	
	public YaampPool(String url) {
		this();
		HttpClient httpClient = HttpClientBuilder.create().useSystemProperties().build();
		this.httpFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		this.url = url;
	}
	
	private YaampPool() {
	}
	
	public boolean checkRate(){
		RestTemplate restTemplate = new RestTemplate(httpFactory);
		String jsonString = restTemplate.getForObject(url, String.class);
		Gson gson = new GsonBuilder().create();
		
		
		Type type = new TypeToken<Map<String,RatePool>>(){}.getType();
		Map<String,RatePool> map = gson.fromJson(jsonString, type);
		for (Entry<String, RatePool> ratePoolEntry : map.entrySet()) {
			String key = ratePoolEntry.getKey();
			RatePool ratePool = ratePoolEntry.getValue();
			RatePrice ratePrice = mapRatePrices.get(key);
			if(ratePrice ==null){
				ratePrice = new RatePrice(key);
				ratePrices.add(ratePrice);
				mapRatePrices.put(key, ratePrice);
			}
			BigDecimal btcPerMh = new BigDecimal(ratePool.getEstimate_current()); 
			ratePrice.setPrice(btcPerMh);
			
		}
		return true;
	}

	public List<RatePrice> getRatePrices() {
		return ratePrices;
	}
	
	
	class RatePool implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = -1050613625500323613L;
		private String name;
		private String port;
		private String coins;
		private String fees;
		private String hashrate;
		private String workers;
		private String estimate_current;
		private String estimate_last24h;
		private String actual_last24h;
		private String hashrate_last24h;
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getPort() {
			return port;
		}
		public void setPort(String port) {
			this.port = port;
		}
		public String getCoins() {
			return coins;
		}
		public void setCoins(String coins) {
			this.coins = coins;
		}
		public String getFees() {
			return fees;
		}
		public void setFees(String fees) {
			this.fees = fees;
		}
		public String getHashrate() {
			return hashrate;
		}
		public void setHashrate(String hashrate) {
			this.hashrate = hashrate;
		}
		public String getWorkers() {
			return workers;
		}
		public void setWorkers(String workers) {
			this.workers = workers;
		}
		public String getEstimate_current() {
			return estimate_current;
		}
		public void setEstimate_current(String estimate_current) {
			this.estimate_current = estimate_current;
		}
		public String getEstimate_last24h() {
			return estimate_last24h;
		}
		public void setEstimate_last24h(String estimate_last24h) {
			this.estimate_last24h = estimate_last24h;
		}
		public String getActual_last24h() {
			return actual_last24h;
		}
		public void setActual_last24h(String actual_last24h) {
			this.actual_last24h = actual_last24h;
		}
		public String getHashrate_last24h() {
			return hashrate_last24h;
		}
		public void setHashrate_last24h(String hashrate_last24h) {
			this.hashrate_last24h = hashrate_last24h;
		}
		
	}

}

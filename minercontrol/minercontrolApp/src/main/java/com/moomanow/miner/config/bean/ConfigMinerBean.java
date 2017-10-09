package com.moomanow.miner.config.bean;

import java.io.Serializable;
import java.util.Set;

public class ConfigMinerBean implements Serializable{
	
	private String minerName;
	private String appMinerType;
	private Set<String> alg;
	private String progame;
	private String urlDownload;
	private Long totalTimeBenchSec;
	
	public String getMinerName() {
		return minerName;
	}
	public void setMinerName(String minerName) {
		this.minerName = minerName;
	}
	public Set<String> getAlg() {
		return alg;
	}
	public void setAlg(Set<String> alg) {
		this.alg = alg;
	}
	public String getProgame() {
		return progame;
	}
	public void setProgame(String progame) {
		this.progame = progame;
	}
	public String getUrlDownload() {
		return urlDownload;
	}
	public void setUrlDownload(String urlDownload) {
		this.urlDownload = urlDownload;
	}
	public String getAppMinerType() {
		return appMinerType;
	}
	public void setAppMinerType(String appMinerType) {
		this.appMinerType = appMinerType;
	}
	public Long getTotalTimeBenchSec() {
		return totalTimeBenchSec;
	}
	public void setTotalTimeBenchSec(Long totalTimeBenchSec) {
		this.totalTimeBenchSec = totalTimeBenchSec;
	}
	
	
	
	
	
	
	
	

}

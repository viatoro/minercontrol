package com.moomanow.miner.config.bean;

import java.io.Serializable;
import java.util.Set;

public class ConfigUserBean implements Serializable {
	
	private String btcAddress;
	private String usernameMiningPoolHub;
	private String workername;
	private Set<String> type;
	public String getBtcAddress() {
		return btcAddress;
	}
	public void setBtcAddress(String btcAddress) {
		this.btcAddress = btcAddress;
	}
	public String getUsernameMiningPoolHub() {
		return usernameMiningPoolHub;
	}
	public void setUsernameMiningPoolHub(String usernameMiningPoolHub) {
		this.usernameMiningPoolHub = usernameMiningPoolHub;
	}
	public String getWorkername() {
		return workername;
	}
	public void setWorkername(String workername) {
		this.workername = workername;
	}
	public Set<String> getType() {
		return type;
	}
	public void setType(Set<String> type) {
		this.type = type;
	}

	
	
}

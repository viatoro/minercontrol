package com.moomanow.miner.config.bean;

import java.io.Serializable;
import java.util.Set;

public class ConfigPoolBean implements Serializable{
	
	private String poolApi;
	private String name;
	private String poolType;
	private String userFormat;
	private String passwordFormat;
	private String hostFormat;
	private String portFormat;
	
	public String getPoolApi() {
		return poolApi;
	}
	public void setPoolApi(String poolApi) {
		this.poolApi = poolApi;
	}
	public String getUserFormat() {
		return userFormat;
	}
	public void setUserFormat(String userFormat) {
		this.userFormat = userFormat;
	}
	public String getPasswordFormat() {
		return passwordFormat;
	}
	public void setPasswordFormat(String passwordFormat) {
		this.passwordFormat = passwordFormat;
	}
	public String getHostFormat() {
		return hostFormat;
	}
	public void setHostFormat(String hostFormat) {
		this.hostFormat = hostFormat;
	}
	public String getPortFormat() {
		return portFormat;
	}
	public void setPortFormat(String portFormat) {
		this.portFormat = portFormat;
	}
	public String getPoolType() {
		return poolType;
	}
	public void setPoolType(String poolType) {
		this.poolType = poolType;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
	
}

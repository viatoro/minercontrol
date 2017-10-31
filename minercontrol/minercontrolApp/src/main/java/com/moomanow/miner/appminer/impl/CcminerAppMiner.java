package com.moomanow.miner.appminer.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.SeekableByteChannel;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.moomanow.miner.api.miner.IMinerReaderApi;
import com.moomanow.miner.api.miner.impl.CcminerApiReader;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.dao.HashRateDao;
import com.moomanow.miner.dao.impl.HashRateDaoImpl;

public class CcminerAppMiner implements IAppMiner {

	private Process process;
	private String command;
	private Map<String, HashRate> mapHashRateRealTime = new HashMap<>();
	private HashRateDao hashRateDao = new HashRateDaoImpl();
	private ConfigMinerBean configMinerBean;
	private String alg;
	private boolean bendIng= false;
	private OutputStream stdOut;
	private InputStream stdIn;
	
	private Long timeStartLong =0L;

	public CcminerAppMiner() {
	}

	@Override
	public void init() {
//		mapHashRate = new HashMap<>();
//		hashRateDao = new HashRateDaoImpl();
	}
	@Override
	public boolean hasDownloaded() {
		File progameFile = new File("./miner/" + configMinerBean.getMinerName()+"/"+configMinerBean.getProgame());
		return progameFile.exists();
		
	}

	@Override
	public boolean isRun() {
		if (process != null)
			return process.isAlive();
		return false;
	}

	@Override
	public HashRate getHashRate(String alg) {
		return mapHashRateRealTime.get(alg);
	}

	@Override
	public void check() {
		IMinerReaderApi apiReader = new CcminerApiReader("localhost", 4068);
		BigDecimal rate = apiReader.check();
		if(rate==null)
			return;
		HashRate hashRate = mapHashRateRealTime.get(alg);
		if(hashRate == null) {
			hashRate = new HashRate();
			mapHashRateRealTime.put(alg, hashRate);
		}
		hashRate .setRate(rate);
//		hashRateDao.saveHashRate(configMinerBean.getMinerName(), alg, hashRate);
	}
	
	
	@Override
	public boolean stopBench() {
		check();
		HashRate hashRate = mapHashRateRealTime.get(alg);
		hashRateDao.saveHashRate(configMinerBean.getMinerName(), alg, hashRate);
		destroy();
		return true;
	}

	public void bench() {

	}
	
	@Override
	public Process run(String alg,String host, String port, String user, String password) {
		Runtime runTime = Runtime.getRuntime();

		try {
			this.alg = alg;
			command = "-a " + alg + " -o stratum+tcp://" + host + ":" + port + " -u " + user + " -p " + password;
			
			System.out.println(command);
			process = runTime.exec("./miner/" + configMinerBean.getMinerName()+"/"+configMinerBean.getProgame() + " " + command);
			timeStartLong = Calendar.getInstance().getTimeInMillis();

			stdIn = process.getInputStream();
			// process.destroy();
			return process;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public void destroy() {
		process.destroy();
	}

	@Override
	public void setConfigMinerBean(ConfigMinerBean configMinerBean) {
		this.configMinerBean = configMinerBean;
	}

	@Override
	public ConfigMinerBean getConfigMinerBean() {
		return configMinerBean;
	}

	@Override
	public boolean isBendIng() {
		return bendIng;
	}

	@Override
	public void setBendIng(boolean bendIng) {
		this.bendIng = bendIng;
	}

	@Override
	public HashRate getHashRateBenchmarked(String alg) {
		return hashRateDao.findHashRate(configMinerBean.getMinerName(), alg);
	}
	@Override
	public Long getTimeStartLong() {
		return timeStartLong;
	}

	@Override
	@JsonIgnore
	public OutputStream getStdOut() {
		return stdOut;
	}

	@Override
	@JsonIgnore
	public InputStream getStdIn() {
		return stdIn;
	}

	@Override
	public String getName() {
		return configMinerBean!=null?configMinerBean.getMinerName():"CCminerAppMiner";
	}
	
	

}

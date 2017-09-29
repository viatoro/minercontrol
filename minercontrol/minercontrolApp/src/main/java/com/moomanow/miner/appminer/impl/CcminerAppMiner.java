package com.moomanow.miner.appminer.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.HashRate;

public class CcminerAppMiner implements IAppMiner {
	
	private Process process;
	private String command;
	private String urlDownload;
	private HashRate hashRate;
	
	public CcminerAppMiner(){
		hashRate = new HashRate();
	}
	
	public CcminerAppMiner(String command){
		this.command =command;
	}
	
	public boolean isRun() {
		if(process!=null)
			return process.isAlive();
		return false;
	}

	public HashRate getHashRate() {
		return hashRate;
	}
	private BigDecimal oneK = new BigDecimal(1000);
	public void check(){
		try {
			Socket socket = new Socket("localhost", 4068);
			DataInputStream din = new DataInputStream(socket.getInputStream());
			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

			String str = "summary", str2 = "";
			dout.writeUTF(str);
			dout.flush();
			str2 = din.readUTF();
			
			Gson gson = new GsonBuilder().create();
			
			
			Type type = new TypeToken<Map<String,String>>(){}.getType();
			Map<String,String> map = gson.fromJson(str2, type);
			String strKHS = map.get("KHS");
			String strACC = map.get("ACC");
			BigDecimal bigDecimalKHS = new BigDecimal(strKHS);
			BigDecimal bigDecimalACC = new BigDecimal(strACC);
			BigDecimal rate = bigDecimalKHS.multiply(oneK);
			hashRate.setRate(rate);
			dout.close();
			socket.close();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public boolean hasBenched() {
		return false;
	}

	public void bench() {

	}

	public boolean run() {
		Runtime runTime = Runtime.getRuntime();
		
		try {
			Process process = runTime.exec(command);
			return process.isAlive();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}

package com.moomanow.miner.appminer.impl;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
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
	private String path;
	private HashRate hashRate;
	
	public CcminerAppMiner(){
		hashRate = new HashRate();
	}
	
	public CcminerAppMiner(String path){
		this();
		this.path =path;
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
			
			InputStream is = socket.getInputStream();
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
//			DataInputStream din = new DataInputStream(socket.getInputStream());
//			DataOutputStream dout = new DataOutputStream(socket.getOutputStream());
			pw.println("summary");
		    pw.flush();
		    StringBuilder stringBuilder = new StringBuilder();
//			String str = "summary", str2 = "";
//			dout.writeUTF(str);
//			dout.flush();
			byte[] buffer = new byte[1024];
		    int read;
		    while((read = is.read(buffer)) != -1) {
		        String output = new String(buffer, 0, read);
		        stringBuilder.append(output);
//		        System.out.print(output);
//		        System.out.flush();
		    };
//			str2 = din.readUTF();
//			String userInput;
//			while ((userInput = stdIn.readLine()) != null) {
//			    out.println(userInput);
//			    System.out.println("echo: " + in.readLine());
//			}
			


			Map<String,String> map = new HashMap<String, String>();
			for (String para : stringBuilder.toString().split(";")) {
				String[] paraAr = para.split("=");map.put(paraAr[0], paraAr[1]);
			}
//			Gson gson = new GsonBuilder().create();
//			Type type = new TypeToken<Map<String,String>>(){}.getType();
//			Map<String,String> map = gson.fromJson(, type);
			String strKHS = map.get("KHS");
			String strACC = map.get("ACC");
			BigDecimal bigDecimalKHS = new BigDecimal(strKHS);
			BigDecimal bigDecimalACC = new BigDecimal(strACC);
			BigDecimal rate = bigDecimalKHS.multiply(oneK);
			hashRate.setRate(rate);
//			dout.close();
			socket.close();
		} catch (ConnectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch ( UnknownHostException e) {
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

	public boolean run(String alg,String host,String port,String user,String password) {
		Runtime runTime = Runtime.getRuntime();
		
		try {
			command = "-a "
					+ alg
					+ " -o stratum+tcp://"
					+ host
					+ ":"
					+ port
					+ " -u "
					+ user
					+ " -p "
					+ password
					;
			
			Process process = runTime.exec(path+" "+command);
//			process.destroy();
			return process.isAlive();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void destroy() {
		process.destroy();
	}
	

}

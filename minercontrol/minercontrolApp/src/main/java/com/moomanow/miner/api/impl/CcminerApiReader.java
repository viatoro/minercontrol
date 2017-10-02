package com.moomanow.miner.api.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import com.moomanow.miner.api.ApiReader;

public class CcminerApiReader implements ApiReader {
	
	private String host;
	private int port;
	
	public CcminerApiReader() {
	}
	
	public CcminerApiReader(String host,int port) {
		this();
		this.host = host;
		this.port = port;
	}
	
	private BigDecimal oneK = new BigDecimal(1000);
	public BigDecimal check() {
		try {
			Socket socket = new Socket(host, port);
//			Socket socket = new Socket("localhost", 4068);
			
			InputStream is = socket.getInputStream();
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			pw.println("summary");
		    pw.flush();
		    StringBuilder stringBuilder = new StringBuilder();
			byte[] buffer = new byte[1024];
		    int read;
		    while((read = is.read(buffer)) != -1) {
		        String output = new String(buffer, 0, read);
		        stringBuilder.append(output);
		    };
			


			Map<String,String> map = new HashMap<String, String>();
			for (String para : stringBuilder.toString().split(";")) {
				String[] paraAr = para.split("=");map.put(paraAr[0], paraAr[1]);
			}
			String strKHS = map.get("KHS");
			String strACC = map.get("ACC");
			BigDecimal bigDecimalKHS = new BigDecimal(strKHS);
			BigDecimal bigDecimalACC = new BigDecimal(strACC);
			BigDecimal rate = bigDecimalKHS.multiply(oneK);
			
			socket.close();
			return rate;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// TODO Auto-generated method stub
		return null;
	}

}

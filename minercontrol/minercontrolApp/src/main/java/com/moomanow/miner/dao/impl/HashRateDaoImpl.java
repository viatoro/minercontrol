package com.moomanow.miner.dao.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.dao.HashRateDao;

public class HashRateDaoImpl implements HashRateDao {
	
	public HashRate findHashRate(String minerName,String alg) {
		try {
			String fileHasRateStr = "./stats/"+minerName+"_"+alg+".json";
			Reader reader;
			File fileHasRate = new File(fileHasRateStr);
			if(!fileHasRate.exists())
				return null;
			reader = new FileReader(fileHasRate );
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<HashRate>(){}.getType();
			HashRate hashRate = gson.fromJson(reader, type);
			return hashRate;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	public void saveHashRate(String minerName,String alg,HashRate hashRate) {
		try {
//			String fileHasRate = "stats/"+minerName+"_"+alg+".json";
			File fileHasRate = new File("./stats/", minerName+"_"+alg+".json");
			fileHasRate.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(fileHasRate );
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<HashRate>(){}.getType();
			
			System.out.println(gson.toJson(hashRate));
			gson.toJson(hashRate, writer);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

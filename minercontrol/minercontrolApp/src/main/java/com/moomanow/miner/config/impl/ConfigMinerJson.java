package com.moomanow.miner.config.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.List;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.config.ConfigMiner;
import com.moomanow.miner.config.bean.ConfigMinerBean;
import com.moomanow.miner.config.bean.ConfigPoolBean;
import com.moomanow.miner.config.bean.ConfigUserBean;

public class ConfigMinerJson implements ConfigMiner {

	@Override
	public List<ConfigMinerBean> loadConfigMiner() {
		try {
			String fileStr = "./config/configMiner.json";
			Reader reader;
			File file = new File(fileStr);
			if(!file.exists())
				return null;
			reader = new FileReader(file);
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<ConfigMinerBean>>(){}.getType();
			List<ConfigMinerBean> config = gson.fromJson(reader, type);
			return config;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public List<ConfigPoolBean> loadConfigPool() {
		try {
			String fileStr = "./config/configPool.json";
			Reader reader;
			File file = new File(fileStr);
			if(!file.exists())
				return null;
			reader = new FileReader(file);
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<ConfigPoolBean>>(){}.getType();
			List<ConfigPoolBean> config = gson.fromJson(reader, type);
			return config;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	

	@Override
	public ConfigUserBean loadConfigUser() {
		try {
			String fileStr = "./config/configUser.json";
			Reader reader;
			File file = new File(fileStr);
			if(!file.exists())
				return null;
			reader = new FileReader(file);
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<ConfigUserBean>(){}.getType();
			ConfigUserBean config = gson.fromJson(reader, type);
			return config;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void saveConfigMiner(List<ConfigMinerBean> configMinerBeans) {
		try {
			File file = new File("./config/configMiner.json");
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file );
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<ConfigMinerBean>>(){}.getType();
			
//			System.out.println(gson.toJson(configMinerBeans));
			gson.toJson(configMinerBeans, writer);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void saveConfigPool(List<ConfigPoolBean> configPoolBeans) {
		// TODO Auto-generated method stub
		try {
			File file = new File("./config/configPool.json");
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file );
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<List<ConfigPoolBean>>(){}.getType();
			
			gson.toJson(configPoolBeans, writer);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	@Override
	public void saveConfigUser(ConfigUserBean configUserBean) {
		// TODO Auto-generated method stub
		try {
			File file = new File("./config/configUser.json");
			file.getParentFile().mkdirs();
			FileWriter writer = new FileWriter(file );
		    Gson gson = new GsonBuilder().create();
			Type type = new TypeToken<ConfigUserBean>(){}.getType();
			
			gson.toJson(configUserBean, writer);
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

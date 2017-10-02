package com.moomanow.miner.appminer.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.SeekableByteChannel;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

import com.moomanow.miner.api.ApiReader;
import com.moomanow.miner.api.impl.CcminerApiReader;
import com.moomanow.miner.appminer.IAppMiner;
import com.moomanow.miner.bean.HashRate;
import com.moomanow.miner.dao.HashRateDao;
import com.moomanow.miner.dao.impl.HashRateDaoImpl;

public class CcminerAppMiner implements IAppMiner {

	private Process process;
	private String command;
	private String progame;
	private String urlDownload;
	private String path;
	private HashRate hashRate;
	private HashRateDao hashRateDao = new HashRateDaoImpl();
	private String minerName;
	private String alg;

	private void load() {
		hashRate = hashRateDao.findHashRate(minerName, alg);
	}

	private CcminerAppMiner() {
		// TODO Auto-generated constructor stub
	}

	public CcminerAppMiner(String minerName, String alg, String progame, String urlDownload) {
		this();
		this.urlDownload = urlDownload;
		this.minerName = minerName;
		this.alg = alg;
		this.progame = progame;
		
		File progameFile = new File("./miner/" + minerName+"/"+progame);
		if(!progameFile.exists())
			download();
		
		load();
	}

	public void download() {
		try {
			URL website = new URL(urlDownload);

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = null;
			try {
				is = website.openStream();
				byte[] byteChunk = new byte[4096]; // Or whatever size you want to read in at a time.
				int n;
				URLConnection conn = website.openConnection();
				int size = conn.getContentLength();
				double sumCount = 0.0;

				while ((n = is.read(byteChunk)) > 0) {
					baos.write(byteChunk, 0, n);
					sumCount += n;
					if (size > 0) {
						System.out.println("Percentace: " + (sumCount / size * 100.0) + "%");
					}
				}
			} catch (IOException e) {
				System.err.printf("Failed while reading bytes from %s: %s", website.toExternalForm(), e.getMessage());
				e.printStackTrace();
				// Perform any other exception handling that's appropriate.
			} finally {
				if (is != null) {
					is.close();
				}
			}

			SeekableByteChannel seekableByteChannel = new SeekableInMemoryByteChannel(baos.toByteArray());

			File outputDir = new File("./miner/" + minerName);
			
			unSevenZipToDir(seekableByteChannel, outputDir);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void unSevenZipToDir(SeekableByteChannel seekableByteChannel, File outputDir) {
		// Make sure output dir exists
		outputDir.mkdirs();
		if (outputDir.exists()) {
			// FileInputStream stream;
			try {
				FileOutputStream output = null;
				SevenZFile f7z = new SevenZFile(seekableByteChannel);
				SevenZArchiveEntry entry;
				long maxSize = 0;
				while ((entry = f7z.getNextEntry()) != null) {
					if (entry != null) {
						String s = entry.getName();
						if (s != null) {
							long sz = entry.getSize();
							if (sz > 0) {
								int count;
								byte data[] = new byte[4096];
								String outFileName = outputDir.getPath() + "/" + new File(entry.getName()).getName();
								File outFile = new File(outFileName);
								// Extract only if it does not already exist
								if (outFile.exists() == false) {
									System.out.println("Extracting " + s + " => size = " + sz);
									FileOutputStream fos = new FileOutputStream(outFile);
									BufferedOutputStream dest = new BufferedOutputStream(fos);
									while ((count = f7z.read(data)) != -1) {
										dest.write(data, 0, count);
									}
									dest.flush();
									dest.close();
								} else {
									System.out.println("Using already Extracted " + s + " => size = " + sz);
								}
							} // end sz > 0
						} // end s != null
					} // end if entry
				} // end while
				f7z.close();
			} catch (FileNotFoundException e) { 
				e.printStackTrace();

			} catch (IOException e) { 
				e.printStackTrace();
			}
		}
	}


	public boolean isRun() {
		if (process != null)
			return process.isAlive();
		return false;
	}

	public HashRate getHashRate() {
		return hashRate;
	}

	// private BigDecimal oneK = new BigDecimal(1000);
	public void check() {
		ApiReader apiReader = new CcminerApiReader("localhost", 4068);
		BigDecimal rate = apiReader.check();
		hashRate.setRate(rate);
		hashRateDao.saveHashRate(minerName, alg, hashRate);
	}

	public boolean hasBenched() {
		return false;
	}

	public void bench() {

	}

	public boolean run(String host, String port, String user, String password) {
		Runtime runTime = Runtime.getRuntime();

		try {
			command = "-a " + alg + " -o stratum+tcp://" + host + ":" + port + " -u " + user + " -p " + password;

			Process process = runTime.exec("./miner/" + minerName+"/"+progame + " " + command);
			// process.destroy();
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

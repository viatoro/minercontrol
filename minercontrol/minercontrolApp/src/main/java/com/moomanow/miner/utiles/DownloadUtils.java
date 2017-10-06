package com.moomanow.miner.utiles;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.SeekableByteChannel;

import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

public class DownloadUtils {

	
	public static void download(String url,String pathOut) {
		try {
			URL website = new URL(url);

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

			File outputDir = new File(pathOut);
			
			unSevenZipToDir(seekableByteChannel, outputDir);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private static void unSevenZipToDir(SeekableByteChannel seekableByteChannel, File outputDir) {
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
}

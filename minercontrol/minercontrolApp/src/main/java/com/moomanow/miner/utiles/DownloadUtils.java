package com.moomanow.miner.utiles;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
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

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import org.apache.commons.compress.archivers.sevenz.SevenZFile;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;

public class DownloadUtils {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = LogManager.getLogger(DownloadUtils.class.getName());

	
	public static void download(String url,String pathOut) {
		if (logger.isDebugEnabled()) {
			logger.debug("download(String, String) - start"); //$NON-NLS-1$
		}

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
						if (logger.isDebugEnabled()) {
							logger.debug("download(String, String) - {}", "Percentace: " + (sumCount / size * 100.0) + "%"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
						}
					}
				}
			} catch (IOException e) {
				logger.error("download(String, String)", e); //$NON-NLS-1$

//				System.err.printf("Failed while reading bytes from %s: %s", website.toExternalForm(), e.getMessage());
//				e.printStackTrace();
				// Perform any other exception handling that's appropriate.
			} finally {
				if (is != null) {
					is.close();
				}
			}

			

			File outputDir = new File(pathOut);
			try {
				archiveToDir(baos.toByteArray(), outputDir);
			}catch (Exception e) {
				sevenZToDir(baos.toByteArray(), outputDir);
			}
			
		} catch (MalformedURLException e) {
			logger.error("download(String, String)", e); //$NON-NLS-1$
		} catch (IOException e) {
			logger.error("download(String, String)", e); //$NON-NLS-1$
		}

		if (logger.isDebugEnabled()) {
			logger.debug("download(String, String) - end"); //$NON-NLS-1$
		}
	}
	private static void sevenZToDir(byte[] bs, File outputDir) {
		if (logger.isDebugEnabled()) {
			logger.debug("archiveToDir(byte[], File) - start"); //$NON-NLS-1$
		}

		// Make sure output dir exists
		outputDir.mkdirs();
		if (outputDir.exists()) {
			// FileInputStream stream;
			try {
				SeekableByteChannel seekableByteChannel = new SeekableInMemoryByteChannel(bs);
//				ByteArrayInputStream bis = new ByteArrayInputStream(bs);
//				ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bis);
//				input.getNextEntry()
				FileOutputStream output = null;
				SevenZFile input = new SevenZFile(seekableByteChannel);
//				ZipFile zipFile = new ZipFile(seekableByteChannel);
				ArchiveEntry entry;
				long maxSize = 0;
				while ((entry = input.getNextEntry()) != null) {
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
									if (logger.isDebugEnabled()) {
										logger.debug("archiveToDir(byte[], File) - {}", "Extracting " + s + " => size = " + sz); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									}
									FileOutputStream fos = new FileOutputStream(outFile);
									BufferedOutputStream dest = new BufferedOutputStream(fos);
									while ((count = input.read(data)) != -1) {
										dest.write(data, 0, count);
									}
									dest.flush();
									dest.close();
								} else {
									if (logger.isDebugEnabled()) {
										logger.debug("archiveToDir(byte[], File) - {}", "Using already Extracted " + s + " => size = " + sz); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									}
								}
							} // end sz > 0
						} // end s != null
					} // end if entry
				} // end while
				input.close();
			} catch (FileNotFoundException e) {
				logger.error("archiveToDir(byte[], File)", e); //$NON-NLS-1$
			} catch (IOException e) {
				logger.error("archiveToDir(byte[], File)", e); //$NON-NLS-1$
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("archiveToDir(byte[], File) - end"); //$NON-NLS-1$
		}
	}
	private static void archiveToDir(byte[] bs, File outputDir) {
		if (logger.isDebugEnabled()) {
			logger.debug("archiveToDir(byte[], File) - start"); //$NON-NLS-1$
		}

		// Make sure output dir exists
		outputDir.mkdirs();
		if (outputDir.exists()) {
			// FileInputStream stream;
			try {
//				SeekableByteChannel seekableByteChannel = new SeekableInMemoryByteChannel(bs);
				ByteArrayInputStream bis = new ByteArrayInputStream(bs);
				ArchiveInputStream input = new ArchiveStreamFactory().createArchiveInputStream(bis);
//				input.getNextEntry()
				FileOutputStream output = null;
//				SevenZFile f7z = new SevenZFile(seekableByteChannel);
//				ZipFile zipFile = new ZipFile(seekableByteChannel);
				ArchiveEntry entry;
				long maxSize = 0;
				while ((entry = input.getNextEntry()) != null) {
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
									if (logger.isDebugEnabled()) {
										logger.debug("archiveToDir(byte[], File) - {}", "Extracting " + s + " => size = " + sz); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									}
									FileOutputStream fos = new FileOutputStream(outFile);
									BufferedOutputStream dest = new BufferedOutputStream(fos);
									while ((count = input.read(data)) != -1) {
										dest.write(data, 0, count);
									}
									dest.flush();
									dest.close();
								} else {
									if (logger.isDebugEnabled()) {
										logger.debug("archiveToDir(byte[], File) - {}", "Using already Extracted " + s + " => size = " + sz); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									}
								}
							} // end sz > 0
						} // end s != null
					} // end if entry
				} // end while
				input.close();
			} catch (FileNotFoundException e) {
				logger.error("archiveToDir(byte[], File)", e); //$NON-NLS-1$
			} catch (IOException e) {
				logger.error("archiveToDir(byte[], File)", e); //$NON-NLS-1$
			} catch (ArchiveException e) {
				logger.error("archiveToDir(byte[], File)", e); //$NON-NLS-1$
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("archiveToDir(byte[], File) - end"); //$NON-NLS-1$
		}
	}
}

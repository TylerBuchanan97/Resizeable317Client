package com.jagex;

import java.applet.Applet;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

public final class SignLink implements Runnable {

	public static RandomAccessFile cache = null;
	public static String dns = null;
	public static String error = "";
	public static RandomAccessFile[] indices = new RandomAccessFile[5];
	public static Applet applet = null;
	public static String midi = null;
	public static int midiFade;
	public static int midiVolume;
	public static boolean reportError = true;
	public static int storeId = 32;
	public static boolean sunJava;
	public static int uid;
	public static String wave = null;
	public static int waveVolume;
	private static boolean active;
	private static String dnsRequest = null;
	private static boolean midiPlay;
	private static int midiPos;
	private static byte[] saveBuffer = null;
	private static int saveLength;
	private static String saveRequest = null;
	private static Socket socket = null;
	private static InetAddress socketAddress;
	private static int socketRequest;
	private static int threadLiveId;
	private static Runnable threadRequest = null;
	private static int threadRequestPriority = 1;
	private static String urlRequest = null;
	private static DataInputStream urlStream = null;
	private static boolean wavePlay;
	private static int wavePosition;

	public static final synchronized void dnsLookup(String request) {
		dns = request;
		dnsRequest = request;
	}

	public static final String findCacheDirectory() {
		String[] directories = { "c:/windows/", "c:/winnt/", "d:/windows/", "d:/winnt/", "e:/windows/", "e:/winnt/",
				"f:/windows/", "f:/winnt/", "c:/", "~/", "/tmp/", "", "c:/rscache", "/rscache" };
		if (storeId < 32 || storeId > 34) {
			storeId = 32;
		}

		String sub = ".file_store_" + storeId;
		for (String directory : directories) {
			try {
				if (directory.length() > 0) {
					File file = new File(directory);
					if (!file.exists()) {
						continue;
					}
				}
				File file1 = new File(directory + sub);
				if (file1.exists() || file1.mkdir()) {
					return directory + sub + "/";
				}
			} catch (Exception ex) {
			}
		}

		return null;
	}

	public static final synchronized void midiSave(byte[] buffer, int length) {
		if (length > 0x1e8480) {
			return;
		} else if (saveRequest != null) {
			return;
		}

		midiPos = (midiPos + 1) % 5;
		saveLength = length;
		saveBuffer = buffer;
		midiPlay = true;
		saveRequest = "jingle" + midiPos + ".mid";
	}

	public static final synchronized Socket openSocket(int port) throws IOException {
		for (socketRequest = port; socketRequest != 0;) {
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}

		if (socket == null) {
			throw new IOException("could not open socket");
		}
		return socket;
	}

	public static final synchronized DataInputStream openUrl(String url) throws IOException {
		for (urlRequest = url; urlRequest != null;) {
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}

		if (urlStream == null) {
			throw new IOException("could not open: " + url);
		}
		return urlStream;
	}

	public static final void reportError(String error) {
		if (!reportError) {
			return;
		} else if (!active) {
			return;
		}

		System.out.println("Error: " + error);
	}

	public static final void startPriv(InetAddress address) {
		threadLiveId = (int) (Math.random() * 99999999D);
		if (active) {
			try {
				Thread.sleep(500L);
			} catch (Exception ex) {
			}
			active = false;
		}

		socketRequest = 0;
		threadRequest = null;
		dnsRequest = null;
		saveRequest = null;
		urlRequest = null;
		socketAddress = address;

		Thread thread = new Thread(new SignLink());
		thread.setDaemon(true);
		thread.start();

		while (!active) {
			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
	}

	public static final synchronized void startThread(Runnable runnable, int priority) {
		threadRequestPriority = priority;
		threadRequest = runnable;
	}

	public static final synchronized boolean waveReplay() {
		if (saveRequest != null) {
			return false;
		}

		saveBuffer = null;
		wavePlay = true;
		saveRequest = "sound" + wavePosition + ".wav";
		return true;
	}

	public static final synchronized boolean waveSave(byte[] buffer, int length) {
		if (length > 0x1e8480 || saveRequest != null) {
			return false;
		}

		wavePosition = (wavePosition + 1) % 5;
		saveLength = length;
		saveBuffer = buffer;
		wavePlay = true;
		saveRequest = "sound" + wavePosition + ".wav";
		return true;
	}

	private static final int getUid(String prefix) {
		try {
			File file = new File(prefix + "uid.dat");
			if (!file.exists() || file.length() < 4) {
				DataOutputStream os = new DataOutputStream(new FileOutputStream(prefix + "uid.dat"));
				os.writeInt((int) (Math.random() * 99999999));
				os.close();
			}
		} catch (Exception ex) {
		}

		try {
			DataInputStream is = new DataInputStream(new FileInputStream(prefix + "uid.dat"));
			int uid = is.readInt();
			is.close();
			return uid + 1;
		} catch (Exception ex) {
			return 0;
		}
	}

	@Override
	public final void run() {
		active = true;
		String directory = findCacheDirectory();
		uid = getUid(directory);

		try {
			File file = new File(directory + "main_file_cache.dat");
			if (file.exists() && file.length() > 0x3200000) {
				file.delete();
			}

			cache = new RandomAccessFile(directory + "main_file_cache.dat", "rw");
			for (int index = 0; index < 5; index++) {
				indices[index] = new RandomAccessFile(directory + "main_file_cache.idx" + index, "rw");
			}

		} catch (Exception exception) {
			exception.printStackTrace();
		}

		for (int id = threadLiveId; threadLiveId == id;) {
			if (socketRequest != 0) {
				try {
					socket = new Socket(socketAddress, socketRequest);
				} catch (Exception ex) {
					socket = null;
				}

				socketRequest = 0;
			} else if (threadRequest != null) {
				Thread thread = new Thread(threadRequest);
				thread.setDaemon(true);
				thread.start();
				thread.setPriority(threadRequestPriority);
				threadRequest = null;
			} else if (dnsRequest != null) {
				try {
					dns = InetAddress.getByName(dnsRequest).getHostName();
				} catch (Exception ex) {
					dns = "unknown";
				}

				dnsRequest = null;
			} else if (saveRequest != null) {
				if (saveBuffer != null) {
					try {
						FileOutputStream fos = new FileOutputStream(directory + saveRequest);
						fos.write(saveBuffer, 0, saveLength);
						fos.close();
					} catch (Exception ex) {
					}
				}

				if (wavePlay) {
					wave = directory + saveRequest;
					wavePlay = false;
				}

				if (midiPlay) {
					midi = directory + saveRequest;
					midiPlay = false;
				}

				saveRequest = null;
			} else if (urlRequest != null) {
				try {
					urlStream = new DataInputStream(new URL(applet.getCodeBase(), urlRequest).openStream());
				} catch (Exception ex) {
					urlStream = null;
				}
				urlRequest = null;
			}

			try {
				Thread.sleep(50L);
			} catch (Exception ex) {
			}
		}
	}

}
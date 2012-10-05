package org.scauhci.android.mentohust;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.stericson.RootTools.Command;
import com.stericson.RootTools.CommandCapture;
import com.stericson.RootTools.RootTools;

public class Mentohust {
	public static final String DIR = "/system/bin";
	public static final String NAME = "mentohust";
	public static final String PATH = DIR + "/" + NAME;
	public static final String LOG_PATH = "/data/local/tmp/mentohust.log";

	public static final String RUN_CMD = PATH
			+ " -u%s -p%s -n%s -s%s -l%s -a1 -d2 -v3.95 -b3";
	public static final String COPY_CMD = "cat %s/" + NAME + " >" + PATH;
	public static final String PERMIT_CMD = "chmod 777 " + PATH;
	public static final String STATUS_CMD = "ps " + NAME;
	public static final String KILL_CMD = "kill %s";
	public static final String NIC_CMD = "netcfg";
	public static final String RM_LOG_CMD = "rm " + LOG_PATH;
	public static final String SHOW_LOG_CMD = "cat " + LOG_PATH;

	public static final String EXITED_STATUS = "尚未运行";
	public static final String EXITED_FLAG = "exit";
	public static final String SUCCESS_STATUS = "成功连接";
	public static final String SUCCESS_FLAG = "Send heartbeat package to keep alive";
	public static final String RUN_STATUS = "连接中";
	/*public static final String NO_SERVER_ERROR = "No Server";
	public static final String NO_SERVER_FLAG = "Couldn't find the auth server";
	public static final String USER_ERROR = "User Error";
	public static final String USER_ERROR_FLAG = "Failed to auth!";
	public static final String TIMEOUT_ERROR = "Timeout";*/
	
	public static final int NONE = -1;

	public static Activity act = null;

	public static void kill() {
		try {
			if (getPid() != NONE) {
				String killCmd = String.format(KILL_CMD, getPid());
				CommandCapture cmd = new CommandCapture(0, killCmd);
				RootTools.getShell(true).add(cmd).waitForFinish();
			}
			rmLog();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void rmLog() {
		try {
			if (RootTools.exists(LOG_PATH)) {
			String rmLogCmd = RM_LOG_CMD;
			CommandCapture cmd = new CommandCapture(0, rmLogCmd);
			RootTools.getShell(true).add(cmd).waitForFinish();
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static int getPid() {
		int pid = NONE;
		try {
			List<String> results = RootTools.sendShell(STATUS_CMD, 0);
			int l = results.size();
			if (l >= 2) {
				String result = results.get(1);
				if(result.split("\\s+").length > 1){					
					pid = Integer.parseInt(result.split("\\s+")[1]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pid;
	}

	public static String getStatus() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					Mentohust.LOG_PATH), 3000);
			String result = "", tempResult;
			
			while ((tempResult = reader.readLine()) != null) {
				result = tempResult;
			}
			reader.close();

			Log.d("Test", result);
			if (result.contains(EXITED_FLAG))
				return EXITED_STATUS;
			if (result.contains(SUCCESS_FLAG))
				return SUCCESS_STATUS;
		} catch (FileNotFoundException e) {
			return EXITED_STATUS;
		} catch (Exception e) {
		};

		return RUN_STATUS;
	}

	public static void run(String user, String passwd, String nic, String dns,
			String failed) {
		if (act == null)
			return;

		try {
			kill();
			String runCmd = String.format(RUN_CMD, user, passwd, nic, dns,
					failed);
			Log.e("Test", runCmd);
			MentohustCommand cmd = new MentohustCommand(0, runCmd);
			RootTools.getShell(true).add(cmd).waitForFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void init(Activity newAct) {
		act = newAct;
		try {
			if (getPid() == NONE) {
				rmLog();
				copyAssets();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String[] getNics() {
		final List<String> nicList = new LinkedList<String>();
		String[] nics = null;

		try {
			String nicCmd = NIC_CMD;
			Command cmd = new MentohustCommand(0, nicCmd) {
				@Override
				public void output(int id, String line) {
					super.output(id, line);
					String nic = null;
					if (line.contains("UP")) {
						nic = line.substring(0, line.indexOf(" "));
						nicList.add(nic);
					}
				}
			};

			RootTools.getShell(true).add(cmd).waitForFinish();

			nics = new String[nicList.size()];
			for (int i = 0; i < nics.length; i++) {
				nics[i] = nicList.get(i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return nics;
	}

	private static void copyAssets() {
		try {
			AssetManager am = act.getAssets();
			InputStream in = am.open(NAME);
			OutputStream out = act.openFileOutput(NAME,
					Context.MODE_WORLD_READABLE);

			copyFile(in, out);
			in.close();
			in = null;
			out.flush();
			out.close();
			out = null;

			String copyCmd = String.format(COPY_CMD, act.getFilesDir());
			String permitCmd = PERMIT_CMD;
			CommandCapture cmd = new CommandCapture(0, copyCmd, permitCmd);
			RootTools.getShell(true).add(cmd).waitForFinish();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void copyFile(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while ((read = in.read(buffer)) != -1) {
			out.write(buffer, 0, read);
		}
	}
}

package org.scauhci.android.mentohust;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.stericson.RootTools.RootTools;

public class LoginActivity extends SherlockActivity {
	MentoStatusHandler statusHandler;
	MentoStatusUpdater statusUpdater;
	CheckBox mentoSwitch;
	TextView mentoLogView;
	Timer statusTimer;

	public static final String LOG_KEY = "log";

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(R.string.about_action)
				.setIcon(android.R.drawable.ic_menu_info_details)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
		menu.add(R.string.prefs_action)
				.setIcon(android.R.drawable.ic_menu_preferences)
				.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);

		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		String title = item.getTitle() + "";
		if (title.equals(getString(R.string.prefs_action))) {
			Intent intent = new Intent(this, PrefsActivity.class);
			startActivity(intent);
		} else if (title.equals(getString(R.string.about_action))) {
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}

		return true;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// RootTools.debugMode = true;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mentoSwitch = (CheckBox) findViewById(R.id.mentohust_switch);
		mentoLogView = (TextView) findViewById(R.id.mentohust_log);
		if (Mentohust.getPid() != Mentohust.NONE) {
			mentoSwitch.setChecked(true);
		}
		Mentohust.init(this);

		statusHandler = new MentoStatusHandler();
		statusTimer = new Timer();
		statusUpdater = new MentoStatusUpdater();
		statusTimer.schedule(statusUpdater, 0, 2000);
	}

	public class MentoStatusHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			String result = msg.getData().getString(LOG_KEY);
			mentoLogView.setText(result);
			if (result.equals(Mentohust.EXITED_STATUS)) {
				mentoSwitch.setChecked(false);
			} else {
				mentoSwitch.setChecked(true);
			}
		}
	}

	class MentoStatusUpdater extends TimerTask {
		@Override
		public void run() {
			Message msg = new Message();
			Bundle bun = new Bundle();
			bun.putString(LOG_KEY, Mentohust.getStatus());
			msg.setData(bun);

			statusHandler.sendMessage(msg);
		}
	}

	public void mentoSwitch(View target) {
		CheckBox controller = (CheckBox) target;
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(this);
		String user = sp.getString(getString(R.string.user_key_prefs), "");
		String passwd = sp.getString(getString(R.string.passwd_key_prefs), "");
		String nic = sp.getString(getString(R.string.nics_key_prefs), "");
		String dns = sp.getString(getString(R.string.dns_key_prefs),
				getString(R.string.dns_default_prefs));
		String failed = sp.getString(getString(R.string.failed_key_prefs),
				getString(R.string.failed_default_prefs));
		if (controller.isChecked()) {
			Mentohust.run(user, passwd, nic, dns, failed);
		} else {
			Mentohust.kill();
		}
	}
}

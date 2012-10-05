package org.scauhci.android.mentohust;

import java.util.Map;
import java.util.Set;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.actionbarsherlock.view.MenuItem;

public class PrefsActivity extends SherlockPreferenceActivity implements
		OnSharedPreferenceChangeListener {
	ListPreference nicsPref;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		finish();
		return true;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getSupportActionBar().setDisplayShowHomeEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		addPreferencesFromResource(R.xml.prefs);
		nicsPref = (ListPreference) findPreference(getString(R.string.nics_key_prefs));
	}
	
	public void initPrefSummaries(){
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
		Map<String, String> kv = (Map<String, String>) sp.getAll();
		Set<String> keys = kv.keySet();
		for (String key : keys) {
			Preference pref = findPreference(key);
			String value = kv.get(key);
			if (value.length() != 0) {
				if(!key.equals(getString(R.string.passwd_key_prefs))){					
					pref.setSummary(value);
				}else{
					pref.setSummary(getString(R.string.passwd_hide_prefs));
				}
			}
		}
	}

	public void initNicList() {
		String[] nics = Mentohust.getNics();
		
		nicsPref.setEntries(nics);
		nicsPref.setEntryValues(nics);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sp, String key) {
		Preference pref = findPreference(key);
		pref.setSummary(sp.getString(key, ""));
	}

	@Override
	protected void onResume() {
		super.onResume();
		initPrefSummaries();
		initNicList();
		getPreferenceScreen().getSharedPreferences()
				.registerOnSharedPreferenceChangeListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}
}

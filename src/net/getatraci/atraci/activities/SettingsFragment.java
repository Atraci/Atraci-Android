package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	SharedPreferences pref;
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		addPreferencesFromResource(R.xml.preferences);
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pref.registerOnSharedPreferenceChangeListener(this);
		return super.onCreateView(inflater, container, savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
		Log.d("ATRACI", "Preference changed! " + key);
		
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		Log.d("ATRACI", "Preference clicked!");
		return false;
	}
	
	
	
	

}

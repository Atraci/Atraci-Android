package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceManager;
import android.support.v4.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	SharedPreferences pref;
	Context mContext;

	public SettingsFragment(Context context) {
		mContext = context;
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		addPreferencesFromResource(R.xml.preferences);
		pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
		pref.registerOnSharedPreferenceChangeListener(this);

		Preference myPref = findPreference( "clear_history" );
		myPref.setOnPreferenceClickListener( new OnPreferenceClickListener()
		{
			public boolean onPreferenceClick( Preference pref )
			{
				HomeActivity.getDatabase().deleteHistory();
				Toast.makeText(mContext, mContext.getString(R.string.history_cleared), Toast.LENGTH_SHORT).show();
				return true;
			}
		} );

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

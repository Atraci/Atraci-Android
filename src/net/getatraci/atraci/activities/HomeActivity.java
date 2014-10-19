package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
import net.getatraci.atraci.data.DatabaseHelper;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * This Activity is the main activity loaded once the application opens.
 * Features a nav drawer and displays the news fragment by default.
 * @author Blake LaFleur
 *
 */

public class HomeActivity extends Activity implements OnItemClickListener{

	private String[] mNavigationDrawerItemTitles; 	 //Contains list of items in the Navigation Drawer
	private DrawerLayout mDrawerLayout;			 	 //Placeholder for navigation drawer
	private ListView mDrawerList;					 //View that holds the navigation drawer
	private ActionBarDrawerToggle mDrawerToggle;  	 //Toggle on App Icon to open nav drawer
	private static DatabaseHelper database;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);

		database = new DatabaseHelper(this.getApplicationContext());
		mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_item, mNavigationDrawerItemTitles));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.drawer_open, R.string.drawer_close) {

			/** Called when a drawer has settled in a completely closed state. */
			public void onDrawerClosed(View view) {
				super.onDrawerClosed(view);
			}

			/** Called when a drawer has settled in a completely open state. */
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
			}
		};

		// Set the drawer toggle as the DrawerListener
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// Set the click listener to the callbacks in this Activity
		mDrawerList.setOnItemClickListener(this);	
		// Make the AppIcon clickable
		getActionBar().setDisplayHomeAsUpEnabled(true);
		// Allow user to tap the appicon
		getActionBar().setHomeButtonEnabled(true);
		//Show the news fragment
		this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeNewsFragment()).commit();
	}

	/* Called after the activity has been created */
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();   // Sync the toggle state after onRestoreInstanceState has occurred.
	}

	/* Called whenever we call invalidateOptionsMenu() */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu); 
		return true;
	}
	
	/* Listener for navbar items like search and drawer */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		if (id == R.id.action_search) {
			openSearch();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/* Called when configuration has changed. Ex: when screen rotated */
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//Update navigation drawer to match the current config
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	/* Function that starts the Seaching activity */
	private void openSearch() {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	/* Listener for when user clicks an item in the Navigation Drawer */
	@Override
	public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
		switch(position) {
		case 0:		// Home item clicked
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeNewsFragment()).commit();
			getActionBar().setTitle(getResources().getString(R.string.app_name));
			break;
		case 1:		//Top Tracks item clicked
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment(database)).commit();
			getActionBar().setTitle(getResources().getString(R.string.top_tracks));
			break;
		case 2:		// Top 100 tracks item clicked
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment(database)).commit();
			getActionBar().setTitle(getResources().getString(R.string.top100));
			break;
		case 3:		// Playlists items clicked
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment(database)).commit();
			getActionBar().setTitle(getResources().getString(R.string.playlists));
			break;
		}
		// Set which item was selected in the nav drawer
		mDrawerList.setItemChecked(position, true);
		// Since an item was clicked, we want to move it out of the way and bring focus to user selection
		mDrawerLayout.closeDrawer(mDrawerList);

	}

	public static DatabaseHelper getDatabase() {
		return database;
	}
}

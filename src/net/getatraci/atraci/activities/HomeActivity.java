package net.getatraci.atraci.activities;

import net.getatraci.atraci.R;
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

public class HomeActivity extends Activity implements OnItemClickListener{

	private String[] mNavigationDrawerItemTitles;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home);
		
		mNavigationDrawerItemTitles= getResources().getStringArray(R.array.navigation_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavigationDrawerItemTitles));
		
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer icon to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description */
                R.string.drawer_close  /* "close drawer" description */
                ) {

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
        mDrawerList.setOnItemClickListener(this);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeNewsFragment()).commit();
	}
	
    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.home, menu);
		
		return true;
	}

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
	
	private void openSearch() {
		Intent i = new Intent(this, SearchActivity.class);
		startActivity(i);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		switch(arg2) {
		case 0:
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new HomeNewsFragment()).commit();
			getActionBar().setTitle(getResources().getString(R.string.app_name));
			break;
		case 1:
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment()).commit();
			getActionBar().setTitle(getResources().getString(R.string.top_tracks));
			break;
		case 2:
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment()).commit();
			getActionBar().setTitle(getResources().getString(R.string.top100));
			break;
		case 3:
			this.getFragmentManager().beginTransaction().replace(R.id.content_frame, new PlaylistSelectorFragment()).commit();
			getActionBar().setTitle(getResources().getString(R.string.playlists));
			break;
		}
		mDrawerList.setItemChecked(arg2, true);
		mDrawerLayout.closeDrawer(mDrawerList);
		
	}
}

package net.getatraci.atraci.loaders;

import java.util.ArrayList;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v13.app.FragmentPagerAdapter;


public class PagerFragmentAdapter extends FragmentPagerAdapter {
	
	private ArrayList<Fragment> fragments;
	private FragmentManager manager;

	public PagerFragmentAdapter(FragmentManager fm, ArrayList<Fragment> frags) {
		super(fm);
		fragments = frags;
		manager = fm;
	}

	@Override
	public Fragment getItem(int pos) {
		// TODO Auto-generated method stub
		return fragments.get(pos);
	}

	@Override
	public int getCount() {
		return 2;
	}
	
	public void setFragmentAtPos(int pos, Fragment frag){
		fragments.set(pos, frag);
		notifyDataSetChanged();
	}
	
	public void detach(Fragment frag) {
		final FragmentTransaction trans = manager.beginTransaction();
	    trans.hide(frag);
		trans.commit();
	}

	public void attach(Fragment frag) {
		final FragmentTransaction trans = manager.beginTransaction();
	    trans.show(frag);
		trans.commit();
	}


}

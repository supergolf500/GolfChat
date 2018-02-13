package com.supergolf500.golfchat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by supergolf500 on 10/11/2559.
 */

public class PagerAdapterMain extends FragmentPagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList();
    private final List<String> mFragmentTitleNames = new ArrayList();

    public PagerAdapterMain(FragmentManager fm) {
        super(fm);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleNames.add(title);
    }

    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
        //return mFragmentTitleNames.get(position);
    }
}
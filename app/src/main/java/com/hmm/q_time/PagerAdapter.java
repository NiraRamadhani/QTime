package com.hmm.q_time;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int numOfTabs) {
        super(fm);
        mNumOfTabs = numOfTabs;
    }

    @Override
    public Fragment getItem(int i) {
        switch (i){
            case 0:
                HomeFragment mHomeFragment = new HomeFragment();
                return mHomeFragment;
            case 1:
                QueueFragment mQueueFragment = new QueueFragment();
                return mQueueFragment;
            case 2:
                NearbyFragment mNearbyFragment = new NearbyFragment();
                return mNearbyFragment;
            case 3:
                MessageFragment mMessageFragment = new MessageFragment();
                return mMessageFragment;
            case 4:
                ProfileFragment mProfileFragment = new ProfileFragment();
                return mProfileFragment;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}

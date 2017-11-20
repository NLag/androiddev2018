package com.nlag.onlinemusicplayer;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.nlag.onlinemusicplayer.LocalComponents.LocalFragment;
import com.nlag.onlinemusicplayer.OnlineComponents.OnlineFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PagerAdapter mainAdapter = new MainFragmentPagerAdapter( getSupportFragmentManager());
        ViewPager mainPager = (ViewPager) findViewById(R.id.mainpager);
        mainPager.setOffscreenPageLimit(2);
        mainPager.setAdapter(mainAdapter);

    }

    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_COUNT = 2;
        private String titles[] = new String[] { "Online", "Local" };

        public MainFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        // number of pages for a ViewPager
        public Fragment getItem(int page) {
            // returns an instance of Fragment corresponding to the specified page
            switch (page) {
                case 0: return new OnlineFragment();
                case 1: return new LocalFragment();
            }
            return null; // failsafe
        }

        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }
    }


}

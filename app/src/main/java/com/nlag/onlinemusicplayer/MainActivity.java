package com.nlag.onlinemusicplayer;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

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

        TabLayout mainTabLayout = (TabLayout) findViewById(R.id.maintab);
        mainTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mainTabLayout.setupWithViewPager(mainPager);

        Toolbar mainToolbar = (Toolbar) findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_toolbar_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_refesh:

                return true;
            case R.id.action_search:
                // User chose the "Search" item, show the app search UI...
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}

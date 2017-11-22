package com.nlag.onlinemusicplayer.MusicLibraryActivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.nlag.onlinemusicplayer.R;

/**
 * Created by nlag on 11/21/17.
 */

public class MusicLibraryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_library);

        int pagenum;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                pagenum= 1;
            } else {
                pagenum= extras.getInt("page_num");
            }
        } else {
            pagenum= savedInstanceState.getInt("page_num");
        }

        Toolbar myToolbar = (Toolbar) findViewById(R.id.musiclib_toolbar);
        myToolbar.setTitle(R.string.music_lib);
        setSupportActionBar(myToolbar);
        ActionBar ab = getSupportActionBar();
        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        PagerAdapter musiclibAdapter = new MusicLibFragmentPagerAdapter( getSupportFragmentManager());
        ViewPager musiclibPager = (ViewPager) findViewById(R.id.musiclib_pager);
        musiclibPager.setOffscreenPageLimit(3);
        musiclibPager.setAdapter(musiclibAdapter);

        TabLayout musiclibTabLayout = (TabLayout) findViewById(R.id.musiclib_tab);
        musiclibTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        musiclibTabLayout.setupWithViewPager(musiclibPager);

        musiclibPager.setCurrentItem(pagenum);

    }



    public class MusicLibFragmentPagerAdapter extends FragmentPagerAdapter {

        private final int PAGE_COUNT = 3;
        private String titles[] = new String[] { "All Songs", "Recently Played", "Downloaded" };

        public MusicLibFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // returns an instance of Fragment corresponding to the specified page
            switch (position) {
                case 0: return new AllSongFragment();
                case 1: return new RecentFragment();
                case 2: return new DownloadedFragment();
            }
            return null; // failsafe
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.musiclib_activity_toolbar_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.action_refesh:

                return true;
            case R.id.action_search:
                // User chose the "Search" item, show the app search UI...
                return true;

            case R.id.action_others:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }
}

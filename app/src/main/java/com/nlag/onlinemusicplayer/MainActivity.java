package com.nlag.onlinemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController.MediaPlayerControl;
import android.widget.Toast;

import com.nlag.onlinemusicplayer.LocalComponents.AllSongFragment;
import com.nlag.onlinemusicplayer.MusicPlayerActivity.MusicController;
import com.nlag.onlinemusicplayer.MusicPlayerActivity.MusicService;
import com.nlag.onlinemusicplayer.OnlineComponents.OnlineFragment;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {
    public ViewPager mainPager;
    public MainFragmentPagerAdapter mainPagerAdapter;
    public TabLayout mainTabLayout;
    public Toolbar mainToolbar;
    public MusicController controller;
    //service
    private MusicService musicSrv;
    private Intent playIntent;
    private boolean musicBound = false;
    private ServiceConnection musicConnection;

    public ServiceConnection setNewServiceConn() {
        return (new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                MusicService.MusicBinder binder = (MusicService.MusicBinder) service;
                //get service
                musicSrv = binder.getService();
                //pass list
                if (mainPager.getCurrentItem() == 0) {
                    musicSrv.setList(((OnlineFragment) mainPagerAdapter.getRegisteredFragment(mainPager.getCurrentItem())).ranklist);
                    musicSrv.setAppQueue((MainAppQueue) getApplication());
                } else {
                    musicSrv.setList(((AllSongFragment) mainPagerAdapter.getRegisteredFragment(mainPager.getCurrentItem())).localSongsList);
                }
                musicBound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                musicBound = false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainPagerAdapter = new MainFragmentPagerAdapter( getSupportFragmentManager());
        mainPager = findViewById(R.id.mainViewPager);
        mainPager.setOffscreenPageLimit(2);
        mainPager.setAdapter(mainPagerAdapter);

        mainTabLayout = findViewById(R.id.mainTabLayout);
        mainTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mainTabLayout.setupWithViewPager(mainPager);

        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);

        setController();

    }

    private void setController() {
        //set the controller up
        controller = new MusicController(this);
        controller.setPrevNextListeners(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playNext();
            }
        }, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playPrev();
            }
        });
        controller.setMediaPlayer(this);
        controller.setAnchorView(findViewById(R.id.mainViewPager));
        controller.setEnabled(true);
    }

    //play next
    private void playNext() {
        musicSrv.playNext();
        controller.show(0);
    }

    //play previous
    private void playPrev() {
        musicSrv.playPrev();
        controller.show(0);
    }

    public void setNewService() {
        stopService(new Intent(getApplicationContext(), MusicService.class));
        unbindService(musicConnection);
        musicConnection = setNewServiceConn();
        bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
        startService(playIntent);
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        musicConnection = setNewServiceConn();
        if (playIntent == null) {
            playIntent = new Intent(getApplicationContext(), MusicService.class);
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE);
            startService(playIntent);
        }
    }

    //user song select
    public void songPicked(int position) {
        if (mainPager.getCurrentItem() == 0) {
            musicSrv.setList(((OnlineFragment) mainPagerAdapter.getRegisteredFragment(mainPager.getCurrentItem())).ranklist);
            musicSrv.setAppQueue((MainAppQueue) getApplication());
        } else {
            musicSrv.setList(((AllSongFragment) mainPagerAdapter.getRegisteredFragment(mainPager.getCurrentItem())).localSongsList);
        }
        musicSrv.setSong(position);
        musicSrv.playSong();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        // Configure the search info and add any event listeners...
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        Toast.makeText(MainActivity.this, query, Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
//                        Toast.makeText(MainActivity.this, newText, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
        );

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // User chose the "Search" item, show the app search UI...

                return true;
            case R.id.action_shuffle:

                return true;
            case R.id.action_end:
                stopService(playIntent);
                musicSrv = null;
                System.exit(0);
                return true;
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    protected void onDestroy() {
        stopService(playIntent);
        musicSrv = null;
        super.onDestroy();
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getDur();
        } else return 0;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return musicSrv.getPosn();
        } else return 0;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
    }

    @Override
    public boolean isPlaying() {
        if (musicSrv != null && musicBound) {
            return musicSrv.isPng();
        } else return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return true;
    }

    @Override
    public boolean canSeekBackward() {
        return true;
    }

    @Override
    public boolean canSeekForward() {
        return true;
    }

    @Override
    public int getAudioSessionId() {
        return 0;
    }

    public class MainFragmentPagerAdapter extends FragmentPagerAdapter {
        private final int PAGE_COUNT = 2;
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
        private String titles[] = new String[]{"Online", "Local"};

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
                case 0:
                    return new OnlineFragment();
                case 1:
                    return new AllSongFragment();
            }
            return null; // failsafe
        }

        @Override
        public CharSequence getPageTitle(int page) {
            // returns a tab title corresponding to the specified page
            return titles[page];
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }
    }

}

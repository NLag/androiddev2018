package com.nlag.onlinemusicplayer;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
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
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController.MediaPlayerControl;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.LocalComponents.AllSongFragment;
import com.nlag.onlinemusicplayer.MusicPlayerActivity.MusicController;
import com.nlag.onlinemusicplayer.MusicPlayerActivity.MusicService;
import com.nlag.onlinemusicplayer.OnlineComponents.OnlineFragment;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class MainActivity extends AppCompatActivity implements MediaPlayerControl {
    public ViewPager mainPager;
    public MainFragmentPagerAdapter mainPagerAdapter;
    public TabLayout mainTabLayout;
    public Toolbar mainToolbar;
    public MusicController controller;
    //service
    public MusicService musicSrv;
    public Intent playIntent;
    public boolean musicBound = false;
    public ServiceConnection musicConnection = new ServiceConnection() {
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
            musicSrv.player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                    controller.show();
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            musicBound = false;
        }
    };

    public boolean paused = false, playbackPaused = false;
    public int durtn = 0, postn = 0;
    public OnlineFragment onlineFragment;

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

    public void setController() {
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
    public void playNext() {
        musicSrv.playNext();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    //play previous
    public void playPrev() {
        musicSrv.playPrev();
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    //start and bind the service when the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        if (playIntent == null) {
            playIntent = new Intent(this, MusicService.class);
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
        if (playbackPaused) {
            setController();
            playbackPaused = false;
        }
        controller.show(0);
    }

    public void searchByCustomString(String url) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document mp3zing = Jsoup.parse(response);
                        Elements searchlist = mp3zing.getElementsByClass("item-song");
                        Log.i("SEARCHSIZE::", String.valueOf(searchlist.size()));
                        for (int i = 0; i < searchlist.size(); i++) {
                            Element itemElement = searchlist.get(i);
                            String itemsongkey = itemElement.attr("data-code");
                            Log.i("Itemdatacode::", itemsongkey);
                            String urlkey = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + itemsongkey;

                            StringRequest request = new StringRequest(urlkey,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONObject jsonData = new JSONObject(response);
                                                JSONObject songJsonObject = jsonData.getJSONObject("data");
                                                String name = songJsonObject.getString("name");
                                                Log.i("Itemname::", name);
                                                String artist = songJsonObject.getString("artists_names");
                                                String pageurl = "https://mp3.zing.vn" + songJsonObject.getString("link");
                                                String thumburl = songJsonObject.getString("thumbnail");
                                                String sourcelink = songJsonObject.getJSONObject("source")
                                                        .getString("128");
                                                final Song newsong = new Song(MainActivity.this, name, artist);
                                                newsong.setOnlineMusic(pageurl, thumburl, "");
                                                newsong.sourcelink = sourcelink;

                                                onlineFragment.ranklist.add(newsong);
                                                onlineFragment.music_rank_Adapter.notifyDataSetChanged();

                                                ImageRequest imageRequest = new ImageRequest(newsong.thumburl,
                                                        new Response.Listener<Bitmap>() {
                                                            @Override
                                                            public void onResponse(Bitmap response) {
                                                                newsong.thumb = response;
                                                                onlineFragment.music_rank_Adapter.notifyDataSetChanged();
                                                            }
                                                        },
                                                        0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                                                        new Response.ErrorListener() {
                                                            @Override
                                                            public void onErrorResponse(VolleyError error) {
                                                            }
                                                        }
                                                );
                                                ((MainAppQueue) getApplication()).getQueue().add(imageRequest);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                        }
                                    }
                            );
                            ((MainAppQueue) getApplication()).getQueue().add(request);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getApplication()).getQueue().add(stringRequest);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_actions, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Online Search Only");

        // Configure the search info and add any event listeners...
        searchView.setOnQueryTextListener(
                new SearchView.OnQueryTextListener() {

                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        String searchurl = "https://mp3.zing.vn/tim-kiem/bai-hat.html?q=" + query;
                        onlineFragment = (OnlineFragment) mainPagerAdapter.getRegisteredFragment(0);
                        onlineFragment.onlineTitle.setText("Search Result");
                        onlineFragment.ranklist.clear();
                        searchByCustomString(searchurl);
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
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
                musicSrv.setShuffle();
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
    protected void onPause() {
        super.onPause();
        paused = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            setController();
            paused = false;
        }
    }

    @Override
    protected void onStop() {
        controller.hide();
        super.onStop();
    }

    @Override
    public void start() {
        musicSrv.go();
    }

    @Override
    public void pause() {
        playbackPaused = true;
        musicSrv.pausePlayer();
    }

    @Override
    public int getDuration() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return durtn = musicSrv.getDur();
        }
//        else durtn = 0;
        return durtn;
    }

    @Override
    public int getCurrentPosition() {
        if (musicSrv != null && musicBound && musicSrv.isPng()) {
            return postn = musicSrv.getPosn();
        }
//        else return 0;
        return postn;
    }

    @Override
    public void seekTo(int pos) {
        musicSrv.seek(pos);
        postn = pos;
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
        public final int PAGE_COUNT = 2;
        public String titles[] = new String[]{"Online", "Local"};
        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

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

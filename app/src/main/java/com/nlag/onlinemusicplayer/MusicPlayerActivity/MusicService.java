package com.nlag.onlinemusicplayer.MusicPlayerActivity;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.Song;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by nlag on 11/24/17.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private final IBinder musicBind = new MusicBinder();
    MainAppQueue mainAppQueue;
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    public void setList(ArrayList<Song> theSongs) {
        songs = theSongs;
    }

    public void setAppQueue(MainAppQueue appQueue) {
        mainAppQueue = appQueue;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        player.stop();
        player.release();
        return false;
    }

    public void playSong() {
        //play a song
        player.reset();
        //get song
        Song playSong = songs.get(songPosn);
        if (playSong.onlinemusic == true) {
            getSongSourceAndPlay(playSong);
        } else {
            try {
                player.setDataSource(playSong.filepath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            player.prepareAsync();
        }

    }

    public void getSongSourceAndPlay(final Song playsong) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, playsong.pageurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document mp3zing = Jsoup.parse(response);
                        Element audioElement = mp3zing.getElementById("zplayerjs-wrapper");
                        String songKey = audioElement.attr("data-xml");
                        int keypivot = songKey.lastIndexOf("key=");
                        songKey = songKey.substring(keypivot + 4);
                        playsong.songKey = songKey;
                        fromKeyGetSongJson(playsong);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        mainAppQueue.getQueue().add(stringRequest);
    }

    public void fromKeyGetSongJson(final Song playsong) {
        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + playsong.songKey;
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        playsong.JsonData = response;
                        getSourceLinkAndPass(playsong);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        mainAppQueue.getQueue().add(request);
    }

    public void getSourceLinkAndPass(Song playsong) {
        try {
            JSONObject jsonObject = new JSONObject(playsong.JsonData);
            String sourceLink = jsonObject
                    .getJSONObject("data")
                    .getJSONObject("source")
                    .getString("128");
            sourceLink = "http:" + sourceLink;
            playsong.sourcelink = sourceLink;
            player.setDataSource(playsong.sourcelink);
            player.prepareAsync();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        //start playback
        mp.start();
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }


}


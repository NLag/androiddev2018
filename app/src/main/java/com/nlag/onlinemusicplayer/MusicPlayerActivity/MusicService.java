package com.nlag.onlinemusicplayer.MusicPlayerActivity;

import android.app.Notification;
import android.app.PendingIntent;
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
import com.nlag.onlinemusicplayer.MainActivity;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by nlag on 11/24/17.
 */

public class MusicService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private static final int NOTIFY_ID = 1;
    private final IBinder musicBind = new MusicBinder();
    MainAppQueue mainAppQueue;
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;
    private String songTitle = "";
    private boolean shuffle = false;
    private Random rand;

    public void setShuffle() {
        shuffle = !shuffle;
    }

    public void onCreate() {
        //create the service
        super.onCreate();
        //initialize position
        songPosn = 0;
        //create player
        player = new MediaPlayer();

        rand = new Random();

        initMusicPlayer();
    }

    public void initMusicPlayer() {
        //set player properties
        player.setWakeMode(this, PowerManager.PARTIAL_WAKE_LOCK);
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
        songTitle = playSong.name;
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

        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.play_button)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            not = builder.build();
        } else {
            not = builder.getNotification();
        }

        startForeground(NOTIFY_ID, not);
    }

    public void setSong(int songIndex) {
        songPosn = songIndex;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        //check if playback has reached the end of a track
        if (player.getCurrentPosition() > 0) {
            mp.reset();
            playNext();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    public int getPosn() {
        return player.getCurrentPosition();
    }

    public int getDur() {
        return player.getDuration();
    }

    public boolean isPng() {
        return player.isPlaying();
    }

    public void pausePlayer() {
        player.pause();
    }

    public void seek(int posn) {
        player.seekTo(posn);
    }

    public void go() {
        player.start();
    }

    public void playPrev() {
        songPosn--;
        if (songPosn < 0) songPosn = songs.size() - 1;
        playSong();
    }

    //skip to next
    public void playNext() {
        if (shuffle) {
            int newSong = songPosn;
            while (newSong == songPosn) {
                newSong = rand.nextInt(songs.size());
            }
            songPosn = newSong;
        } else {
            songPosn++;
            if (songPosn >= songs.size()) songPosn = 0;
        }
        playSong();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    public class MusicBinder extends Binder {
        public MusicService getService() {
            return MusicService.this;
        }
    }

}


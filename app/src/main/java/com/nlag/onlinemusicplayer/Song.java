package com.nlag.onlinemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by nlag on 11/23/17.
 */

public class Song {
    public String name;
    public String artist;
    public String performer;

    public boolean onlinemusic;
    public String pageurl;
    public String thumburl;
    public Bitmap thumb = null;
    public Drawable thumbsample;
    public String ranknum;
    public String songKey;
    public String JsonData;
    public String sourcelink;

    public String filepath;

    public Song(Context context, String name, String artist, String performer) {
        this.name = name;
        this.artist = context.getResources().getString(R.string.artist) + ": " + artist;
        this.performer = context.getResources().getString(R.string.performer) + ": " + performer;
//        this.thumb = ((BitmapDrawable) context.getDrawable(R.drawable.icon_song)).getBitmap();
        this.thumbsample = context.getResources().getDrawable(R.drawable.icon_song);
    }

    public void setOfflineMusic(String path) {
        this.onlinemusic = false;
        this.filepath = path;
    }

    public void setOnlineMusic(String pageurl, String thumburl, String ranknum) {
        this.onlinemusic = true;
        this.pageurl = pageurl;
        this.thumburl = thumburl;
        this.ranknum = ranknum;
    }
}

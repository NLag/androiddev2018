package com.nlag.onlinemusicplayer.OnlineComponents;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;

/**
 * Created by nlag on 11/21/17.
 */

public class OnlineMusicRankingSong {
    private String name;
    private String artist;
    private Bitmap thumb;

    public OnlineMusicRankingSong(String name, String artist, Bitmap thumb) {
        this.name = name;
        this.artist = artist;
        this.thumb = thumb;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Bitmap getThumb() {
        return thumb;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setThumb(Bitmap thumb) {
        this.thumb = thumb;
    }
}

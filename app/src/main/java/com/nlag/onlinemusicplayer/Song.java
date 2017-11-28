package com.nlag.onlinemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nlag on 11/23/17.
 */

public class Song {
    public String name = "";
    public String artist = "";

    public boolean onlinemusic = false;
    public String pageurl = "";
    public String thumburl = "";
    public Bitmap thumb = null;
    public String ranknum = "";
    public String songKey = "";
    public String JsonData = "";
    public String sourcelink = "";

    public String filepath = "";
    Context context;

    public Song(Context context, String name, String artist) {
        this.context = context;
        this.name = name;
        this.artist = artist;
        this.thumb = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.icon_sample_song_thumb));
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


    public void getSourceLinkFromJsonData(String jsonData) {
        try {
            JSONObject jsonObject = new JSONObject(JsonData);
            String sourceLinkJSO = jsonObject
                    .getJSONObject("data")
                    .getJSONObject("source")
                    .getString("128");
            sourceLinkJSO = "http:" + sourceLinkJSO;
            sourcelink = sourceLinkJSO;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

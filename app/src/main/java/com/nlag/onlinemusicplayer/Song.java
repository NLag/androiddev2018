package com.nlag.onlinemusicplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.OnlineComponents.MusicRankingAdapter;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by nlag on 11/23/17.
 */

public class Song {
    public long id = 0;
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
    MainAppQueue appQueue;
    MusicRankingAdapter adapter;

    public Song(Context context, String name, String artist) {
        this.context = context;
        this.name = name;
        this.artist = context.getResources().getString(R.string.artist) + ": " + artist;
        this.thumb = BitmapFactory.decodeStream(context.getResources().openRawResource(R.raw.icon_sample_song_thumb));
    }

    public void getSongThumbnail(String thumburl) {
        ImageRequest imageRequest = new ImageRequest(thumburl,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        thumb = response;
                        adapter.notifyDataSetChanged();
                    }
                },
                0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        appQueue.getQueue().add(imageRequest);
    }

    public void getSongKey(String pageurl) {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, pageurl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document mp3zing = Jsoup.parse(response);
                        Element audioElement = mp3zing.getElementById("zplayerjs-wrapper");
                        String songKeyXML = audioElement.attr("data-xml");
                        int keypivot = songKeyXML.lastIndexOf("key=");
                        songKeyXML = songKeyXML.substring(keypivot + 4);
                        songKey = songKeyXML;
                        getSongSource(songKey);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        appQueue.getQueue().add(stringRequest);
    }

    public void getSongSource(String songKey) {
        String url = "https://mp3.zing.vn/xhr/media/get-source?type=audio&key=" + songKey;
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonData = response;
                        getSourceLinkFromJsonData(JsonData);
                        adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        appQueue.getQueue().add(request);
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

    public void setOfflineMusic(long songID, String path) {
        this.onlinemusic = false;
        this.id = songID;
        this.filepath = path;
    }

    public void setOnlineMusic(MainAppQueue appQueue, MusicRankingAdapter adapter, String pageurl, String thumburl, String ranknum) {
        this.appQueue = appQueue;
        this.adapter = adapter;
        this.onlinemusic = true;
        this.pageurl = pageurl;
        this.thumburl = thumburl;
        this.ranknum = ranknum;
        getSongThumbnail(thumburl);
        getSongKey(pageurl);
    }
}

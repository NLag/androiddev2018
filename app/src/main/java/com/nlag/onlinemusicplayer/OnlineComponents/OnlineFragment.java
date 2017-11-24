package com.nlag.onlinemusicplayer.OnlineComponents;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;
import com.nlag.onlinemusicplayer.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Created by nlag on 11/20/17.
 */

public class OnlineFragment extends Fragment {
    public ArrayList<Song> ranklist;
    public ListView music_rank_ListView;
    public MusicRankingAdapter music_rank_Adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View onlineFragmentView = inflater.inflate(R.layout.fragment_online,container,false);

        //get Music Ranking json
        ranklist = new ArrayList<>();

        music_rank_Adapter = new MusicRankingAdapter(getContext(), ranklist);

        music_rank_ListView = onlineFragmentView.findViewById(R.id.music_rank_ListView);
        music_rank_ListView.setAdapter(music_rank_Adapter);

        music_rank_ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Song song = (Song) music_rank_ListView.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected: " + song.name , Toast.LENGTH_SHORT).show();

            }
        });
        getRankListFromZingMP3();

        return onlineFragmentView;
    }

    public void putSonginIntent(Intent intent, Song song) {
        intent.putExtra("title", song.name);
        intent.putExtra("artist", song.artist);
        intent.putExtra("onlinemusic", song.onlinemusic);
        intent.putExtra("sourcelink", song.sourcelink);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        song.thumb.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        intent.putExtra("thumb", byteArray);
        intent.putExtra("filepath", song.filepath);
    }

    public void getRankListFromZingMP3() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=20";
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("data")
                                    .getJSONArray("song");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObjectIter = jsonArray.getJSONObject(i);
                                parseJsonDataToArrayList(jsonObjectIter, Integer.toString(i + 1), i);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        music_rank_Adapter.notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }
        );
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(request);
    }

    public void parseJsonDataToArrayList(JSONObject songJsonObject, String ranknum, int position) {
        try {
            String name = songJsonObject.getString("name");
            String artist = songJsonObject.getString("artists_names");
            String pageurl = "https://mp3.zing.vn" + songJsonObject.getString("link");
            String thumburl = songJsonObject.getString("thumbnail");
            Song newsong = new Song(getContext(), name, artist);
            newsong.setOnlineMusic((MainAppQueue) getActivity().getApplication(), music_rank_Adapter, pageurl, thumburl, ranknum);
            ranklist.add(position, newsong);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



}

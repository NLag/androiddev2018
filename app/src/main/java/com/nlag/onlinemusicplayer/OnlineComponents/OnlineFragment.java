package com.nlag.onlinemusicplayer.OnlineComponents;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by nlag on 11/20/17.
 */

public class OnlineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View onlineFragmentView = inflater.inflate(R.layout.online_fragment,container,false);

        //get Music Ranking
        ArrayList<JSONObject> rank10songs = getRankFromZingMP3();

        return onlineFragmentView;
    }

    public ArrayList getRankFromZingMP3() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=10";
        final ArrayList<JSONObject> jsonSongsList = new ArrayList<>();
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i( "Json response ", response);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONObject("data")
                                    .getJSONArray("song");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonSongsList.add(jsonArray.getJSONObject(i));
                            }
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
        ((MainAppQueue) getActivity().getApplication()).getQueue().add(request);
        return jsonSongsList;
    }
}

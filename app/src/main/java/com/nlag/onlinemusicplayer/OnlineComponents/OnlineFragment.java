package com.nlag.onlinemusicplayer.OnlineComponents;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.nlag.onlinemusicplayer.MainAppQueue;
import com.nlag.onlinemusicplayer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nlag on 11/20/17.
 */

public class OnlineFragment extends Fragment {
    private class OnlineMusicRankingSong {
        public String name;
        public String artist;
        public String performer;
        public Bitmap thumb;

        public OnlineMusicRankingSong(String name, String artist, String performer, Bitmap thumb) {
            this.name = name;
            this.artist = artist;
            this.performer = performer;
            this.thumb = thumb;
        }
    }
    private ArrayList<OnlineMusicRankingSong> rank10songs = new ArrayList<>();
    private MusicRankingAdapter rankingAdapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        return super.onCreateView(inflater, container, savedInstanceState);
        View onlineFragmentView = inflater.inflate(R.layout.online_fragment,container,false);

        //get Music Ranking json
        getRankFromZingMP3();
        rankingAdapter = new MusicRankingAdapter(getContext(),rank10songs);

        final ListView musicrank_list = (ListView) onlineFragmentView.findViewById(R.id.musicrank_list);
        musicrank_list.setAdapter(rankingAdapter);

        musicrank_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnlineMusicRankingSong o = (OnlineMusicRankingSong) musicrank_list.getItemAtPosition(position);
                Toast.makeText(getContext(), "Selected: " + o.name , Toast.LENGTH_LONG).show();
            }
        });
        return onlineFragmentView;
    }

    private void getRankFromZingMP3() {
        String url = "https://mp3.zing.vn/xhr/chart-realtime?chart=song&time=-1&count=20";
        StringRequest request = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        parseDataToList(response);
                        rankingAdapter.notifyDataSetChanged();
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

    private void parseDataToList(String response) {
        Log.i( "Json response ", response);
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONObject("data")
                    .getJSONArray("song");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectiter = jsonArray.getJSONObject(i);
                final String name = jsonObjectiter.getString("name");
                final String artist = jsonObjectiter.getString("artists_names");
                final String performer = jsonObjectiter.getString("performer");
                String thumburl = jsonObjectiter.getString("thumbnail");
                Response.Listener<Bitmap> listener = new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        OnlineMusicRankingSong song = new OnlineMusicRankingSong(name,artist,performer, response);
                        rank10songs.add(song);
                        rankingAdapter.notifyDataSetChanged();
                    }
                };
                ImageRequest imageRequest = new ImageRequest(thumburl, listener, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.ARGB_8888,null);
                ((MainAppQueue) getActivity().getApplication()).getQueue().add(imageRequest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    public class MusicRankingAdapter extends BaseAdapter {

        private ArrayList<OnlineMusicRankingSong> rankingsongList;
        private LayoutInflater layoutInflater;
        private Context context;

        public MusicRankingAdapter(Context context, ArrayList<OnlineMusicRankingSong> rankingsongList) {
            this.context = context;
            this.layoutInflater = LayoutInflater.from(context);
            this.rankingsongList = rankingsongList;
        }

        @Override
        public int getCount() {
            return rankingsongList.size();
        }

        @Override
        public Object getItem(int position) {
            return rankingsongList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.online_musicranking_list_item, null);
                Log.i("item view inflate", Integer.toString(position));
                holder = new ViewHolder();
                holder.thumbnail = (ImageView) convertView.findViewById(R.id.musicrank_item_thumb);
                holder.name = (TextView) convertView.findViewById(R.id.musicrank_item_name);
                holder.artist = (TextView) convertView.findViewById(R.id.musicrank_item_artist);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            OnlineMusicRankingSong song = this.rankingsongList.get(position);
            holder.name.setText(song.name);
            holder.artist.setText(song.artist);
            holder.thumbnail.setImageBitmap(song.thumb);

            return convertView;
        }
        class ViewHolder {
            ImageView thumbnail;
            TextView name;
            TextView artist;
        }
    }


}
